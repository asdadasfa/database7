<template>
  <div class="user-center">
    <div class="container">
      <div class="card">
        <div class="card-header">
          <h2>个人中心</h2>
        </div>
        
        <div class.tabs>
          <div class="tab-buttons">
            <button 
              :class="['tab-button', { active: activeTab === 'profile' }]"
              @click="activeTab = 'profile'"
            >
              个人信息
            </button>
            <button 
              :class="['tab-button', { active: activeTab === 'orders' }]"
              @click="switchTab('orders')"
            >
              我的订单
            </button>
          </div>
          
          <div class="tab-content">
            <!-- 个人信息 -->
            <div v-if="activeTab === 'profile'" class="tab-pane">
              <form class="form" @submit.prevent="updateProfile">
                <div class="form-group">
                  <label>买家ID</label>
                  <input type="text" v-model="profileForm.buyerId" disabled class="form-control" />
                </div>
                <div class="form-group">
                  <label>用户名</label>
                  <input type="text" v-model="profileForm.buyerName" class="form-control" required />
                </div>
                <div class="form-group">
                  <label>新密码</label>
                  <input 
                    type="password" 
                    v-model="profileForm.newPassword" 
                    class="form-control"
                    placeholder="留空表示不修改密码"
                  />
                </div>
                <div class="form-group">
                  <button type="submit" class="btn btn-primary" :disabled="loading">
                    {{ loading ? '保存中...' : '保存修改' }}
                  </button>
                </div>
              </form>
            </div>
            
            <!-- 我的订单 -->
            <div v-if="activeTab === 'orders'" class="tab-pane">
               <div v-if="ordersLoading" class="loading-spinner"></div>
              <div v-else-if="orders.length === 0" class="empty-orders">
                <div class="empty-content">
                  <div class="empty-icon">📦</div>
                  <p>暂无订单</p>
                  <button class="btn btn-primary" @click="$router.push('/goods')">
                    去购物
                  </button>
                </div>
              </div>
              <div v-else class="orders-list">
                <div class="order-card" v-for="order in orders" :key="order.orderId">
                  <div class="order-header">
                    <span class="order-id">订单号: {{ order.orderId }}</span>
                    <span class="order-time">下单时间: {{ formatTime(order.time) }}</span>
                    <span :class="['status-tag', getStatusClass(order.state)]">{{ order.state }}</span>
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
                    <div class="order-total">总计: <strong>￥{{ order.totalAmount.toFixed(2) }}</strong></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import { buyerAPI, orderAPI } from '../api';
import Message from '../utils/message';

const router = useRouter();
const activeTab = ref('profile');
const loading = ref(false);
const ordersLoading = ref(false);
const orders = ref([]);

const profileForm = reactive({
  buyerId: '',
  buyerName: '',
  newPassword: '',
});

const switchTab = (tab) => {
  activeTab.value = tab;
  if (tab === 'orders') {
    loadOrders();
  }
};

const formatTime = (timeStr) => {
  if (!timeStr) return 'N/A';
  const date = new Date(timeStr);
  return date.toLocaleString('zh-CN');
};

const getStatusClass = (status) => {
    switch (status) {
        case '待支付': return 'status-pending';
        case '支付成功': return 'status-paid';
        case '取消': return 'status-cancelled';
        default: return '';
    }
};

const loadUserInfo = () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
  if (!userInfo.buyerId) {
    Message.error('请先登录');
    router.push('/login');
    return;
  }
  profileForm.buyerId = userInfo.buyerId;
  profileForm.buyerName = userInfo.buyerName;
};

const loadOrders = async () => {
  if (!profileForm.buyerId) return;
  ordersLoading.value = true;
  try {
    const response = await orderAPI.getOrdersByBuyerId(profileForm.buyerId);
    if (response.code === 200) {
      orders.value = response.data || [];
    } else {
      Message.error(response.msg || '获取订单失败');
    }
  } catch (error) {
    Message.error('获取订单失败');
  } finally {
    ordersLoading.value = false;
  }
};

const updateProfile = async () => {
  loading.value = true;
  try {
    const updateData = {
      buyerId: profileForm.buyerId,
      buyerName: profileForm.buyerName,
    };
    if (profileForm.newPassword) {
      updateData.buyerPassword = profileForm.newPassword;
    }
    const response = await buyerAPI.update(updateData);
    if (response.code === 200) {
      Message.success('个人信息更新成功');
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
      userInfo.buyerName = profileForm.buyerName;
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
      profileForm.newPassword = '';
    } else {
      Message.error(response.msg || '更新失败');
    }
  } catch (error) {
    Message.error('更新失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadUserInfo();
  loadOrders();
});

// 监听localStorage.userInfo变化，自动重新加载
watch(
  () => localStorage.getItem('userInfo'),
  () => {
    loadUserInfo();
    loadOrders();
  }
);
</script>

<style scoped>
/* Basic styles for user center */
.user-center {
  max-width: 1000px;
  margin: 20px auto;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}
.container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  overflow: hidden;
}
.card-header {
  padding: 20px 25px;
  background-color: #f7f9fc;
  border-bottom: 1px solid #e9ecef;
}
.card-header h2 {
  margin: 0;
  font-size: 1.5em;
  color: #333;
}
.tabs {
  display: flex;
}
.tab-buttons {
  display: flex;
  flex-direction: column;
  padding: 15px 0;
  border-right: 1px solid #e9ecef;
  background-color: #f7f9fc;
}
.tab-button {
  padding: 15px 25px;
  border: none;
  background: none;
  cursor: pointer;
  text-align: left;
  font-size: 1em;
  color: #555;
  border-left: 3px solid transparent;
  transition: all 0.2s;
}
.tab-button.active {
  border-left-color: #409eff;
  background-color: #fff;
  color: #409eff;
  font-weight: bold;
}
.tab-content {
  flex-grow: 1;
  padding: 25px;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #555;
}
.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
}
.form-control:disabled {
  background-color: #f5f5f5;
}
.btn-primary {
  padding: 10px 20px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.btn-primary:hover:not(:disabled) {
  background-color: #3076c9;
}
.btn-primary:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

/* Orders List Styles */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.order-card {
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 15px;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
}
.order-id, .order-time { font-size: 0.9em; color: #666; }
.status-tag { font-weight: bold; }
.status-pending { color: #f56c6c; }
.status-paid { color: #67c23a; }
.status-cancelled { color: #909399; }
.order-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
}
.item-name { font-weight: bold; }
.item-price { font-size: 0.9em; color: #888; }
.order-footer {
  text-align: right;
  padding-top: 10px;
  border-top: 1px solid #eee;
  margin-top: 10px;
}
.empty-orders {
    text-align: center;
    padding: 40px 0;
}
.empty-icon {
    font-size: 3em;
    margin-bottom: 10px;
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