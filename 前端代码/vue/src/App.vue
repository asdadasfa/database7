<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Message from './utils/message'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const route = useRoute()

    const isLoggedIn = computed(() => {
      return !!localStorage.getItem('token')
    })

    const userType = ref(localStorage.getItem('userType'))

    // 监听登录状态变化
    const updateUserType = () => {
      userType.value = localStorage.getItem('userType')
    }
    onMounted(() => {
      window.addEventListener('storage', updateUserType)
    })

    const logout = () => {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('userType')
      Message.success('退出成功')
      userType.value = null
      router.push('/login')
      // 强制刷新页面
      setTimeout(() => {
        window.location.reload()
      }, 100)
    }

    // 判断当前路由是否激活
    const isActive = (path) => {
      // 精确匹配
      return route.path === path
    }

    return {
      isLoggedIn,
      userType,
      logout,
      isActive
    }
  }
}
</script>

<template>
  <div id="app">
    <div class="app-container">
      <header class="app-header">
        <nav class="nav-menu">
          <div class="nav-left">
            <!-- 管理员登录后不显示左侧导航 -->
            <template v-if="!isLoggedIn || userType !== 'admin'">
              <router-link to="/" class="nav-item" :class="{ active: isActive('/') }">
                🏠 首页
              </router-link>
              <router-link to="/goods" class="nav-item" :class="{ active: isActive('/goods') }">
                📦 商品列表
              </router-link>
              <router-link v-if="isLoggedIn && userType === 'buyer'" to="/cart" class="nav-item" :class="{ active: isActive('/cart') }">
                🛒 购物车
              </router-link>
              <router-link v-if="isLoggedIn && userType === 'buyer'" to="/orders" class="nav-item" :class="{ active: isActive('/orders') }">
                📋 我的订单
              </router-link>
            </template>
          </div>
          
          <div class="nav-right">
            <!-- 管理员登录后只显示退出按钮 -->
            <template v-if="isLoggedIn && userType === 'admin'">
              <button @click="logout" class="nav-item logout-btn">
                🚪 退出
              </button>
            </template>
            <!-- 其他用户类型的导航 -->
            <template v-else>
              <router-link v-if="isLoggedIn && userType === 'buyer'" to="/user" class="nav-item" :class="{ active: isActive('/user') }">
                👤 个人中心
              </router-link>
              <router-link v-if="isLoggedIn && userType === 'seller'" to="/seller" class="nav-item" :class="{ active: isActive('/seller') }">
                🏪 卖家中心
              </router-link>
              <router-link v-if="!isLoggedIn" to="/login" class="nav-item" :class="{ active: isActive('/login') }">
                🔑 登录
              </router-link>
              <router-link v-if="!isLoggedIn" to="/register" class="nav-item" :class="{ active: isActive('/register') }">
                📝 注册
              </router-link>
              <button v-if="isLoggedIn && userType !== 'admin'" @click="logout" class="nav-item logout-btn">
                🚪 退出
              </button>
            </template>
          </div>
        </nav>
      </header>
      
      <main class="app-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  min-height: 100vh;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  width: 100vw;
  left: 0;
  right: 0;
  margin-left: calc(50% - 50vw);
  min-width: 100vw;
}

.nav-menu {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background: transparent;
  box-sizing: border-box;
}

.nav-menu {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.nav-left,
.nav-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.nav-item {
  text-decoration: none;
  color: #333;
  padding: 8px 16px;
  border-radius: 4px;
  transition: all 0.3s;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-item:hover {
  background: #f5f5f5;
  color: #409eff;
}

.nav-item.active {
  background: #409eff;
  color: white;
}

.logout-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-family: inherit;
  font-size: inherit;
}

.logout-btn:hover {
  background: #f56c6c !important;
  color: white !important;
}

.app-main {
  flex: 1;
  padding: 20px;
  background: #f5f5f5;
  min-height: calc(100vh - 60px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-menu {
    flex-direction: column;
    height: auto;
    padding: 10px 20px;
  }
  
  .nav-left,
  .nav-right {
    width: 100%;
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .nav-item {
    font-size: 12px;
    padding: 6px 12px;
  }
}
</style>
