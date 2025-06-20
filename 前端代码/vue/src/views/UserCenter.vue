<template>
  <div class="user-center">
    <div class="container">
      <div class="card">
        <div class="card-header">
          <h2>个人中心</h2>
        </div>
        
        <div class="tabs">
          <div class="tab-buttons">
            <button 
              :class="['tab-button', { active: activeTab === 'profile' }]"
              @click="activeTab = 'profile'"
            >
              个人信息
            </button>
            <button 
              :class="['tab-button', { active: activeTab === 'orders' }]"
              @click="activeTab = 'orders'"
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
              <div v-if="orders.length === 0" class="empty-orders">
                <div class="empty-content">
                  <div class="empty-icon">📦</div>
                  <p>暂无订单</p>
                  <button class="btn btn-primary" @click="$router.push('/goods')">
                    去购物
                  </button>
                </div>
              </div>
              <div v-else class="table-container">
                <table class="table">
                  <thead>
                    <tr>
                      <th>订单号</th>
                      <th>商品名称</th>
                      <th>数量</th>
                      <th>总价</th>
                      <th>下单时间</th>
                      <th>状态</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="order in orders" :key="order.orderId">
                      <td>{{ order.orderId }}</td>
                      <td>{{ order.goodsName }}</td>
                      <td>{{ order.num }}</td>
                      <td>¥{{ order.totalPrice }}</td>
                      <td>{{ order.orderTime }}</td>
                      <td>
                        <span :class="['status-tag', getOrderStatusType(order.status)]">
                          {{ getOrderStatusText(order.status) }}
                        </span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 消息提示 -->
    <div v-if="message.show" :class="['message', message.type]">
      {{ message.text }}
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { buyerAPI } from '../api'

export default {
  name: 'UserCenter',
  setup() {
    const activeTab = ref('profile')
    const loading = ref(false)
    const orders = ref([])
    
    const message = reactive({
      show: false,
      text: '',
      type: 'info'
    })
    
    const profileForm = reactive({
      buyerId: '',
      buyerName: '',
      newPassword: ''
    })
    
    const showMessage = (text, type = 'info') => {
      message.text = text
      message.type = type
      message.show = true
      setTimeout(() => {
        message.show = false
      }, 3000)
    }
    
    const loadUserInfo = () => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      profileForm.buyerId = userInfo.buyerId || ''
      profileForm.buyerName = userInfo.buyerName || ''
    }
    
    const updateProfile = async () => {
      try {
        loading.value = true
        
        const updateData = {
          buyerId: profileForm.buyerId,
          buyerName: profileForm.buyerName
        }
        
        if (profileForm.newPassword) {
          updateData.buyerPassword = profileForm.newPassword
        }
        
        const response = await buyerAPI.update(updateData)
        if (response.code === 200) {
          showMessage('个人信息更新成功', 'success')
          // 更新本地存储的用户信息
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
          userInfo.buyerName = profileForm.buyerName
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          profileForm.newPassword = ''
        } else {
          showMessage(response.msg || '更新失败', 'error')
        }
      } catch (error) {
        showMessage('更新失败', 'error')
      } finally {
        loading.value = false
      }
    }
    
    const getOrderStatusType = (status) => {
      const statusMap = {
        'pending': 'warning',
        'paid': 'success',
        'shipped': 'primary',
        'delivered': 'success',
        'cancelled': 'danger'
      }
      return statusMap[status] || 'info'
    }
    
    const getOrderStatusText = (status) => {
      const statusMap = {
        'pending': '待付款',
        'paid': '已付款',
        'shipped': '已发货',
        'delivered': '已送达',
        'cancelled': '已取消'
      }
      return statusMap[status] || '未知'
    }
    
    onMounted(() => {
      loadUserInfo()
      // 这里可以加载订单数据
      // loadOrders()
    })
    
    return {
      activeTab,
      loading,
      profileForm,
      orders,
      message,
      updateProfile,
      getOrderStatusType,
      getOrderStatusText
    }
  }
}
</script>

<style scoped>
.user-center {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.card-header h2 {
  margin: 0;
  color: #333;
}

.tabs {
  padding: 20px;
}

.tab-buttons {
  display: flex;
  border-bottom: 1px solid #ddd;
  margin-bottom: 20px;
}

.tab-button {
  padding: 10px 20px;
  border: none;
  background: none;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab-button.active {
  border-bottom-color: #409eff;
  color: #409eff;
}

.tab-pane {
  min-height: 400px;
}

.form {
  max-width: 500px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 600;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-control:focus {
  outline: none;
  border-color: #409eff;
}

.form-control:disabled {
  background: #f5f5f5;
  color: #999;
}

.btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.btn:hover {
  background: #f5f5f5;
}

.btn-primary {
  background: #409eff;
  color: white;
  border-color: #409eff;
}

.btn-primary:hover {
  background: #337ecc;
}

.empty-orders {
  text-align: center;
  padding: 40px 0;
}

.empty-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.empty-icon {
  font-size: 48px;
  color: #ccc;
}

.empty-content p {
  color: #999;
  font-size: 16px;
}

.table-container {
  overflow-x: auto;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th,
.table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.table th {
  background: #f5f5f5;
  font-weight: 600;
}

.status-tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
}

.status-tag.success {
  background: #67c23a;
}

.status-tag.warning {
  background: #e6a23c;
}

.status-tag.primary {
  background: #409eff;
}

.status-tag.danger {
  background: #f56c6c;
}

.status-tag.info {
  background: #909399;
}

/* 消息提示样式 */
.message {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 12px 20px;
  border-radius: 4px;
  color: white;
  z-index: 1001;
  animation: slideIn 0.3s ease;
}

.message.success {
  background: #67c23a;
}

.message.error {
  background: #f56c6c;
}

.message.info {
  background: #909399;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
</style> 