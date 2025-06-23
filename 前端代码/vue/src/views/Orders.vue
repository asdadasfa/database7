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
    <div v-if="loading" class="loading-spinner"></div>
    <div v-else-if="!orders.length" class="empty-orders">
      <div class="empty-content">
        <div class="empty-icon">📦</div>
        <p>暂无{{ getTabText() }}订单</p>
      </div>
    </div>
    
    <div v-else class="orders-list">
      <div class="order-card" v-for="order in orders" :key="order.orderId">
        <div class="order-header">
          <span class="order-id">订单号: {{ order.orderId }}</span>
           <span class="order-time">下单时间: {{ formatTime(order.time) }}</span>
          <span class="order-status" :class="getStatusClass(order.state)">
            {{ order.state }}
          </span>
        </div>
        
        <div class="order-items">
          <div class="order-item" v-for="item in order.items" :key="item.goodsId">
            <div class="item-info">
              <span class="item-name">{{ item.goodsName }}</span>
              <span class="item-price">￥{{ item.price }} × {{ item.num }}</span>
            </div>
            <span class="item-total">￥{{ (item.price * item.num).toFixed(2) }}</span>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-total">
            总计: <strong>￥{{ order.totalAmount.toFixed(2) }}</strong>
          </div>
          <div class="order-actions">
            <button 
              v-if="order.state === '待支付'" 
              class="action-btn pay-btn" 
              @click="payOrder(order)"
            >
              立即支付
            </button>
            <button 
              v-if="order.state === '待支付'" 
              class="action-btn cancel-btn" 
              @click="cancelOrder(order)"
            >
              取消订单
            </button>
             <button
                v-if="order.state === '支付成功'"
                class="action-btn view-btn"
                @click="viewOrder(order)"
              >
                查看详情
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
const loading = ref(false)

const switchTab = async (tab) => {
  activeTab.value = tab
  await loadOrders()
}

const formatTime = (timeStr) => {
  if (!timeStr) return 'N/A';
  // Assuming the time is in a format that can be parsed by Date
  const date = new Date(timeStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const loadOrders = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.buyerId) {
    Message.warning('请先登录')
    router.push('/login')
    return
  }

  loading.value = true
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
  } finally {
    loading.value = false
  }
}

const payOrder = async (order) => {
  try {
    const response = await orderAPI.payOrder(order.orderId)
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

const cancelOrder = async (order) => {
  if (!confirm('确定要取消这个订单吗？')) return;
  try {
    const response = await orderAPI.cancelOrder(order.orderId)
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

const viewOrder = (order) => {
    // Placeholder for viewing order details, could navigate to a new page
    alert(`查看订单详情: ${order.orderId}`);
};


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
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.order-id {
  font-size: 0.9em;
  color: #666;
}
.order-time {
  font-size: 0.9em;
  color: #666;
}

.order-status {
  font-weight: bold;
}
.status-pending { color: #f56c6c; }
.status-paid { color: #67c23a; }
.status-cancelled { color: #909399; }

.order-items {
  padding: 15px 0;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.item-info {
  display: flex;
  flex-direction: column;
}

.item-name {
  font-weight: bold;
}

.item-price {
  font-size: 0.9em;
  color: #999;
}

.item-total {
  font-weight: bold;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.order-total {
  font-size: 1.1em;
}

.order-actions .action-btn {
  background: none;
  border: 1px solid #ccc;
  border-radius: 4px;
  padding: 6px 12px;
  cursor: pointer;
  transition: all 0.2s;
  margin-left: 10px;
}
.action-btn.pay-btn {
  border-color: #409eff;
  background-color: #409eff;
  color: white;
}
.action-btn.cancel-btn {
  border-color: #f56c6c;
  color: #f56c6c;
}
.action-btn.cancel-btn:hover {
  background-color: #f56c6c;
  color: white;
}

.action-btn.view-btn {
  border-color: #67c23a;
  color: #67c23a;
}
.action-btn.view-btn:hover {
  background-color: #67c23a;
  color: white;
}

.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 50px auto;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 