<template>
  <div class="seller-center">
    <div class="container">
      <div class="card">
        <div class="card-header">
          <h2>卖家中心</h2>
        </div>
        
        <div class="tabs">
          <div class="tab-buttons">
            <button 
              :class="['tab-button', { active: activeTab === 'goods' }]"
              @click="switchTab('goods')"
            >
              我的商品
            </button>
            <button 
              :class="['tab-button', { active: activeTab === 'profile' }]"
              @click="switchTab('profile')"
            >
              个人信息
            </button>
          </div>
          
          <div class="tab-content">
            <!-- 商品管理 -->
            <div v-if="activeTab === 'goods'" class="tab-pane">
              <div class="goods-header">
                <button class="btn btn-primary" @click="showAddGoodsDialog">
                  + 添加商品
                </button>
              </div>
              
              <div v-if="goodsLoading" class="loading-spinner"></div>
              <div v-else class="table-container">
                <table class="table">
                  <thead>
                    <tr>
                      <th>商品名称</th>
                      <th>类型</th>
                      <th>价格</th>
                      <th>库存</th>
                      <th>操作</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="goods in myGoods" :key="goods.goodsId">
                      <td>{{ goods.goodsName }}</td>
                      <td>{{ goods.type }}</td>
                      <td>¥{{ goods.price.toFixed(2) }}</td>
                      <td>{{ goods.num }}</td>
                      <td>
                        <button class="btn btn-small" @click="editGoods(goods)">
                          编辑
                        </button>
                        <button class="btn btn-small btn-danger" @click="deleteGoods(goods)">
                          删除
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <!-- 分页控件 -->
                <div class="pagination" v-if="goodsTotal > goodsPageSize">
                  <button :disabled="goodsPage === 1" @click="changeGoodsPage(goodsPage - 1)">上一页</button>
                  <span>第 {{ goodsPage }} / {{ goodsTotalPages }} 页</span>
                  <button :disabled="goodsPage === goodsTotalPages" @click="changeGoodsPage(goodsPage + 1)">下一页</button>
                </div>
              </div>
            </div>
            
            <!-- 个人信息 -->
            <div v-if="activeTab === 'profile'" class="tab-pane">
              <form class="form" @submit.prevent="updateProfile">
                <div class="form-group">
                  <label>卖家ID</label>
                  <input type="text" v-model="profileForm.sellerId" disabled class="form-control" />
                </div>
                <div class="form-group">
                  <label>用户名</label>
                  <input type="text" v-model="profileForm.sellerName" class="form-control" required />
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
                  <button type="button" class="btn btn-danger" @click="showLogoutDialog">
                    注销账户
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑商品对话框 -->
    <div v-if="goodsDialogVisible" class="modal-overlay" @click="closeDialog">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ isEdit ? '编辑商品' : '添加商品' }}</h3>
          <button class="close-btn" @click="closeDialog">&times;</button>
        </div>
        <form class="modal-body" @submit.prevent="saveGoods">
          <div class="form-group">
            <label>商品名称</label>
            <input type="text" v-model="goodsForm.goodsName" class="form-control" required />
          </div>
          <div class="form-group">
            <label>商品类型</label>
            <select v-model="goodsForm.type" class="form-control" required>
              <option value="">请选择商品类型</option>
              <option value="电子产品">电子产品</option>
              <option value="服装">服装</option>
              <option value="食品">食品</option>
              <option value="图书">图书</option>
              <option value="家居">家居</option>
              <option value="运动">运动</option>
            </select>
          </div>
          <div class="form-group">
            <label>价格</label>
            <input type="number" v-model="goodsForm.price" class="form-control" min="0" step="0.01" required />
          </div>
          <div class="form-group">
            <label>库存</label>
            <input type="number" v-model="goodsForm.num" class="form-control" min="0" required />
          </div>
          <div class="form-group">
            <label>商品图片</label>
            <input type="file" @change="handleImageUpload" class="form-control" accept="image/*" multiple />
            <div v-if="goodsForm.images && goodsForm.images.length" class="image-preview">
              <img v-for="(image, index) in goodsForm.images" :key="index" :src="image instanceof File ? URL.createObjectURL(image) : image" class="preview-image" />
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn" @click="closeDialog">取消</button>
            <button type="submit" class="btn btn-primary" :disabled="loading">
              {{ loading ? '保存中...' : (isEdit ? '更新' : '添加') }}
            </button>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 消息提示 -->
    <div v-if="message.show" :class="['message', message.type]">
      {{ message.text }}
    </div>
    
    <!-- 注销确认对话框 -->
    <div v-if="logoutDialogVisible" class="modal-overlay" @click="closeLogoutDialog">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>注销账户</h3>
          <button class="close-btn" @click="closeLogoutDialog">&times;</button>
        </div>
        <div class="modal-body">
          <div class="warning-message">
            <p><strong>⚠️ 警告：</strong>注销账户是不可逆操作，注销后您的账户将被永久删除，所有数据将无法恢复。</p>
            <p>请输入您的密码确认注销操作：</p>
          </div>
          <div class="form-group">
            <input 
              type="password" 
              v-model="logoutPassword" 
              class="form-control" 
              placeholder="请输入密码"
              required
            />
          </div>
          <div class="modal-footer">
            <button type="button" class="btn" @click="closeLogoutDialog">取消</button>
            <button type="button" class="btn btn-danger" @click="confirmLogout" :disabled="!logoutPassword">
              确认注销
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import { sellerAPI, goodsAPI, orderAPI } from '../api';
import Message from '../utils/message';

