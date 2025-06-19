<template>
  <div class="goods-list">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="search-header">
              <h2>商品列表</h2>
              <div class="search-form">
                <el-input
                  v-model="searchForm.goodsName"
                  placeholder="搜索商品名称"
                  style="width: 200px; margin-right: 10px;"
                  clearable
                  @keyup.enter="handleSearch"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
                <el-select
                  v-model="searchForm.type"
                  placeholder="商品类型"
                  style="width: 150px; margin-right: 10px;"
                  clearable
                >
                  <el-option label="电子产品" value="电子产品" />
                  <el-option label="服装" value="服装" />
                  <el-option label="食品" value="食品" />
                  <el-option label="图书" value="图书" />
                </el-select>
                <el-input-number
                  v-model="searchForm.minPrice"
                  placeholder="最低价格"
                  style="width: 120px; margin-right: 10px;"
                  :min="0"
                />
                <el-input-number
                  v-model="searchForm.maxPrice"
                  placeholder="最高价格"
                  style="width: 120px; margin-right: 10px;"
                  :min="0"
                />
                <el-button type="primary" @click="handleSearch">
                  <el-icon><Search /></el-icon>
                  搜索
                </el-button>
                <el-button @click="resetSearch">重置</el-button>
              </div>
            </div>
          </template>
          
          <el-row :gutter="20">
            <el-col :span="6" v-for="goods in goodsList" :key="goods.goodsId">
              <el-card :body-style="{ padding: '0px' }" class="goods-card">
                <img :src="goods.goodsImage || '/default-goods.jpg'" class="goods-image">
                <div style="padding: 14px;">
                  <h3>{{ goods.goodsName }}</h3>
                  <p class="goods-type">类型: {{ goods.type }}</p>
                  <p class="goods-stock">库存: {{ goods.num }}</p>
                  <p class="price">¥{{ goods.price }}</p>
                  <div class="bottom">
                    <el-button type="primary" size="small" @click="viewDetail(goods.goodsId)">
                      查看详情
                    </el-button>
                    <el-button 
                      type="success" 
                      size="small" 
                      @click="addToCart(goods)"
                      :disabled="goods.num <= 0"
                    >
                      加入购物车
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[8, 16, 24, 32]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { goodsAPI, cartAPI } from '../api'
import { ElMessage } from 'element-plus'

export default {
  name: 'GoodsList',
  setup() {
    const router = useRouter()
    const goodsList = ref([])
    const currentPage = ref(1)
    const pageSize = ref(8)
    const total = ref(0)
    const loading = ref(false)
    
    const searchForm = reactive({
      goodsName: '',
      type: '',
      minPrice: null,
      maxPrice: null
    })
    
    const loadGoods = async () => {
      try {
        loading.value = true
        let response
        
        if (searchForm.goodsName) {
          response = await goodsAPI.getGoodsByNameLike(searchForm.goodsName)
        } else if (searchForm.type) {
          response = await goodsAPI.getGoodsByType(searchForm.type)
        } else if (searchForm.minPrice || searchForm.maxPrice) {
          response = await goodsAPI.getGoodsByPriceRange(
            searchForm.minPrice || 0,
            searchForm.maxPrice || 999999
          )
        } else {
          response = await goodsAPI.getAllGoods()
        }
        
        if (response.code === 200) {
          goodsList.value = response.data
          total.value = response.data.length
        }
      } catch (error) {
        console.error('加载商品失败:', error)
        ElMessage.error('加载商品失败')
      } finally {
        loading.value = false
      }
    }
    
    const handleSearch = () => {
      currentPage.value = 1
      loadGoods()
    }
    
    const resetSearch = () => {
      Object.keys(searchForm).forEach(key => {
        searchForm[key] = key === 'goodsName' || key === 'type' ? '' : null
      })
      currentPage.value = 1
      loadGoods()
    }
    
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
    }
    
    const handleCurrentChange = (val) => {
      currentPage.value = val
    }
    
    const viewDetail = (goodsId) => {
      router.push(`/goods/${goodsId}`)
    }
    
    const addToCart = async (goods) => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (!userInfo.buyerId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }
      
      if (goods.num <= 0) {
        ElMessage.warning('商品库存不足')
        return
      }
      
      try {
        const response = await cartAPI.addToCart(userInfo.buyerId, goods.goodsId, 1)
        if (response.code === 200) {
          ElMessage.success('已添加到购物车')
        } else {
          ElMessage.error(response.msg || '添加失败')
        }
      } catch (error) {
        ElMessage.error('添加失败')
      }
    }
    
    onMounted(() => {
      loadGoods()
    })
    
    return {
      goodsList,
      currentPage,
      pageSize,
      total,
      loading,
      searchForm,
      loadGoods,
      handleSearch,
      resetSearch,
      handleSizeChange,
      handleCurrentChange,
      viewDetail,
      addToCart
    }
  }
}
</script>

<style scoped>
.goods-list {
  max-width: 1200px;
  margin: 0 auto;
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}

.search-header h2 {
  margin: 0;
}

.search-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.goods-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.goods-card:hover {
  transform: translateY(-5px);
}

.goods-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.goods-type {
  color: #666;
  font-size: 0.9em;
}

.goods-stock {
  color: #999;
  font-size: 0.9em;
}

.price {
  color: #e74c3c;
  font-size: 1.2em;
  font-weight: bold;
  margin: 10px 0;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
  display: flex;
  justify-content: space-between;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .search-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-form {
    margin-top: 10px;
    width: 100%;
  }
}
</style> 