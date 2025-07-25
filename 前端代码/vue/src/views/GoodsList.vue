<template>
  <div class="goods-list-page">
    <h2 class="page-title">全部商品</h2>
    
    <!-- 搜索和筛选区域 -->
    <div class="search-filter-section">
      <div class="search-box">
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="搜索商品名称..." 
          class="search-input"
          @input="handleSearch"
        />
        <button @click="handleSearch" class="search-btn">搜索</button>
      </div>
      
      <div class="filter-section">
        <select v-model="selectedType" @change="handleFilter" class="filter-select">
          <option value="">全部类型</option>
          <option value="电子产品">电子产品</option>
          <option value="服装">服装</option>
          <option value="食品">食品</option>
          <option value="图书">图书</option>
          <option value="家居">家居</option>
          <option value="运动">运动</option>
        </select>
        
        <div class="price-filter">
          <input 
            v-model="minPrice" 
            type="number" 
            placeholder="最低价" 
            class="price-input"
            @input="handleFilter"
          />
          <span>-</span>
          <input 
            v-model="maxPrice" 
            type="number" 
            placeholder="最高价" 
            class="price-input"
            @input="handleFilter"
          />
        </div>
        
        <button @click="resetFilter" class="reset-btn">重置筛选</button>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-spinner"></div>
    
    <!-- 商品列表 -->
    <div v-else class="goods-list">
      <div class="goods-card" v-for="goods in goodsList" :key="goods.goodsId">
        <img :src="goods.images && goods.images[0] ? goods.images[0] : '/default-goods.jpg'" class="goods-image" />
        <h3>{{ goods.goodsName }}</h3>
        <p>类型: {{ goods.type }}</p>
        <p class="price">￥{{ goods.price }}</p>
        <div class="actions">
          <button @click="viewDetail(goods.goodsId)">查看详情</button>
          <button @click="addToCart(goods)">加入购物车</button>
        </div>
      </div>
    </div>
    
    <!-- 无商品提示 -->
    <div v-if="!loading && goodsList.length === 0" class="no-goods">
      <div class="no-goods-content">
        <div class="no-goods-icon">🔍</div>
        <p>没有找到相关商品</p>
        <button @click="resetFilter" class="main-btn">查看全部商品</button>
      </div>
    </div>
    
    <!-- 分页控件 -->
    <div class="pagination" v-if="total > pageSize">
      <button :disabled="page === 1" @click="changePage(page - 1)">上一页</button>
      <span>第 {{ page }} / {{ totalPages }} 页</span>
      <button :disabled="page === totalPages" @click="changePage(page + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { goodsAPI, cartAPI } from '../api/index.js'
import Message from '../utils/message'

const router = useRouter()
const goodsList = ref([])
const loading = ref(false)
const searchQuery = ref('')
const selectedType = ref('')
const minPrice = ref('')
const maxPrice = ref('')
const page = ref(1)
const pageSize = 10
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / pageSize))

const loadGoods = async () => {
  loading.value = true
  try {
    let response
    
    // 根据筛选条件调用不同的API
    if (searchQuery.value) {
      // 按名称搜索
      response = await goodsAPI.getGoodsByNameLikePaged(searchQuery.value, page.value, pageSize)
    } else if (selectedType.value) {
      // 按类型筛选
      response = await goodsAPI.getGoodsByTypePaged(selectedType.value, page.value, pageSize)
    } else if (minPrice.value || maxPrice.value) {
      // 按价格范围筛选
      const min = minPrice.value ? parseFloat(minPrice.value) : 0
      const max = maxPrice.value ? parseFloat(maxPrice.value) : 999999
      response = await goodsAPI.getGoodsByPriceRangePaged(min, max, page.value, pageSize)
    } else {
      // 获取所有商品
      response = await goodsAPI.getAllGoodsPaged(page.value, pageSize)
    }
    
    if (response.code === 200) {
      goodsList.value = response.data.data || []
      total.value = response.data.total || 0
    } else {
      Message.error(response.msg || '获取商品失败')
    }
  } catch (error) {
    Message.error('获取商品失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadGoods()
}

const handleFilter = () => {
  page.value = 1
  loadGoods()
}

const resetFilter = () => {
  searchQuery.value = ''
  selectedType.value = ''
  minPrice.value = ''
  maxPrice.value = ''
  page.value = 1
  loadGoods()
}

const changePage = (newPage) => {
  if (newPage < 1 || newPage > totalPages.value) return
  page.value = newPage
  loadGoods()
}

const viewDetail = (goodsId) => {
  router.push(`/goods/${goodsId}`)
}

const addToCart = async (goods) => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.buyerId) {
    Message.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const response = await cartAPI.addToCart(userInfo.buyerId, goods.goodsId, 1)
    if (response.code === 200) {
      Message.success('已添加到购物车')
    } else {
      Message.error(response.msg || '添加失败')
    }
  } catch (error) {
    Message.error('添加失败')
  }
}