const router = useRouter();
const activeTab = ref('goods');
const loading = ref(false);
const goodsLoading = ref(false);
const ordersLoading = ref(false);

const myGoods = ref([]);
const orders = ref([]);
const goodsDialogVisible = ref(false);
const isEdit = ref(false);

// 商品分页相关
const goodsPage = ref(1)
const goodsPageSize = 10
const goodsTotal = ref(0)
const goodsTotalPages = computed(() => Math.ceil(goodsTotal.value / goodsPageSize))

const profileForm = reactive({
  sellerId: '',
  sellerName: '',
  newPassword: '',
});

const goodsForm = reactive({
  goodsId: '',
  goodsName: '',
  type: '',
  price: 0,
  num: 0,
  images: [],
});

const message = reactive({ show: false, type: '', text: '' });

// 订单分页相关
const orderPage = ref(1)
const orderPageSize = 5
const orderTotal = ref(0)
const orderTotalPages = computed(() => Math.ceil(orderTotal.value / orderPageSize))

const logoutDialogVisible = ref(false);
const logoutPassword = ref('');

const switchTab = (tab) => {
  activeTab.value = tab;
  if (tab === 'goods') {
    loadMyGoods();
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
  if (!userInfo.sellerId) {
    Message.error('请先登录');
    router.push('/login');
    return;
  }
  profileForm.sellerId = userInfo.sellerId;
  profileForm.sellerName = userInfo.sellerName;
};

const changeGoodsPage = async (newPage) => {
  if (newPage < 1 || newPage > goodsTotalPages.value) return
  goodsPage.value = newPage
  await loadMyGoods()
}

const loadMyGoods = async () => {
  if (!profileForm.sellerId) return;
  goodsLoading.value = true;
  try {
    const response = await goodsAPI.getGoodsBySellerIdPaged(profileForm.sellerId, goodsPage.value, goodsPageSize);
    if (response.code === 200) {
      myGoods.value = response.data.data || [];
      goodsTotal.value = response.data.total || 0;
    } else {
      Message.error(response.msg || '加载商品失败');
    }
  } catch (error) {
    Message.error('加载商品失败');
  } finally {
    goodsLoading.value = false;
  }
};

const loadOrders = async () => {
  if (!profileForm.sellerId) return;
  ordersLoading.value = true;
  try {
    const response = await orderAPI.getOrdersBySellerIdPaged(profileForm.sellerId, orderPage.value, orderPageSize)
    if (response.code === 200) {
      orders.value = response.data.data || []
      orderTotal.value = response.data.total || 0
    } else {
      Message.error(response.msg || '获取订单失败');
    }
  } catch (error) {
    Message.error('获取订单失败');
  } finally {
    ordersLoading.value = false;
  }
}

const showAddGoodsDialog = () => {
  isEdit.value = false;
  Object.keys(goodsForm).forEach(key => {
    if (key === 'images') {
      goodsForm.images = [];
    } else if (key === 'price' || key === 'num') {
      goodsForm[key] = 0;
    } else {
      goodsForm[key] = '';
    }
  });
  goodsDialogVisible.value = true;
};

const editGoods = (goods) => {
  isEdit.value = true;
  Object.keys(goodsForm).forEach(key => {
    if (key === 'images') {
      goodsForm.images = Array.isArray(goods.images) ? goods.images : (goods.images ? [goods.images] : []);
    } else {
    goodsForm[key] = goods[key];
    }
  });
  goodsDialogVisible.value = true;
};

const deleteGoods = async (goods) => {
  if (!confirm('确定要删除这个商品吗？')) {
    return;
  }
  
  try {
    const response = await goodsAPI.deleteGoods(goods.goodsId);
    if (response.code === 200) {
      Message.success('删除成功');
      loadMyGoods();
    } else {
      Message.error(response.msg || '删除失败');
    }
  } catch (error) {
    Message.error('删除失败');
  }
};

const saveGoods = async () => {
  try {
    loading.value = true;
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    if (!goodsForm.goodsName || !goodsForm.type || !goodsForm.price || !goodsForm.num) {
      Message.error('请填写完整商品信息');
      return;
    }
    const formData = new FormData();
    formData.append('goodsName', goodsForm.goodsName);
    formData.append('sellerId', userInfo.sellerId);
    formData.append('type', goodsForm.type);
    formData.append('price', goodsForm.price);
    formData.append('num', goodsForm.num);
    goodsForm.images.forEach(file => {
      formData.append('images', file);
    });
    let response;
    if (isEdit.value) {
      response = await goodsAPI.updateGoods(goodsForm);
    } else {
      response = await goodsAPI.addGoods(formData);
    }
    if (response.code === 200) {
      Message.success(isEdit.value ? '商品更新成功' : '商品添加成功');
      goodsDialogVisible.value = false;
      loadMyGoods();
    } else {
      Message.error(response.msg || '操作失败');
    }
  } catch (error) {
    Message.error('操作失败');
  } finally {
    loading.value = false;
  }
};

const closeDialog = () => {
  goodsDialogVisible.value = false;
};

const handleImageUpload = (event) => {
  const files = event.target.files;
  goodsForm.images = [];
  for (let i = 0; i < files.length; i++) {
    goodsForm.images.push(files[i]);
  }
};

const updateProfile = async () => {
  loading.value = true;
  try {
    const updateData = {
      sellerId: profileForm.sellerId,
      sellerName: profileForm.sellerName
    };
    
    if (profileForm.newPassword) {
      updateData.sellerPassword = profileForm.newPassword;
    }
    
    const response = await sellerAPI.update(updateData);
    if (response.code === 200) {
      Message.success('个人信息更新成功');
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
      userInfo.sellerName = profileForm.sellerName;
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

const showLogoutDialog = () => {
  logoutDialogVisible.value = true;
};

const closeLogoutDialog = () => {
  logoutDialogVisible.value = false;
  logoutPassword.value = '';
};

const confirmLogout = async () => {
  if (!logoutPassword.value) {
    Message.error('请输入密码');
    return;
  }
  
  try {
    const response = await sellerAPI.logout(profileForm.sellerId, logoutPassword.value);
    if (response.code === 200) {
      Message.success('账户注销成功');
      // 清除本地存储
      localStorage.removeItem('token');
      localStorage.removeItem('userType');
      localStorage.removeItem('userInfo');
      // 跳转到首页
      setTimeout(() => {
        window.location.reload();
      }, 1000);
    } else {
      Message.error(response.msg || '注销失败');
    }
  } catch (error) {
    Message.error('注销失败');
  } finally {
    closeLogoutDialog();
  }
};

onMounted(() => {
  loadUserInfo();
  loadMyGoods();
  loadOrders();
});

// 监听localStorage.userInfo变化，自动重新加载
watch(
  () => localStorage.getItem('userInfo'),
  () => {
    loadUserInfo();
    loadMyGoods();
    loadOrders();
  }
);
</script>

<style scoped>
.seller-center {
  max-width: 1200px;
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

.goods-header {
  margin-bottom: 20px;
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

.btn-danger {
  background: #f56c6c;
  color: white;
  border-color: #f56c6c;
}

.btn-danger:hover {
  background: #e74c3c;
}

.btn-small {
  padding: 4px 8px;
  font-size: 12px;
  margin-right: 5px;
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

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  width: 500px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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

/* 图片预览样式 */
.image-preview {
  display: flex;
  gap: 10px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.preview-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #ddd;
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

.status-tag {
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 4px;
  color: white;
  font-size: 0.9em;
}

.status-pending {
  background-color: #f56c6c;
}

.status-paid {
  background-color: #67c23a;
}

.status-cancelled {
  background-color: #909399;
}

.loading-spinner {
  margin: 50px auto;
  /* ... spinner styles ... */
}

.empty-orders {
  text-align: center;
  padding: 40px 0;
}

.empty-icon {
  font-size: 3em;
  margin-bottom: 10px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  margin: 0 5px;
}

.pagination button:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}
</style> 