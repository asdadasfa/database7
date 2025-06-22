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
              @click="activeTab = 'goods'"
            >
              我的商品
            </button>
            <button 
              :class="['tab-button', { active: activeTab === 'profile' }]"
              @click="activeTab = 'profile'"
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
              
              <div class="table-container">
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
                      <td>¥{{ goods.price }}</td>
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
            <input type="file" @change="handleImageUpload" class="form-control" accept="image/*" />
            <div v-if="goodsForm.images && goodsForm.images.length" class="image-preview">
              <img v-for="(image, index) in goodsForm.images" :key="index" :src="image" class="preview-image" />
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
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { sellerAPI, goodsAPI } from '../api'

export default {
  name: 'SellerCenter',
  setup() {
    const activeTab = ref('goods')
    const loading = ref(false)
    const myGoods = ref([])
    const goodsDialogVisible = ref(false)
    const isEdit = ref(false)
    
    const message = reactive({
      show: false,
      text: '',
      type: 'info'
    })
    
    const profileForm = reactive({
      sellerId: '',
      sellerName: '',
      newPassword: ''
    })
    
    const goodsForm = reactive({
      goodsId: '',
      goodsName: '',
      type: '',
      price: 0,
      num: 0,
      images: []
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
      profileForm.sellerId = userInfo.sellerId || ''
      profileForm.sellerName = userInfo.sellerName || ''
    }
    
    const loadMyGoods = async () => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (!userInfo.sellerId) return
      
      try {
        const response = await goodsAPI.getGoodsBySellerId(userInfo.sellerId)
        if (response.code === 200) {
          myGoods.value = response.data
        }
      } catch (error) {
        console.error('加载商品失败:', error)
        showMessage('加载商品失败', 'error')
      }
    }
    
    const showAddGoodsDialog = () => {
      isEdit.value = false
      Object.keys(goodsForm).forEach(key => {
        goodsForm[key] = key === 'price' || key === 'num' ? 0 : ''
      })
      goodsDialogVisible.value = true
    }
    
    const editGoods = (goods) => {
      isEdit.value = true
      Object.keys(goodsForm).forEach(key => {
        goodsForm[key] = goods[key]
      })
      goodsDialogVisible.value = true
    }
    
    const closeDialog = () => {
      goodsDialogVisible.value = false
    }
    
    const saveGoods = async () => {
      try {
        loading.value = true
        
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const goodsData = {
          ...goodsForm,
          sellerId: userInfo.sellerId
        }
        
        let response
        if (isEdit.value) {
          response = await goodsAPI.updateGoods(goodsData)
        } else {
          response = await goodsAPI.addGoods(goodsData)
        }
        
        if (response.code === 200) {
          showMessage(isEdit.value ? '商品更新成功' : '商品添加成功', 'success')
          goodsDialogVisible.value = false
          loadMyGoods()
        } else {
          showMessage(response.msg || '操作失败', 'error')
        }
      } catch (error) {
        showMessage('操作失败', 'error')
      } finally {
        loading.value = false
      }
    }
    
    const deleteGoods = async (goods) => {
      if (!confirm('确定要删除这个商品吗？')) {
        return
      }
      
      try {
        const response = await goodsAPI.deleteGoods(goods.goodsId)
        if (response.code === 200) {
          showMessage('删除成功', 'success')
          loadMyGoods()
        } else {
          showMessage(response.msg || '删除失败', 'error')
        }
      } catch (error) {
        showMessage('删除失败', 'error')
      }
    }
    
    const updateProfile = async () => {
      try {
        loading.value = true
        
        const updateData = {
          sellerId: profileForm.sellerId,
          sellerName: profileForm.sellerName
        }
        
        if (profileForm.newPassword) {
          updateData.sellerPassword = profileForm.newPassword
        }
        
        const response = await sellerAPI.update(updateData)
        if (response.code === 200) {
          showMessage('个人信息更新成功', 'success')
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
          userInfo.sellerName = profileForm.sellerName
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
    
    const handleImageUpload = (event) => {
      const files = event.target.files
      for (let i = 0; i < files.length; i++) {
        const file = files[i]
        const reader = new FileReader()
        reader.onload = (e) => {
          goodsForm.images.push(e.target.result)
        }
        reader.readAsDataURL(file)
      }
    }
    
    onMounted(() => {
      loadUserInfo()
      loadMyGoods()
    })
    
    return {
      activeTab,
      loading,
      profileForm,
      goodsForm,
      myGoods,
      goodsDialogVisible,
      isEdit,
      message,
      showAddGoodsDialog,
      editGoods,
      closeDialog,
      saveGoods,
      deleteGoods,
      updateProfile,
      handleImageUpload
    }
  }
}
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
</style> 