<template>
  <div class="goods-detail">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <div v-if="goods" class="detail-content">
            <el-row :gutter="40">
              <el-col :span="12">
                <div class="goods-image-container">
                  <img :src="goods.goodsImage || '/default-goods.jpg'" class="goods-image">
                </div>
              </el-col>
              <el-col :span="12">
                <div class="goods-info">
                  <h1>{{ goods.goodsName }}</h1>
                  <p class="goods-type">类型: {{ goods.type }}</p>
                  <p class="goods-stock">库存: {{ goods.num }} 件</p>
                  <p class="price">¥{{ goods.price }}</p>
                  
                  <div class="quantity-selector">
                    <span>购买数量:</span>
                    <el-input-number
                      v-model="quantity"
                      :min="1"
                      :max="goods.num"
                      size="large"
                    />
                  </div>
                  
                  <div class="actions">
                    <el-button 
                      type="primary" 
                      size="large" 
                      @click="addToCart"
                      :disabled="goods.num <= 0"
                    >
                      加入购物车
                    </el-button>
                    <el-button 
                      type="success" 
                      size="large" 
                      @click="buyNow"
                      :disabled="goods.num <= 0"
                    >
                      立即购买
                    </el-button>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          
          <div v-else-if="loading" class="loading">
            <el-skeleton :rows="10" animated />
          </div>
          
          <div v-else class="not-found">
            <el-empty description="商品不存在">
              <el-button type="primary" @click="$router.push('/goods')">
                返回商品列表
              </el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { goodsAPI, cartAPI } from '../api'
import { ElMessage } from 'element-plus'

export default {
  name: 'GoodsDetail',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const goods = ref(null)
    const loading = ref(true)
    const quantity = ref(1)
    
    const loadGoodsDetail = async () => {
      try {
        loading.value = true
        const response = await goodsAPI.getGoodsById(route.params.id)
        if (response.code === 200) {
          goods.value = response.data
        } else {
          goods.value = null
        }
      } catch (error) {
        console.error('加载商品详情失败:', error)
        ElMessage.error('加载商品详情失败')
        goods.value = null
      } finally {
        loading.value = false
      }
    }
    
    const addToCart = async () => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (!userInfo.buyerId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }
      
      if (goods.value.num <= 0) {
        ElMessage.warning('商品库存不足')
        return
      }
      
      try {
        const response = await cartAPI.addToCart(userInfo.buyerId, goods.value.goodsId, quantity.value)
        if (response.code === 200) {
          ElMessage.success('已添加到购物车')
        } else {
          ElMessage.error(response.msg || '添加失败')
        }
      } catch (error) {
        ElMessage.error('添加失败')
      }
    }
    
    const buyNow = () => {
      ElMessage.info('购买功能开发中...')
      // 这里可以跳转到结算页面
      // router.push('/checkout')
    }
    
    onMounted(() => {
      loadGoodsDetail()
    })
    
    return {
      goods,
      loading,
      quantity,
      addToCart,
      buyNow
    }
  }
}
</script>

<style scoped>
.goods-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.detail-content {
  padding: 20px 0;
}

.goods-image-container {
  text-align: center;
}

.goods-image {
  width: 100%;
  max-width: 400px;
  height: 400px;
  object-fit: cover;
  border-radius: 8px;
}

.goods-info h1 {
  margin: 0 0 20px 0;
  color: #333;
}

.goods-type {
  color: #666;
  font-size: 1.1em;
  margin: 10px 0;
}

.goods-stock {
  color: #999;
  font-size: 1em;
  margin: 10px 0;
}

.price {
  color: #e74c3c;
  font-size: 2em;
  font-weight: bold;
  margin: 20px 0;
}

.quantity-selector {
  margin: 20px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-selector span {
  font-size: 1.1em;
  color: #333;
}

.actions {
  margin-top: 30px;
  display: flex;
  gap: 15px;
}

.actions .el-button {
  flex: 1;
}

.loading {
  padding: 40px;
}

.not-found {
  padding: 40px;
  text-align: center;
}

@media (max-width: 768px) {
  .detail-content .el-row {
    flex-direction: column;
  }
  
  .actions {
    flex-direction: column;
  }
}
</style> 