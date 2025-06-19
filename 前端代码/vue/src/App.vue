<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'App',
  setup() {
    const router = useRouter()
    const activeIndex = ref('/')

    const isLoggedIn = computed(() => {
      return !!localStorage.getItem('token')
    })

    const logout = () => {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      ElMessage.success('退出成功')
      router.push('/login')
    }

    onMounted(() => {
      activeIndex.value = router.currentRoute.value.path
    })

    return {
      activeIndex,
      isLoggedIn,
      logout
    }
  }
}
</script>

<template>
  <div id="app">
    <el-container>
      <el-header>
        <el-menu
          :default-active="activeIndex"
          class="el-menu-demo"
          mode="horizontal"
          router
        >
          <el-menu-item index="/">
            <el-icon><House /></el-icon>
            首页
          </el-menu-item>
          <el-menu-item index="/goods">
            <el-icon><Goods /></el-icon>
            商品列表
          </el-menu-item>
          <el-menu-item index="/cart" v-if="isLoggedIn">
            <el-icon><ShoppingCart /></el-icon>
            购物车
          </el-menu-item>
          <div class="flex-grow" />
          <el-menu-item index="/user" v-if="isLoggedIn">
            <el-icon><User /></el-icon>
            个人中心
          </el-menu-item>
          <el-menu-item index="/seller" v-if="isLoggedIn">
            <el-icon><Shop /></el-icon>
            卖家中心
          </el-menu-item>
          <el-menu-item index="/login" v-if="!isLoggedIn">
            <el-icon><Key /></el-icon>
            登录
          </el-menu-item>
          <el-menu-item index="/register" v-if="!isLoggedIn">
            <el-icon><UserFilled /></el-icon>
            注册
          </el-menu-item>
          <el-menu-item @click="logout" v-if="isLoggedIn">
            <el-icon><SwitchButton /></el-icon>
            退出
          </el-menu-item>
        </el-menu>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

.el-header {
  padding: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.el-menu {
  border-bottom: none;
}

.flex-grow {
  flex-grow: 1;
}

.el-main {
  padding: 20px;
  min-height: calc(100vh - 60px);
}
</style>
