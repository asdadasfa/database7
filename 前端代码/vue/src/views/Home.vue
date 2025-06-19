<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <h1>欢迎来到电商平台</h1>
          <p>发现优质商品，享受购物乐趣</p>
          <el-button type="primary" size="large" @click="$router.push('/goods')">
            开始购物
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="24">
        <h2>热门商品</h2>
        <el-row :gutter="20">
          <el-col :span="6" v-for="goods in featuredGoods" :key="goods.goodsId">
            <el-card :body-style="{ padding: '0px' }" class="goods-card">
              <img :src="goods.goodsImage || '/default-goods.jpg'" class="goods-image">
              <div style="padding: 14px;">
                <h3>{{ goods.goodsName }}</h3>
                <p>类型: {{ goods.type }}</p>
                <p class="price">¥{{ goods.price }}</p>
                <div class="bottom">
                  <el-button type="primary" size="small" @click="viewDetail(goods.goodsId)">
                    查看详情
                  </el-button>
                  <el-button type="success" size="small" @click="addToCart(goods)">
                    加入购物车
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card>
          <h3>买家服务</h3>
          <ul>
            <li>浏览商品</li>
            <li>加入购物车</li>
            <li>下单购买</li>
            <li>查看订单</li>
          </ul>
          <el-button type="primary" @click="$router.push('/register')">注册买家</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <h3>卖家服务</h3>
          <ul>
            <li>发布商品</li>
            <li>管理库存</li>
            <li>处理订单</li>
            <li>查看销售</li>
          </ul>
          <el-button type="success" @click="$router.push('/register')">注册卖家</el-button>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <h3>平台特色</h3>
          <ul>
            <li>安全交易</li>
            <li>快速配送</li>
            <li>售后保障</li>
            <li>用户评价</li>
          </ul>
          <el-button type="info" @click="$router.push('/goods')">了解更多</el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { goodsAPI, cartAPI } from '../api'
import { ElMessage } from 'element-plus'

export default {
  name: 'Home',
  setup() {
    const router = useRouter()
    const featuredGoods = ref([])

    const loadFeaturedGoods = async () => {
      try {
        const response = await goodsAPI.getAllGoods()
        if (response.code === 200) {
          featuredGoods.value = response.data.slice(0, 4) // 显示前4个商品
        }
      } catch (error) {
        console.error('加载商品失败:', error)
      }
    }

    const viewDetail = (goodsId) => {
      router.push(`/goods/${goodsId}`)
    }

    const addToCart = async (goods) => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (!userInfo.buyerId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }

      try {
        const response = await cartAPI.addToCart(userInfo.buyerId, goods.goodsId, 1)
        if (response.code === 200) {
          ElMessage.success('已添加到购物车')
        } else {
          ElMessage.error(response.msg || '添加失败')
        }
      } catch (error) {
        ElMessage.error('添加失败')
      }
    }

    onMounted(() => {
      loadFeaturedGoods()
    })

    return {
      featuredGoods,
      viewDetail,
      addToCart
    }
  }
}
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-card h1 {
  font-size: 2.5em;
  margin-bottom: 10px;
}

.welcome-card p {
  font-size: 1.2em;
  margin-bottom: 20px;
}

.goods-card {
  margin-bottom: 20px;
  transition: transform 0.3s;
}

.goods-card:hover {
  transform: translateY(-5px);
}

.goods-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.price {
  color: #e74c3c;
  font-size: 1.2em;
  font-weight: bold;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
  display: flex;
  justify-content: space-between;
}

ul {
  list-style: none;
  padding: 0;
}

li {
  padding: 5px 0;
  border-bottom: 1px solid #eee;
}

li:last-child {
  border-bottom: none;
}
</style> 