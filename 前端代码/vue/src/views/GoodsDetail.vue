<template>
  <div class="goods-detail-page">
    <div v-if="goods" class="detail-content">
      <div class="goods-image-section">
        <div class="carousel-wrapper" v-if="goods.images && goods.images.length">
          <img :src="goods.images[carouselIndex] || '/public/default-goods.jpg'" class="goods-image" />
          <button v-if="goods.images.length > 1" class="carousel-btn left" @click="prevImage">&#8592;</button>
          <button v-if="goods.images.length > 1" class="carousel-btn right" @click="nextImage">&#8594;</button>
          <div v-if="goods.images.length > 1" class="carousel-dots">
            <span v-for="(img, idx) in goods.images" :key="idx" :class="['dot', { active: idx === carouselIndex }]" @click="goToImage(idx)"></span>
          </div>
        </div>
        <div v-else>
          <img src="/public/default-goods.jpg" class="goods-image" />
        </div>
      </div>
      
      <div class="goods-info-section">
        <h1 class="goods-title">{{ goods.goodsName }}</h1>
        <div class="goods-meta">
          <span class="goods-type">类型: {{ goods.type }}</span>
          <span class="goods-stock">库存: {{ goods.num }} 件</span>
        </div>
        <div class="goods-price">￥{{ goods.price }}</div>
        
        <div class="quantity-section">
          <label>购买数量:</label>
          <div class="quantity-controls">
            <button @click="decreaseQuantity" :disabled="quantity <= 1" class="quantity-btn">-</button>
            <span class="quantity-display">{{ quantity }}</span>
            <button @click="increaseQuantity" :disabled="quantity >= goods.num" class="quantity-btn">+</button>
          </div>
        </div>
        
        <div class="action-buttons">
          <button 
            @click="addToCart" 
            :disabled="goods.num <= 0" 
            class="action-btn primary"
          >
            加入购物车
          </button>
          <button 
            @click="buyNow" 
            :disabled="goods.num <= 0" 
            class="action-btn success"
          >
            立即购买
          </button>
        </div>
      </div>
    </div>
    
    <div v-else-if="loading" class="loading-section">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
    
    <div v-else class="not-found-section">
      <div class="not-found-content">
        <div class="not-found-icon">❌</div>
        <h2>商品不存在</h2>
        <p>抱歉，您访问的商品不存在或已被删除</p>
        <button @click="$router.push('/goods')" class="back-btn">返回商品列表</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { goodsAPI, cartAPI, orderAPI } from '../api'
import Message from '../utils/message'

const route = useRoute()
const router = useRouter()
const goods = ref(null)
const loading = ref(true)
const quantity = ref(1)
const carouselIndex = ref(0)

const loadGoodsDetail = async () => {
  try {
    loading.value = true
    const response = await goodsAPI.getGoodsById(route.params.id)
    if (response.code === 200) {
      goods.value = response.data
      carouselIndex.value = 0
    } else {
      goods.value = null
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
    Message.error('加载商品详情失败')
    goods.value = null
  } finally {
    loading.value = false
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const increaseQuantity = () => {
  if (goods.value && quantity.value < goods.value.num) {
    quantity.value++
  }
}

const addToCart = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.buyerId) {
    Message.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (goods.value.num <= 0) {
    Message.warning('商品库存不足')
    return
  }
  
  try {
    const response = await cartAPI.addToCart(userInfo.buyerId, goods.value.goodsId, quantity.value)
    if (response.code === 200) {
      Message.success('已添加到购物车')
    } else {
      Message.error(response.msg || '添加失败')
    }
  } catch (error) {
    Message.error('添加失败')
  }
}

const buyNow = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.buyerId) {
    Message.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (goods.value.num < quantity.value) {
    Message.warning('商品库存不足')
    return
  }
  
  // 创建订单数据
  const orderData = {
    buyerId: userInfo.buyerId,
    sellerId: goods.value.sellerId,
    goodsId: goods.value.goodsId,
    num: quantity.value
  }
  
  try {
    const response = await orderAPI.createOrder(orderData)
    if (response.code === 200) {
      Message.success('订单已创建，请尽快支付')
      router.push('/orders')
    } else {
      Message.error(response.msg || '订单创建失败')
    }
  } catch (error) {
    Message.error('订单创建失败')
  }
}

const prevImage = () => {
  if (!goods.value || !goods.value.images) return
  carouselIndex.value = (carouselIndex.value - 1 + goods.value.images.length) % goods.value.images.length
}
const nextImage = () => {
  if (!goods.value || !goods.value.images) return
  carouselIndex.value = (carouselIndex.value + 1) % goods.value.images.length
}
const goToImage = (idx) => {
  carouselIndex.value = idx
}

onMounted(() => {
  loadGoodsDetail()
})
</script>

<style scoped>
.goods-detail-page {
  width: 90vw;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 60px);
  background: transparent;
  box-sizing: border-box;
}

