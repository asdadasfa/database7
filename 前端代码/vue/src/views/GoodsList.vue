<template>
  <div class="goods-list-page">
    <h2 class="page-title">全部商品</h2>
    <div class="goods-list">
      <div class="goods-card" v-for="goods in goodsList" :key="goods.goodsId">
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
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { goodsAPI, cartAPI } from '../api/index.js'
import Message from '../utils/message'

const router = useRouter()
const goodsList = ref([])

const loadGoods = async () => {
  const res = await goodsAPI.getAllGoods()
  if (res.code === 200) {
    goodsList.value = res.data
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

onMounted(loadGoods)
</script>

<style scoped>
.goods-list-page {
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
.page-title {
  font-size: 1.6em;
  color: #222;
  margin-bottom: 18px;
  text-align: left;
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
@media (max-width: 900px) {
  .goods-list {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
  .goods-image {
    max-width: 140px;
    height: 110px;
  }
}
@media (max-width: 600px) {
  .goods-list-page {
    padding: 10px;
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
  .page-title {
    font-size: 1.2em;
  }
}
</style> 