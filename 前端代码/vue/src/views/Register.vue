<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <h2>用户注册</h2>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="买家注册" name="buyer">
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
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="buyerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleBuyerRegister" :loading="loading">
                注册
              </el-button>
              <el-button @click="$router.push('/login')">已有账号？去登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="卖家注册" name="seller">
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
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="sellerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="handleSellerRegister" :loading="loading">
                注册
              </el-button>
              <el-button @click="$router.push('/login')">已有账号？去登录</el-button>
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
  name: 'Register',
  setup() {
    const router = useRouter()
    const activeTab = ref('buyer')
    const loading = ref(false)
    
    const buyerFormRef = ref()
    const sellerFormRef = ref()
    
    const buyerForm = reactive({
      buyerName: '',
      buyerPassword: '',
      confirmPassword: ''
    })
    
    const sellerForm = reactive({
      sellerName: '',
      sellerPassword: '',
      confirmPassword: ''
    })
    
    const validateConfirmPassword = (rule, value, callback) => {
      const currentForm = activeTab.value === 'buyer' ? buyerForm : sellerForm
      if (value !== currentForm[activeTab.value === 'buyer' ? 'buyerPassword' : 'sellerPassword']) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }
    
    const buyerRules = {
      buyerName: [
        { required: true, message: '请输入买家用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
      ],
      buyerPassword: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
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
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
      ]
    }
    
    const handleTabClick = () => {
      buyerFormRef.value?.resetFields()
      sellerFormRef.value?.resetFields()
    }
    
    const handleBuyerRegister = async () => {
      try {
        await buyerFormRef.value.validate()
        loading.value = true
        
        const response = await buyerAPI.register({
          buyerName: buyerForm.buyerName,
          buyerPassword: buyerForm.buyerPassword
        })
        
        if (response.code === 200) {
          ElMessage.success('买家注册成功，请登录')
          router.push('/login')
        } else {
          ElMessage.error(response.msg || '注册失败')
        }
      } catch (error) {
        ElMessage.error('注册失败')
      } finally {
        loading.value = false
      }
    }
    
    const handleSellerRegister = async () => {
      try {
        await sellerFormRef.value.validate()
        loading.value = true
        
        const response = await sellerAPI.register({
          sellerName: sellerForm.sellerName,
          sellerPassword: sellerForm.sellerPassword
        })
        
        if (response.code === 200) {
          ElMessage.success('卖家注册成功，请登录')
          router.push('/login')
        } else {
          ElMessage.error(response.msg || '注册失败')
        }
      } catch (error) {
        ElMessage.error('注册失败')
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
      handleBuyerRegister,
      handleSellerRegister
    }
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
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