.detail-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
  padding: 30px;
}

.goods-image-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.goods-image {
  width: 100%;
  max-width: 400px;
  height: 400px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.carousel-wrapper {
  position: relative;
  width: 100%;
  max-width: 400px;
  height: 400px;
  margin: 0 auto;
}
.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0,0,0,0.3);
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  font-size: 1.5em;
  cursor: pointer;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
}
.carousel-btn.left {
  left: 10px;
}
.carousel-btn.right {
  right: 10px;
}
.carousel-dots {
  position: absolute;
  bottom: 12px;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 8px;
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #ccc;
  display: inline-block;
  cursor: pointer;
  transition: background 0.2s;
}
.dot.active {
  background: #409eff;
}

.goods-info-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.goods-title {
  font-size: 2em;
  color: #333;
  margin: 0;
  line-height: 1.2;
}

.goods-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 1.1em;
}

.goods-price {
  font-size: 2.5em;
  font-weight: bold;
  color: #e74c3c;
  margin: 10px 0;
}

.quantity-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.quantity-section label {
  font-weight: bold;
  color: #333;
}

.quantity-controls {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 6px;
  overflow: hidden;
}

.quantity-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: #f5f5f5;
  cursor: pointer;
  font-size: 1.2em;
  transition: background 0.2s;
}

.quantity-btn:hover:not(:disabled) {
  background: #e0e0e0;
}

.quantity-btn:disabled {
  background: #f0f0f0;
  color: #ccc;
  cursor: not-allowed;
}

.quantity-display {
  width: 60px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  font-weight: bold;
  border-left: 1px solid #ddd;
  border-right: 1px solid #ddd;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.action-btn {
  flex: 1;
  padding: 15px 30px;
  border: none;
  border-radius: 8px;
  font-size: 1.1em;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.primary {
  background: #409eff;
  color: #fff;
}

.action-btn.primary:hover:not(:disabled) {
  background: #3076c9;
}

.action-btn.success {
  background: #67c23a;
  color: #fff;
}

.action-btn.success:hover:not(:disabled) {
  background: #5daf34;
}

.action-btn:disabled {
  background: #ccc;
  color: #999;
  cursor: not-allowed;
}

.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.not-found-section {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
}

.not-found-content {
  text-align: center;
}

.not-found-icon {
  font-size: 4em;
  margin-bottom: 20px;
}

.not-found-content h2 {
  color: #333;
  margin-bottom: 10px;
}

.not-found-content p {
  color: #666;
  margin-bottom: 20px;
}

.back-btn {
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 10px 20px;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s;
}

.back-btn:hover {
  background: #3076c9;
}

@media (max-width: 768px) {
  .detail-content {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
  
  .goods-image {
    max-width: 100%;
    height: 300px;
  }
  
  .goods-title {
    font-size: 1.5em;
  }
  
  .goods-price {
    font-size: 2em;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .goods-meta {
    flex-direction: column;
    gap: 10px;
  }
}
</style> 