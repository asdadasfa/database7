<template>
  <div class="admin-center">
    <div v-if="!isLoggedIn" class="admin-login">
      <h2>管理员登录</h2>
      <form @submit.prevent="handleLogin">
        <input v-model="loginForm.id" placeholder="管理员账号" required class="form-control" />
        <input v-model="loginForm.password" type="password" placeholder="密码" required class="form-control" />
        <button type="submit" class="main-btn">登录</button>
      </form>
      <div v-if="loginError" class="error-msg">{{ loginError }}</div>
    </div>
    <div v-else class="admin-panel">
      <div class="admin-header">
        <h2>管理员后台</h2>
      </div>
      <div class="admin-tabs">
        <button :class="['tab-btn', {active: tab==='buyers'}]" @click="tab='buyers'">买家管理</button>
        <button :class="['tab-btn', {active: tab==='sellers'}]" @click="tab='sellers'">卖家管理</button>
        <button :class="['tab-btn', {active: tab==='goods'}]" @click="tab='goods'">商品管理</button>
      </div>
      <div class="tab-content">
        <!-- 买家管理 -->
        <div v-if="tab==='buyers'">
          <h3>买家列表</h3>
          <table class="table">
            <thead><tr><th>ID</th><th>用户名</th><th>状态</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="buyer in buyers" :key="buyer.buyerId">
                <td>{{ buyer.buyerId }}</td>
                <td>{{ buyer.buyerName }}</td>
                <td>{{ buyer.bool ? '正常' : '已删除' }}</td>
                <td>
                  <button v-if="buyer.bool" @click="deleteBuyer(buyer.buyerId)" class="btn btn-danger">删除</button>
                  <button v-else @click="restoreBuyer(buyer.buyerId)" class="btn btn-success">恢复</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div class="pagination" v-if="buyerTotal > buyerPageSize">
            <button :disabled="buyerPage===1" @click="changeBuyerPage(buyerPage-1)">上一页</button>
            <span>第 {{buyerPage}} / {{Math.ceil(buyerTotal/buyerPageSize)}} 页</span>
            <button :disabled="buyerPage===Math.ceil(buyerTotal/buyerPageSize)" @click="changeBuyerPage(buyerPage+1)">下一页</button>
          </div>
        </div>
        <!-- 卖家管理 -->
        <div v-if="tab==='sellers'">
          <h3>卖家列表</h3>
          <table class="table">
            <thead><tr><th>ID</th><th>用户名</th><th>状态</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="seller in sellers" :key="seller.sellerId">
                <td>{{ seller.sellerId }}</td>
                <td>{{ seller.sellerName }}</td>
                <td>{{ seller.bool ? '正常' : '已删除' }}</td>
                <td>
                  <button v-if="seller.bool" @click="deleteSeller(seller.sellerId)" class="btn btn-danger">删除</button>
                  <button v-else @click="restoreSeller(seller.sellerId)" class="btn btn-success">恢复</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div class="pagination" v-if="sellerTotal > sellerPageSize">
            <button :disabled="sellerPage===1" @click="changeSellerPage(sellerPage-1)">上一页</button>
            <span>第 {{sellerPage}} / {{Math.ceil(sellerTotal/sellerPageSize)}} 页</span>
            <button :disabled="sellerPage===Math.ceil(sellerTotal/sellerPageSize)" @click="changeSellerPage(sellerPage+1)">下一页</button>
          </div>
        </div>
        <!-- 商品管理 -->
        <div v-if="tab==='goods'">
          <h3>商品列表</h3>
          <table class="table">
            <thead><tr><th>ID</th><th>名称</th><th>卖家ID</th><th>类型</th><th>价格</th><th>库存</th><th>状态</th><th>操作</th></tr></thead>
            <tbody>
              <tr v-for="goods in goodsList" :key="goods.goodsId">
                <td>{{ goods.goodsId }}</td>
                <td>{{ goods.goodsName }}</td>
                <td>{{ goods.sellerId }}</td>
                <td>{{ goods.type }}</td>
                <td>{{ goods.price }}</td>
                <td>{{ goods.num }}</td>
                <td>{{ goods.bool ? '正常' : '已删除' }}</td>
                <td>
                  <button v-if="goods.bool" @click="deleteGoods(goods.goodsId)" class="btn btn-danger">删除</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div class="pagination" v-if="goodsTotal > goodsPageSize">
            <button :disabled="goodsPage===1" @click="changeGoodsPage(goodsPage-1)">上一页</button>
            <span>第 {{goodsPage}} / {{Math.ceil(goodsTotal/goodsPageSize)}} 页</span>
            <button :disabled="goodsPage===Math.ceil(goodsTotal/goodsPageSize)" @click="changeGoodsPage(goodsPage+1)">下一页</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { adminAPI, goodsAPI } from '../api'

