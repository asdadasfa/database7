import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import GoodsList from '../views/GoodsList.vue'
import GoodsDetail from '../views/GoodsDetail.vue'
import Cart from '../views/Cart.vue'
import Orders from '../views/Orders.vue'
import UserCenter from '../views/UserCenter.vue'
import SellerCenter from '../views/SellerCenter.vue'
import AdminCenter from '../views/AdminCenter.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/goods',
    name: 'GoodsList',
    component: GoodsList
  },
  {
    path: '/goods/:id',
    name: 'GoodsDetail',
    component: GoodsDetail
  },
  {
    path: '/cart',
    name: 'Cart',
    component: Cart,
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: Orders,
    meta: { requiresAuth: true }
  },
  {
    path: '/user',
    name: 'UserCenter',
    component: UserCenter,
    meta: { requiresAuth: true }
  },
  {
    path: '/seller',
    name: 'SellerCenter',
    component: SellerCenter,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'AdminCenter',
    component: AdminCenter
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router 