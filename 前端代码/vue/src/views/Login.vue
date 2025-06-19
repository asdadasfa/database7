<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>用户登录</h2>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="买家登录" name="buyer">
          <el-form
            ref="buyerFormRef"
            :model="buyerForm"
            :rules="buyerRules"
            label-width="80px"
          >
            <el-form-item label="用户名" prop="buyerName">
              <el-input
                v-model="buyerForm.buyerName"
                placeholder="请输入买家用户名"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item label="密码" prop="buyerPassword">
              <el-input
                v-model="buyerForm.buyerPassword"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleBuyerLogin" :loading="loading">
                登录
              </el-button>
              <el-button @click="$router.push('/register')">注册账号</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="卖家登录" name="seller">
          <el-form
            ref="sellerFormRef"
            :model="sellerForm"
            :rules="sellerRules"
            label-width="80px"
          >
            <el-form-item label="用户名" prop="sellerName">
              <el-input
                v-model="sellerForm.sellerName"
                placeholder="请输入卖家用户名"
                prefix-icon="User"
              />
            </el-form-item>
            <el-form-item label="密码" prop="sellerPassword">
              <el-input
                v-model="sellerForm.sellerPassword"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="handleSellerLogin" :loading="loading">
                登录
              </el-button>
              <el-button @click="$router.push('/register')">注册账号</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { buyerAPI, sellerAPI } from '../api'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const activeTab = ref('buyer')
    const loading = ref(false)
    
    const buyerFormRef = ref()
    const sellerFormRef = ref()
    
    const buyerForm = reactive({
      buyerName: '',
      buyerPassword: ''
    })
    
    const sellerForm = reactive({
      sellerName: '',
      sellerPassword: ''
    })
    
    const buyerRules = {
      buyerName: [
        { required: true, message: '请输入买家用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
      ],
      buyerPassword: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
      ]
    }
    
    const sellerRules = {
      sellerName: [
        { required: true, message: '请输入卖家用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
      ],
      sellerPassword: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
      ]
    }
    
    const handleTabClick = () => {
      // 切换标签时重置表单
      buyerFormRef.value?.resetFields()
      sellerFormRef.value?.resetFields()
    }
    
    const handleBuyerLogin = async () => {
      try {
        await buyerFormRef.value.validate()
        loading.value = true
        
        const response = await buyerAPI.login(buyerForm)
        if (response.code === 200) {
          localStorage.setItem('token', 'buyer-token') // 实际项目中应该是真实的token
          localStorage.setItem('userInfo', JSON.stringify(response.data))
          localStorage.setItem('userType', 'buyer')
          ElMessage.success('买家登录成功')
          router.push('/')
        } else {
          ElMessage.error(response.msg || '登录失败')
        }
      } catch (error) {
        ElMessage.error('登录失败')
      } finally {
        loading.value = false
      }
    }
    
    const handleSellerLogin = async () => {
      try {
        await sellerFormRef.value.validate()
        loading.value = true
        
        const response = await sellerAPI.login(sellerForm)
        if (response.code === 200) {
          localStorage.setItem('token', 'seller-token') // 实际项目中应该是真实的token
          localStorage.setItem('userInfo', JSON.stringify(response.data))
          localStorage.setItem('userType', 'seller')
          ElMessage.success('卖家登录成功')
          router.push('/seller')
        } else {
          ElMessage.error(response.msg || '登录失败')
        }
      } catch (error) {
        ElMessage.error('登录失败')
      } finally {
        loading.value = false
      }
    }
    
    return {
      activeTab,
      loading,
      buyerFormRef,
      sellerFormRef,
      buyerForm,
      sellerForm,
      buyerRules,
      sellerRules,
      handleTabClick,
      handleBuyerLogin,
      handleSellerLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0;
  color: #333;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-button {
  width: 100%;
  margin-bottom: 10px;
}

.el-button + .el-button {
  margin-left: 0;
}
</style> 