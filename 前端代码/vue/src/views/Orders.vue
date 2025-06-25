<template>
  <div class="orders-page">
    <h2 class="page-title">我的订单</h2>
    <div class="order-tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-btn', activeTab === tab.key ? 'active' : '']"
        @click="switchTab(tab.key)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-if="loading" class="loading-spinner"></div>
    <div v-else-if="!orders.length" class="empty-orders">
      <div class="empty-content">
        <div class="empty-icon">📦</div>
        <p>暂无{{ getTabText() }}订单</p>
      </div>
    </div>

    <div v-else class="orders-list">
      <table class="table">
        <thead>
          <tr>
            <th>商品图片</th>
            <th>订单号</th>
            <th>数量</th>
            <th>总价</th>
            <th>下单时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.orderId">
            <td>
              <img
                :src="goodsImages[order.goodsId] || '/default-goods.jpg'"
                alt="商品图片"
                style="width: 72px; height: 72px; object-fit: cover; border-radius: 10px; box-shadow: 0 1px 4px rgba(0,0,0,0.08); background: #f5f7fa; cursor: pointer;"
                @click="goToGoodsDetail(order.goodsId)"
              />
            </td>
            <td><span class="order-cell order-id">{{ order.orderId }}</span></td>
            <td><span class="order-cell order-num">{{ order.num }}</span></td>
            <td><span class="order-cell order-sum">￥{{ order.sum }}</span></td>
            <td><span class="order-cell order-time">{{ order.time }}</span></td>
            <td>
              <template v-if="order.state === '待支付'">
                <button class="action-btn pay-btn" @click="payOrder(order)">支付</button>
                <button class="action-btn cancel-btn" @click="cancelOrder(order)">取消</button>
              </template>
            </td>
          </tr>
        </tbody>
      </table>
      <div class="pagination" v-if="total > pageSize">
        <button :disabled="page === 1" @click="changePage(page - 1)">上一页</button>
        <span>第 {{ page }} / {{ totalPages }} 页</span>
        <button :disabled="page === totalPages" @click="changePage(page + 1)">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { orderAPI, goodsAPI } from '../api'
import Message from '../utils/message'

const router = useRouter()
const tabs = [
  { key: 'pending', label: '待支付' },
  { key: 'paid', label: '已支付' },
  { key: 'cancelled', label: '已取消' }
]
const activeTab = ref('pending')
const orders = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 5
const total = ref(0)
const totalPages = computed(() => Math.ceil(total.value / pageSize))
const goodsImages = ref({})

const switchTab = (tab) => {
  if (activeTab.value !== tab) {
    activeTab.value = tab
    page.value = 1
    loadOrders()
  }
}

const changePage = (newPage) => {
  if (newPage < 1 || newPage > totalPages.value) return
  page.value = newPage
  loadOrders()
}

const formatTime = (timeStr) => {
  if (!timeStr) return 'N/A';
  const date = new Date(timeStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const getTabText = () => {
  const tab = tabs.find(t => t.key === activeTab.value)
  return tab ? tab.label : ''
}

const getStatusClass = (status) => {
  switch (status) {
    case '待支付': return 'status-pending'
    case '支付成功': return 'status-paid'
    case '取消': return 'status-cancelled'
    default: return ''
  }
}

const getGoodsImage = async (goodsId) => {
  if (goodsImages.value[goodsId]) return goodsImages.value[goodsId]
  try {
    const res = await goodsAPI.getGoodsById(goodsId)
    let img = ''
    if (res.code === 200 && res.data && res.data.images && res.data.images.length > 0) {
      img = res.data.images[0]
      // 若图片路径不是完整URL，拼接后端静态资源前缀
      if (!/^https?:\/\//.test(img)) {
        img = `http://localhost:8686/images/${img}`
      }
    } else {
      img = '/default-goods.jpg'
    }
    goodsImages.value[goodsId] = img
    return img
  } catch {
    goodsImages.value[goodsId] = '/default-goods.jpg'
    return '/default-goods.jpg'
  }
}

const fetchAllGoodsImages = async (ordersArr) => {
  const promises = ordersArr.map(order => getGoodsImage(order.goodsId))
  await Promise.all(promises)
}

const loadOrders = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.buyerId) {
    Message.warning('请先登录')
    router.push('/login')
    return
  }
  loading.value = true
  try {
    let state = ''
    switch (activeTab.value) {
      case 'pending': state = '待支付'; break
      case 'paid': state = '支付成功'; break
      case 'cancelled': state = '取消'; break
    }
    const response = await orderAPI.getOrdersByBuyerIdAndStatePaged(userInfo.buyerId, state, page.value, pageSize)
    const resData = response.data || response

    if (response.code === 200) {
      orders.value = response.data || []
      await fetchAllGoodsImages(orders.value)
    }
    else {
      orders.value = []
      total.value = 0
      Message.error(resData.msg || '获取订单失败')
    }
  } catch (error) {
    orders.value = []
    total.value = 0
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
      loadOrders()
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
      loadOrders()
    } else {
      Message.error(response.msg || '取消订单失败')
    }
  } catch (error) {
    Message.error('取消订单失败')
  }
}

