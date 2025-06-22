<template>
  <div class="orders-page">
    <h2 class="page-title">我的订单</h2>
    
    <!-- 订单状态切换 -->
    <div class="order-tabs">
      <button 
        :class="['tab-btn', activeTab === 'pending' ? 'active' : '']" 
        @click="switchTab('pending')"
      >
        待支付
      </button>
      <button 
        :class="['tab-btn', activeTab === 'paid' ? 'active' : '']" 
        @click="switchTab('paid')"
      >
        已支付
      </button>
      <button 
        :class="['tab-btn', activeTab === 'cancelled' ? 'active' : '']" 
        @click="switchTab('cancelled')"
      >
        已取消
      </button>
    </div>

    <!-- 订单列表 -->
    <div v-if="!orders.length" class="empty-orders">
      <div class="empty-content">
        <div class="empty-icon">📦</div>
        <p>暂无{{ getTabText() }}订单</p>
      </div>
    </div>
    
    <div v-else class="orders-list">
      <div class="order-card" v-for="order in orders" :key="order.orderId">
        <div class="order-header">
          <span class="order-id">订单号: {{ order.orderId }}</span>
          <span class="order-status" :class="getStatusClass(order.state)">
            {{ order.state }}
          </span>
        </div>
        
        <div class="order-items">
          <div class="order-item" v-for="item in order.orderItems" :key="item.goodsId">
            <div class="item-info">
              <span class="item-name">{{ item.goodsName }}</span>
              <span class="item-price">￥{{ item.price }} × {{ item.num }}</span>
            </div>
            <span class="item-total">￥{{ (item.price * item.num).toFixed(2) }}</span>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-total">
            总计: ￥{{ order.totalAmount }}
          </div>
          <div class="order-actions">
            <button 
              v-if="order.state === '待支付'" 
              class="action-btn pay-btn" 
              @click="payOrder(order.orderId)"
            >
              立即支付
            </button>
            <button 
              v-if="order.state === '待支付'" 
              class="action-btn cancel-btn" 
              @click="cancelOrder(order.orderId)"
            >
              取消订单
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderAPI } from '../api'
import Message from '../utils/message'

const router = useRouter()
const activeTab = ref('pending')
const orders = ref([])

const switchTab = async (tab) => {
  activeTab.value = tab
  await loadOrders()
}

const loadOrders = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.buyerId) {
    Message.warning('请先登录')
    router.push('/login')
    return
  }

  try {
    let response
    switch (activeTab.value) {
      case 'pending':
        response = await orderAPI.getPendingOrders(userInfo.buyerId)
        break
      case 'paid':
        response = await orderAPI.getPaidOrders(userInfo.buyerId)
        break
      case 'cancelled':
        response = await orderAPI.getCancelledOrders(userInfo.buyerId)
        break
    }
    
    if (response.code === 200) {
      orders.value = response.data || []
    } else {
      Message.error(response.msg || '获取订单失败')
    }
  } catch (error) {
    Message.error('获取订单失败')
  }
}

const payOrder = async (orderId) => {
  try {
    const response = await orderAPI.payOrder(orderId)
    if (response.code === 200) {
      Message.success('支付成功')
      await loadOrders()
    } else {
      Message.error(response.msg || '支付失败')
    }
  } catch (error) {
    Message.error('支付失败')
  }
}

const cancelOrder = async (orderId) => {
  try {
    const response = await orderAPI.cancelOrder(orderId)
    if (response.code === 200) {
      Message.success('订单已取消')
      await loadOrders()
    } else {
      Message.error(response.msg || '取消订单失败')
    }
  } catch (error) {
    Message.error('取消订单失败')
  }
}

const getTabText = () => {
  switch (activeTab.value) {
    case 'pending': return '待支付'
    case 'paid': return '已支付'
    case 'cancelled': return '已取消'
    default: return ''
  }
}

const getStatusClass = (status) => {
  switch (status) {
    case '待支付': return 'status-pending'
    case '支付成功': return 'status-paid'
    case '取消': return 'status-cancelled'
    default: return ''
  }
}

onMounted(loadOrders)
</script>

<style scoped>
.orders-page {
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

.order-tabs {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.tab-btn {
  padding: 10px 20px;
  background: #f5f6fa;
  border: none;
  border-radius: 6px;
  color: #666;
  font-size: 1em;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-btn.active {
  background: #409eff;
  color: #fff;
}

.empty-orders {
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

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.order-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
  padding: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-id {
  font-weight: bold;
  color: #333;
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.9em;
  font-weight: bold;
}

.status-pending {
  background: #fff3cd;
  color: #856404;
}

.status-paid {
  background: #d4edda;
  color: #155724;
}

.status-cancelled {
  background: #f8d7da;
  color: #721c24;
}

.order-items {
  margin-bottom: 16px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f9f9f9;
}

.order-item:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.item-name {
  font-weight: bold;
  color: #333;
}

.item-price {
  color: #666;
  font-size: 0.9em;
}

.item-total {
  font-weight: bold;
  color: #e74c3c;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.order-total {
  font-size: 1.1em;
  font-weight: bold;
  color: #e74c3c;
}

.order-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 0.9em;
  cursor: pointer;
  transition: background 0.2s;
}

.pay-btn {
  background: #409eff;
  color: #fff;
}

.pay-btn:hover {
  background: #3076c9;
}

.cancel-btn {
  background: #f56c6c;
  color: #fff;
}

.cancel-btn:hover {
  background: #e74c3c;
}

@media (max-width: 600px) {
  .orders-page {
    padding: 10px;
  }
  
  .order-tabs {
    flex-wrap: wrap;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .order-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .order-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style> 