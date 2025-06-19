import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8686',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// 买家相关API
export const buyerAPI = {
  // 买家注册
  register: (data) => api.post('/buyer/register', data),
  // 买家登录
  login: (data) => api.post('/buyer/login', data),
  // 买家注销
  logout: (buyerId) => api.post('/buyer/logout', null, { params: { buyerId } }),
  // 获取买家信息
  getInfo: (buyerId) => api.get(`/buyer/${buyerId}`),
  // 更新买家信息
  update: (data) => api.put('/buyer/update', data)
}

// 卖家相关API
export const sellerAPI = {
  // 卖家注册
  register: (data) => api.post('/seller/register', data),
  // 卖家登录
  login: (data) => api.post('/seller/login', data),
  // 卖家注销
  logout: (sellerId) => api.post('/seller/logout', null, { params: { sellerId } }),
  // 获取卖家信息
  getInfo: (sellerId) => api.get(`/seller/${sellerId}`),
  // 更新卖家信息
  update: (data) => api.put('/seller/update', data)
}

// 商品相关API
export const goodsAPI = {
  // 添加商品
  addGoods: (data) => api.post('/goods/addGoods', data),
  // 获取所有商品
  getAllGoods: () => api.get('/goods/getAllGoods'),
  // 根据ID获取商品
  getGoodsById: (goodsId) => api.get('/goods/getGoodsById', { params: { goodsId } }),
  // 根据卖家ID获取商品
  getGoodsBySellerId: (sellerId) => api.get('/goods/getGoodsBySellerId', { params: { sellerId } }),
  // 根据类型获取商品
  getGoodsByType: (type) => api.get('/goods/getGoodsByType', { params: { type } }),
  // 根据名称模糊查询
  getGoodsByNameLike: (goodsName) => api.get('/goods/getGoodsByNameLike', { params: { goodsName } }),
  // 根据价格范围查询
  getGoodsByPriceRange: (minPrice, maxPrice) => api.get('/goods/getGoodsByPriceRange', { params: { minPrice, maxPrice } }),
  // 更新商品信息
  updateGoods: (data) => api.put('/goods/updateGoods', data),
  // 更新商品库存
  updateGoodsStock: (goodsId, num) => api.put('/goods/updateGoodsStock', null, { params: { goodsId, num } }),
  // 删除商品
  deleteGoods: (goodsId) => api.delete('/goods/deleteGoods', { params: { goodsId } })
}

// 购物车相关API
export const cartAPI = {
  // 添加商品到购物车
  addToCart: (buyerId, goodsId, num) => api.post('/cart/addToCart', null, { params: { buyerId, goodsId, num } }),
  // 获取购物车内容
  getCartContents: (buyerId) => api.get('/cart/getCartContents', { params: { buyerId } }),
  // 更新购物车商品数量
  updateCartItem: (buyerId, goodsId, num) => api.put('/cart/updateCartItem', null, { params: { buyerId, goodsId, num } }),
  // 从购物车移除商品
  removeFromCart: (buyerId, goodsId) => api.delete('/cart/removeFromCart', { params: { buyerId, goodsId } }),
  // 清空购物车
  clearCart: (buyerId) => api.delete('/cart/clearCart', { params: { buyerId } })
}

export default api 