const viewOrder = (order) => {
  alert(`查看订单详情: ${order.orderId}`);
};

const goToGoodsDetail = (goodsId) => {
  router.push(`/goods/${goodsId}`)
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
  background: #f7f9fb;
  box-sizing: border-box;
}
.page-title {
  font-size: 2em;
  color: #222;
  margin-bottom: 24px;
  text-align: left;
  font-weight: bold;
  letter-spacing: 1px;
}
.order-tabs {
  display: flex;
  gap: 18px;
  margin-bottom: 32px;
}
.tab-btn {
  padding: 10px 32px;
  background: #f5f6fa;
  border: none;
  border-radius: 10px;
  color: #666;
  font-size: 1.15em;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(64,158,255,0.04);
}
.tab-btn.active {
  background: #409eff;
  color: #fff;
  box-shadow: 0 4px 16px rgba(64,158,255,0.12);
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
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 24px 18px 10px 18px;
  margin-bottom: 24px;
}
.table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 8px;
  margin-bottom: 20px;
}
.table th, .table td {
  padding: 14px 12px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  text-align: left;
  font-size: 1.08em;
}
.table th {
  background: #f5f5f5;
  font-weight: 600;
  border-radius: 8px 8px 0 0;
}
.table tr {
  transition: box-shadow 0.2s, background 0.2s;
}
.table tbody tr:hover {
  background: #f0f7ff;
  box-shadow: 0 2px 8px rgba(64,158,255,0.08);
}
.order-status {
  font-weight: bold;
  padding: 4px 14px;
  border-radius: 12px;
  font-size: 1em;
  display: inline-block;
}
.status-pending { background: #fff1f0; color: #f56c6c; border: 1px solid #fbc4c4; }
.status-paid { background: #f0f9eb; color: #67c23a; border: 1px solid #b7eb8f; }
.status-cancelled { background: #f4f4f5; color: #909399; border: 1px solid #e4e7ed; }
.action-btn {
  border-radius: 8px;
  border: none;
  padding: 7px 18px;
  font-size: 1em;
  margin-right: 8px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04);
}
.action-btn.pay-btn {
  background: #409eff;
  color: #fff;
}
.action-btn.pay-btn:hover {
  background: #337ecc;
}
.action-btn.cancel-btn {
  background: #fff1f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}
.action-btn.cancel-btn:hover {
  background: #f56c6c;
  color: #fff;
}
.action-btn.view-btn {
  background: #f0f9eb;
  color: #67c23a;
  border: 1px solid #b7eb8f;
}
.action-btn.view-btn:hover {
  background: #67c23a;
  color: #fff;
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
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 24px;
  gap: 16px;
}
.pagination button {
  padding: 8px 22px;
  background: #f5f6fa;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 1.08em;
  color: #409eff;
  font-weight: 500;
}
.pagination button:disabled {
  background: #f5f6fa;
  color: #bbb;
  cursor: not-allowed;
}
.pagination span {
  font-size: 1.1em;
  color: #666;
}
.order-cell {
  display: inline-block;
  min-width: 120px;
  max-width: 320px;
  padding: 8px 18px;
  background: #f5f7fa;
  border-radius: 12px;
  font-weight: 500;
  font-size: 1.08em;
  color: #333;
  box-shadow: 0 1px 4px rgba(64,158,255,0.06);
  word-break: break-all;
}
.order-id { min-width: 180px; }
.order-num { min-width: 100px; text-align: center; }
.order-sum { min-width: 120px; color: #409eff; font-weight: bold; }
.order-time { min-width: 180px; font-size: 1em; color: #666; }
</style>
