<template>
  <div class="cart-page">
    <h2 class="page-title">我的购物车</h2>
    <div v-if="!cartItems.length" class="empty-cart">
      <div class="empty-content">
        <div class="empty-icon">🛒</div>
        <p>购物车是空的</p>
        <button class="main-btn" @click="$router.push('/goods')">去购物</button>
      </div>
    </div>
    <div v-else class="cart-list">
      <div class="cart-card" v-for="item in cartItems" :key="item.goodsId">
        <img :src="item.goodsImage || '/default-goods.jpg'" class="cart-image" />
        <div class="cart-info">
          <h3>{{ item.goodsName }}</h3>
          <p>类型: {{ item.type }}</p>
          <p>单价: ￥{{ item.price }}</p>
          <div class="cart-actions">
            <button @click="updateQuantity(item, item.num - 1)" :disabled="item.num <= 1">-</button>
            <span class="cart-num">{{ item.num }}</span>
            <button @click="updateQuantity(item, item.num + 1)">+</button>
            <button class="remove-btn" @click="removeItem(item)">删除</button>
          </div>
        </div>
        <div class="cart-sum">小计: ￥{{ (item.price * item.num).toFixed(2) }}</div>
      </div>
      <div class="cart-summary">
        <div>商品总数: {{ totalItems }} 件</div>
        <div class="total-price">总金额: ￥{{ totalAmount }}</div>
        <button class="main-btn" @click="checkout">结算</button>
        <button class="main-btn danger" @click="clearCart" :disabled="!cartItems.length">清空购物车</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { cartAPI } from '../api'
import Message from '../utils/message'

const router = useRouter()
const cartItems = ref([])

const totalItems = computed(() => cartItems.value.reduce((total, item) => total + item.num, 0))
const totalAmount = computed(() => cartItems.value.reduce((total, item) => total + item.price * item.num, 0).toFixed(2))

const loadCart = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.buyerId) {
    Message.warning('请先登录')
    router.push('/login')
    return
  }
  const response = await cartAPI.getCartContents(userInfo.buyerId)
  if (response.code === 200) {
    cartItems.value = response.data.cartItems || []
  }
}

const updateQuantity = async (item, newQuantity) => {
  if (newQuantity < 1) return
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const response = await cartAPI.updateCartItem(userInfo.buyerId, item.goodsId, newQuantity)
  if (response.code === 200) {
    item.num = newQuantity
    Message.success('数量更新成功')
  } else {
    Message.error(response.msg || '更新失败')
    await loadCart()
  }
}

const removeItem = async (item) => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const response = await cartAPI.removeFromCart(userInfo.buyerId, item.goodsId)
  if (response.code === 200) {
    Message.success('删除成功')
    await loadCart()
  } else {
    Message.error(response.msg || '删除失败')
  }
}

const clearCart = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const response = await cartAPI.clearCart(userInfo.buyerId)
  if (response.code === 200) {
    Message.success('购物车已清空')
    cartItems.value = []
  } else {
    Message.error(response.msg || '清空失败')
  }
}

const checkout = () => {
  if (!cartItems.value.length) {
    Message.warning('购物车是空的')
    return
  }
  Message.info('结算功能开发中...')
}

onMounted(loadCart)
</script>

<style scoped>
.cart-page {
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
.empty-cart {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}
.empty-content {
  text-align: center;
}
.empty-icon {
  font-size: 3em;
  margin-bottom: 10px;
}
.cart-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.cart-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
  padding: 16px 12px;
  gap: 18px;
}
.cart-image {
  width: 100px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  background: #f5f5f5;
}
.cart-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.cart-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}
.cart-actions button {
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 4px 10px;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s;
}
.cart-actions button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.cart-num {
  min-width: 24px;
  text-align: center;
  display: inline-block;
}
.remove-btn {
  background: #f56c6c !important;
  color: #fff !important;
}
.cart-sum {
  font-weight: bold;
  color: #e74c3c;
  min-width: 100px;
  text-align: right;
}
.cart-summary {
  margin-top: 24px;
  display: flex;
  gap: 24px;
  align-items: center;
  flex-wrap: wrap;
}
.total-price {
  color: #e74c3c;
  font-weight: bold;
}
.main-btn {
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 8px 24px;
  font-size: 1.1em;
  cursor: pointer;
  transition: background 0.2s;
  margin-right: 10px;
}
.main-btn:hover {
  background: #3076c9;
}
.main-btn.danger {
  background: #f56c6c;
}
.main-btn.danger:hover {
  background: #c0392b;
}
@media (max-width: 900px) {
  .cart-page {
    padding: 10px;
  }
  .cart-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  .cart-image {
    width: 80px;
    height: 60px;
  }
}
@media (max-width: 600px) {
  .cart-page {
    padding: 6px;
  }
  .cart-card {
    padding: 8px 4px;
  }
  .cart-image {
    width: 60px;
    height: 40px;
  }
  .page-title {
    font-size: 1.2em;
  }
}
</style> 