onMounted(loadGoods)
</script>

<style scoped>
.goods-list-page {
  width: 90vw;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  background: transparent;
  box-sizing: border-box;
}
.page-title {
  font-size: 1.6em;
  color: #222;
  margin-bottom: 18px;
  text-align: left;
}
.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
  flex: 1;
  width: 100%;
}
.goods-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
  padding: 18px 14px 20px 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: box-shadow 0.2s, transform 0.2s;
  min-height: 320px;
}
.goods-card:hover {
  box-shadow: 0 6px 18px rgba(64, 158, 255, 0.15);
  transform: translateY(-4px) scale(1.03);
}
.goods-image {
  width: 100%;
  max-width: 200px;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 12px;
}
.goods-card h3 {
  font-size: 1.1em;
  margin: 8px 0 4px 0;
  color: #222;
  text-align: center;
}
.goods-card p {
  margin: 2px 0;
  color: #666;
  font-size: 0.98em;
}
.price {
  color: #e74c3c;
  font-size: 1.15em;
  font-weight: bold;
  margin-bottom: 8px;
}
.actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}
.actions button {
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 5px;
  padding: 7px 16px;
  font-size: 0.98em;
  cursor: pointer;
  transition: background 0.2s;
}
.actions button:hover {
  background: #3076c9;
}
@media (max-width: 900px) {
  .goods-list {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
  .goods-image {
    max-width: 140px;
    height: 110px;
  }
}
@media (max-width: 600px) {
  .goods-list-page {
    padding: 10px;
  }
  .goods-list {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }
  .goods-card {
    padding: 10px 4px 12px 4px;
    min-height: 180px;
  }
  .goods-image {
    max-width: 100%;
    height: 80px;
  }
  .page-title {
    font-size: 1.2em;
  }
}

.search-filter-section {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
  padding: 20px;
  margin-bottom: 24px;
}

.search-box {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.search-input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1em;
  outline: none;
  transition: border 0.2s;
}

.search-input:focus {
  border-color: #409eff;
}

.search-btn {
  padding: 10px 20px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.search-btn:hover {
  background: #3076c9;
}

.filter-section {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1em;
  outline: none;
  transition: border 0.2s;
}

.filter-select:focus {
  border-color: #409eff;
}

.price-filter {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-input {
  width: 100px;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1em;
  outline: none;
  transition: border 0.2s;
}

.price-input:focus {
  border-color: #409eff;
}

.reset-btn {
  padding: 8px 16px;
  background: #909399;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.reset-btn:hover {
  background: #606266;
}

.no-goods {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.no-goods-content {
  text-align: center;
}

.no-goods-icon {
  font-size: 3em;
  margin-bottom: 10px;
}

.main-btn {
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 10px 20px;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s;
}

.main-btn:hover {
  background: #3076c9;
}

.loading-spinner {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.loading-spinner::before {
  content: '';
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 10px;
}

.pagination button {
  padding: 8px 16px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.pagination button:hover {
  background: #3076c9;
}

.pagination button:disabled {
  background: #909399;
  cursor: not-allowed;
}

.pagination span {
  margin: 0 16px;
  font-size: 1em;
  color: #666;
}
</style> 