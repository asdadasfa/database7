<template>
  <div class="user-center">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <h2>个人中心</h2>
          </template>
          
          <el-tabs v-model="activeTab">
            <el-tab-pane label="个人信息" name="profile">
              <el-form
                ref="profileFormRef"
                :model="profileForm"
                :rules="profileRules"
                label-width="100px"
              >
                <el-form-item label="买家ID" prop="buyerId">
                  <el-input v-model="profileForm.buyerId" disabled />
                </el-form-item>
                <el-form-item label="用户名" prop="buyerName">
                  <el-input v-model="profileForm.buyerName" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="profileForm.newPassword"
                    type="password"
                    placeholder="留空表示不修改密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updateProfile" :loading="loading">
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            
            <el-tab-pane label="我的订单" name="orders">
              <div v-if="orders.length === 0" class="empty-orders">
                <el-empty description="暂无订单">
                  <el-button type="primary" @click="$router.push('/goods')">
                    去购物
                  </el-button>
                </el-empty>
              </div>
              <div v-else>
                <el-table :data="orders" style="width: 100%">
                  <el-table-column prop="orderId" label="订单号" width="180" />
                  <el-table-column prop="goodsName" label="商品名称" />
                  <el-table-column prop="num" label="数量" width="100" />
                  <el-table-column prop="totalPrice" label="总价" width="120">
                    <template #default="scope">
                      ¥{{ scope.row.totalPrice }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="orderTime" label="下单时间" width="180" />
                  <el-table-column label="状态" width="100">
                    <template #default="scope">
                      <el-tag :type="getOrderStatusType(scope.row.status)">
                        {{ getOrderStatusText(scope.row.status) }}
                      </el-tag>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { buyerAPI } from '../api'
import { ElMessage } from 'element-plus'

export default {
  name: 'UserCenter',
  setup() {
    const activeTab = ref('profile')
    const loading = ref(false)
    const profileFormRef = ref()
    const orders = ref([])
    
    const profileForm = reactive({
      buyerId: '',
      buyerName: '',
      newPassword: ''
    })
    
    const profileRules = {
      buyerName: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
      ]
    }
    
    const loadUserInfo = () => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      profileForm.buyerId = userInfo.buyerId || ''
      profileForm.buyerName = userInfo.buyerName || ''
    }
    
    const updateProfile = async () => {
      try {
        await profileFormRef.value.validate()
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
          ElMessage.success('个人信息更新成功')
          // 更新本地存储的用户信息
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
          userInfo.buyerName = profileForm.buyerName
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          profileForm.newPassword = ''
        } else {
          ElMessage.error(response.msg || '更新失败')
        }
      } catch (error) {
        ElMessage.error('更新失败')
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
      profileFormRef,
      profileForm,
      profileRules,
      orders,
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
}

.empty-orders {
  text-align: center;
  padding: 40px 0;
}

.el-form {
  max-width: 500px;
}
</style> 