const isLoggedIn = ref(false)
const loginForm = reactive({ id: '', password: '' })
const loginError = ref('')
const tab = ref('buyers')
const buyers = ref([])
const sellers = ref([])
const goodsList = ref([])
const buyerPage = ref(1)
const buyerPageSize = 5
const buyerTotal = ref(0)
const sellerPage = ref(1)
const sellerPageSize = 5
const sellerTotal = ref(0)
const goodsPage = ref(1)
const goodsPageSize = 5
const goodsTotal = ref(0)

// 检查是否已经登录
onMounted(() => {
  const userType = localStorage.getItem('userType')
  const token = localStorage.getItem('token')
  if (userType === 'admin' && token) {
    isLoggedIn.value = true
    loadAll()
  }
})

const handleLogin = async () => {
  loginError.value = ''
  const res = await adminAPI.login(loginForm.id, loginForm.password)
  if (res.code === 200) {
    isLoggedIn.value = true
    localStorage.setItem('token', res.data.token || 'admin-token')
    localStorage.setItem('userType', 'admin')
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    loadAll()
    setTimeout(() => {
      window.location.reload()
    }, 100)
  } else {
    loginError.value = res.msg || '登录失败'
  }
}
const goBack = () => {
  window.history.back();
}
const loadAll = async () => {
  const [buyerRes, sellerRes, goodsRes] = await Promise.all([
    adminAPI.getAllBuyersPaged(buyerPage.value, buyerPageSize),
    adminAPI.getAllSellersPaged(sellerPage.value, sellerPageSize),
    goodsAPI.getAllGoodsPaged(goodsPage.value, goodsPageSize)
  ])
  buyers.value = buyerRes.data.data || []
  buyerTotal.value = buyerRes.data.total || 0
  sellers.value = sellerRes.data.data || []
  sellerTotal.value = sellerRes.data.total || 0
  goodsList.value = goodsRes.data.data || []
  goodsTotal.value = goodsRes.data.total || 0
}
const deleteBuyer = async (buyerId) => {
  await adminAPI.deleteBuyer(buyerId)
  loadAll()
}
const restoreBuyer = async (buyerId) => {
  await adminAPI.restoreBuyer(buyerId)
  loadAll()
}
const deleteSeller = async (sellerId) => {
  await adminAPI.deleteSeller(sellerId)
  loadAll()
}
const restoreSeller = async (sellerId) => {
  await adminAPI.restoreSeller(sellerId)
  loadAll()
}
const deleteGoods = async (goodsId) => {
  await adminAPI.deleteGoods(goodsId)
  loadAll()
}
const changeBuyerPage = (page) => { buyerPage.value = page; loadAll() }
const changeSellerPage = (page) => { sellerPage.value = page; loadAll() }
const changeGoodsPage = (page) => { goodsPage.value = page; loadAll() }
</script>

<style scoped>
.admin-center {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 10px;
}
.admin-login {
  max-width: 400px;
  margin: 60px auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 30px 24px;
}
.admin-login h2 {
  text-align: center;
  margin-bottom: 18px;
}
.form-control {
  width: 100%;
  margin-bottom: 16px;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1em;
}
.main-btn {
  width: 100%;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 10px 0;
  font-size: 1.1em;
  cursor: pointer;
  transition: background 0.2s;
}
.main-btn:hover {
  background: #3076c9;
}
.main-btn.danger {
  background: #f56c6c;
}
.main-btn.danger:hover {
  background: #c0392b;
}
.error-msg {
  color: #e74c3c;
  text-align: center;
  margin-top: 10px;
}
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}
.admin-header h2 {
  font-size: 2.4em;
  font-weight: bold;
  letter-spacing: 2px;
  color: #222;
  margin: 0;
  padding: 0;
}
.admin-tabs {
  display: flex;
  gap: 16px;
  margin-bottom: 18px;
}
.tab-btn {
  padding: 8px 18px;
  border: none;
  background: #f5f5f5;
  border-radius: 6px 6px 0 0;
  font-size: 1.1em;
  cursor: pointer;
  transition: background 0.2s;
}
.tab-btn.active {
  background: #409eff;
  color: #fff;
}
.tab-content {
  background: #fff;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.07);
  padding: 24px 12px;
}
.table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}
.table th, .table td {
  border: 1px solid #eee;
  padding: 8px 6px;
  text-align: center;
}
.table th {
  background: #f5f5f5;
}
.table tr:nth-child(even) {
  background: #fafbfc;
}
.table button {
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 4px 12px;
  cursor: pointer;
  margin: 0 2px;
  transition: background 0.2s;
}
.table button:hover {
  background: #3076c9;
}

.btn {
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  cursor: pointer;
  margin: 0 2px;
  transition: background 0.2s;
  font-size: 0.9em;
}

.btn:hover {
  background: #3076c9;
}

.btn-danger {
  background: #f56c6c;
}

.btn-danger:hover {
  background: #c0392b;
}

.btn-success {
  background: #67c23a;
}

.btn-success:hover {
  background: #5aad2e;
}
</style> 