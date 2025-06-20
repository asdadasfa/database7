<template>
  <div class="home">
    <div class="welcome">
      <h1>欢迎来到电商平台</h1>
      <p>发现优质商品，享受购物乐趣</p>
      <button class="main-btn" @click="$router.push('/goods')">开始购物</button>
    </div>
    <div class="featured">
      <h2>热门商品</h2>
      <div class="goods-list">
        <div class="goods-card" v-for="goods in featuredGoods" :key="goods.goodsId">
          <img :src="goods.images && goods.images[0] ? goods.images[0] : '/default-goods.jpg'" class="goods-image" />
          <h3>{{ goods.goodsName }}</h3>
          <p>类型: {{ goods.type }}</p>
          <p class="price">￥{{ goods.price }}</p>
          <div class="actions">
            <button @click="viewDetail(goods.goodsId)">查看详情</button>
            <button @click="addToCart(goods)">加入购物车</button>
          </div>
        </div>
      </div>
    </div>

    <div class="info-section">
      <div class="info-card">
        <h3>买家服务</h3>
        <ul>
          <li>浏览商品</li>
          <li>加入购物车</li>
          <li>下单购买</li>
          <li>查看订单</li>
        </ul>
        <button class="info-btn primary" @click="$router.push('/register')">注册买家</button>
      </div>
      <div class="info-card">
        <h3>卖家服务</h3>
        <ul>
          <li>发布商品</li>
          <li>管理库存</li>
          <li>处理订单</li>
          <li>查看销售</li>
        </ul>
        <button class="info-btn success" @click="$router.push('/register')">注册卖家</button>
      </div>
      <div class="info-card">
        <h3>平台特色</h3>
        <ul>
          <li>安全交易</li>
          <li>快速配送</li>
          <li>售后保障</li>
          <li>用户评价</li>
        </ul>
        <button class="info-btn info" @click="$router.push('/goods')">了解更多</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { goodsAPI, cartAPI } from '../api/index.js'
import Message from '../utils/message'

const router = useRouter()
const featuredGoods = ref([])

const loadFeaturedGoods = async () => {
  const res = await goodsAPI.getAllGoods()
  if (res.code === 200) {
    featuredGoods.value = res.data.slice(0, 4)
  }
}

const viewDetail = (goodsId) => {
  router.push(`/goods/${goodsId}`)
}

const addToCart = async (goods) => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  if (!userInfo.buyerId) {
    Message.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const response = await cartAPI.addToCart(userInfo.buyerId, goods.goodsId, 1)
    if (response.code === 200) {
      Message.success('已添加到购物车')
    } else {
      Message.error(response.msg || '添加失败')
    }
  } catch (error) {
    Message.error('添加失败')
  }
}

onMounted(loadFeaturedGoods)
</script>

<style scoped>
:global(body) {
  background: #f5f6fa;
  min-height: 100vh;
  margin: 0;
}
:global(#app) {
  min-height: 100vh;
  background: #f5f6fa;
}
.home {
  width: 90vw;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 60px);
  display: flex;
  flex-direction: column;
  background: transparent;
  box-sizing: border-box;
}

.welcome {
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  padding: 40px 10px 30px 10px;
  margin-bottom: 30px;
}
.welcome h1 {
  font-size: 2.5em;
  margin-bottom: 10px;
}
.welcome p {
  font-size: 1.2em;
  margin-bottom: 20px;
}
.main-btn {
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 12px 32px;
  font-size: 1.1em;
  cursor: pointer;
  transition: background 0.2s;
}
.main-btn:hover {
  background: #3076c9;
}

.featured {
  margin-top: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}
.featured h2 {
  margin-bottom: 18px;
  font-size: 1.5em;
  color: #333;
}
.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 24px;
  flex: 1;
  width: 100%;
}
.goods-card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
  padding: 18px 14px 20px 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: box-shadow 0.2s, transform 0.2s;
  min-height: 320px;
}
.goods-card:hover {
  box-shadow: 0 6px 18px rgba(64, 158, 255, 0.15);
  transform: translateY(-4px) scale(1.03);
}
.goods-image {
  width: 100%;
  max-width: 200px;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 12px;
}
.goods-card h3 {
  font-size: 1.1em;
  margin: 8px 0 4px 0;
  color: #222;
  text-align: center;
}
.goods-card p {
  margin: 2px 0;
  color: #666;
  font-size: 0.98em;
}
.price {
  color: #e74c3c;
  font-size: 1.15em;
  font-weight: bold;
  margin-bottom: 8px;
}
.actions {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}
.actions button {
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 5px;
  padding: 7px 16px;
  font-size: 0.98em;
  cursor: pointer;
  transition: background 0.2s;
}
.actions button:hover {
  background: #3076c9;
}

.info-section {
  display: flex;
  gap: 24px;
  margin-top: 32px;
  margin-bottom: 10px;
}
.info-card {
  flex: 1;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
  padding: 24px 18px 18px 18px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 0;
}
.info-card h3 {
  margin-bottom: 12px;
  color: #409eff;
}
.info-card ul {
  list-style: none;
  padding: 0;
  margin-bottom: 16px;
  width: 100%;
}
.info-card li {
  padding: 5px 0;
  border-bottom: 1px solid #eee;
  text-align: center;
  color: #666;
  font-size: 0.98em;
}
.info-card li:last-child {
  border-bottom: none;
}
.info-btn {
  border: none;
  border-radius: 5px;
  padding: 8px 20px;
  font-size: 1em;
  cursor: pointer;
  margin-top: 8px;
  color: #fff;
  transition: background 0.2s;
}
.info-btn.primary {
  background: #409eff;
}
.info-btn.primary:hover {
  background: #3076c9;
}
.info-btn.success {
  background: #67c23a;
}
.info-btn.success:hover {
  background: #4cae1c;
}
.info-btn.info {
  background: #909399;
}
.info-btn.info:hover {
  background: #606266;
}

@media (max-width: 900px) {
  .goods-list {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
  .goods-image {
    max-width: 140px;
    height: 110px;
  }
  .info-section {
    flex-direction: column;
    gap: 16px;
  }
}

@media (max-width: 600px) {
  .home {
    padding: 10px;
  }
  .welcome {
    padding: 24px 6px 18px 6px;
  }
  .goods-list {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 12px;
  }
  .goods-card {
    padding: 10px 4px 12px 4px;
    min-height: 180px;
  }
  .goods-image {
    max-width: 100%;
    height: 80px;
  }
  .featured h2 {
    font-size: 1.1em;
  }
}

</style> 