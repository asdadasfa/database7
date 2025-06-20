<template>
  <div class="register-page">
    <div class="register-card">
      <h2 class="register-title">用户注册</h2>
      <div class="tab-group">
        <button :class="['tab-btn', activeTab === 'buyer' ? 'active' : '']" @click="activeTab = 'buyer'">买家注册</button>
        <button :class="['tab-btn', activeTab === 'seller' ? 'active' : '']" @click="activeTab = 'seller'">卖家注册</button>
      </div>
      <form v-if="activeTab === 'buyer'" class="register-form" @submit.prevent="handleBuyerRegister">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="buyerForm.buyerName" type="text" required placeholder="请输入买家用户名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="buyerForm.buyerPassword" type="password" required placeholder="请输入密码" />
        </div>
        <div class="form-actions">
          <button class="main-btn" type="submit" :disabled="loading">注册</button>
          <button class="main-btn info" type="button" @click="$router.push('/login')">去登录</button>
        </div>
      </form>
      <form v-else class="register-form" @submit.prevent="handleSellerRegister">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="sellerForm.sellerName" type="text" required placeholder="请输入卖家用户名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="sellerForm.sellerPassword" type="password" required placeholder="请输入密码" />
        </div>
        <div class="form-actions">
          <button class="main-btn" type="submit" :disabled="loading">注册</button>
          <button class="main-btn info" type="button" @click="$router.push('/login')">去登录</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { buyerAPI, sellerAPI } from '../api'
import Message from '../utils/message'

const router = useRouter()
const activeTab = ref('buyer')
const loading = ref(false)

const buyerForm = ref({ buyerName: '', buyerPassword: '' })
const sellerForm = ref({ sellerName: '', sellerPassword: '' })

const handleBuyerRegister = async () => {
  if (!buyerForm.value.buyerName || !buyerForm.value.buyerPassword) {
    Message.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  const response = await buyerAPI.register(buyerForm.value)
  if (response.code === 200) {
    Message.success('注册成功，请登录')
    router.push('/login')
  } else {
    Message.error(response.msg || '注册失败')
  }
  loading.value = false
}

const handleSellerRegister = async () => {
  if (!sellerForm.value.sellerName || !sellerForm.value.sellerPassword) {
    Message.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  const response = await sellerAPI.register(sellerForm.value)
  if (response.code === 200) {
    Message.success('注册成功，请登录')
    router.push('/login')
  } else {
    Message.error(response.msg || '注册失败')
  }
  loading.value = false
}
</script>

<style scoped>
.register-page {
  width: 100vw;
  min-height: calc(100vh - 60px);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  background: transparent;
}
.register-card {
  width: 90vw;
  max-width: 500px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  padding: 32px 24px 24px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 40px;
}
.register-title {
  font-size: 1.6em;
  color: #222;
  margin-bottom: 18px;
}
.tab-group {
  display: flex;
  width: 100%;
  margin-bottom: 18px;
}
.tab-btn {
  flex: 1;
  padding: 10px 0;
  background: #f5f6fa;
  border: none;
  border-radius: 6px 6px 0 0;
  color: #409eff;
  font-size: 1em;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.tab-btn.active {
  background: #409eff;
  color: #fff;
}
.register-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.form-group label {
  color: #666;
  font-size: 0.98em;
}
.form-group input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1em;
  outline: none;
  transition: border 0.2s;
}
.form-group input:focus {
  border: 1.5px solid #409eff;
}
.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}
.main-btn {
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 8px 24px;
  font-size: 1.1em;
  cursor: pointer;
  transition: background 0.2s;
}
.main-btn:hover {
  background: #3076c9;
}
.main-btn.info {
  background: #909399;
}
.main-btn.info:hover {
  background: #606266;
}
@media (max-width: 600px) {
  .register-card {
    padding: 18px 6px 12px 6px;
    max-width: 98vw;
    margin-top: 16px;
  }
  .register-title {
    font-size: 1.2em;
  }
  .main-btn {
    font-size: 1em;
    padding: 8px 12px;
  }
}
</style> 