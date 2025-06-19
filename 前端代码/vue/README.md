# 电商平台前端

基于 Vue 3 + Element Plus 的现代化电商平台前端应用。

## 技术栈

- **Vue 3.5.13** - 渐进式 JavaScript 框架
- **Vue Router 4.2.5** - 官方路由管理器
- **Element Plus 2.4.4** - 基于 Vue 3 的组件库
- **Axios 1.6.0** - HTTP 客户端
- **Vite 6.3.5** - 下一代前端构建工具

## 功能特性

### 买家功能
- 用户注册/登录
- 商品浏览和搜索
- 商品详情查看
- 购物车管理
- 个人信息管理
- 订单查看

### 卖家功能
- 卖家注册/登录
- 商品管理（增删改查）
- 个人信息管理
- 销售数据查看

### 通用功能
- 响应式设计
- 路由守卫
- 状态管理
- 错误处理

## 项目结构

```
src/
├── api/           # API 接口
├── assets/        # 静态资源
├── components/    # 公共组件
├── router/        # 路由配置
├── views/         # 页面组件
│   ├── Home.vue           # 首页
│   ├── Login.vue          # 登录页
│   ├── Register.vue       # 注册页
│   ├── GoodsList.vue      # 商品列表
│   ├── GoodsDetail.vue    # 商品详情
│   ├── Cart.vue           # 购物车
│   ├── UserCenter.vue     # 用户中心
│   └── SellerCenter.vue   # 卖家中心
├── App.vue        # 根组件
└── main.js        # 入口文件
```

## 安装和运行

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

应用将在 `http://localhost:3000` 启动。

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产版本

```bash
npm run preview
```

## 环境配置

### 后端API配置

前端默认连接到 `http://localhost:8686` 的后端服务。如需修改，请编辑 `src/api/index.js` 中的 `baseURL`。

### 代理配置

开发环境下已配置代理，将 `/api` 请求转发到后端服务：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8686',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/api/, '')
  }
}
```

## 主要页面说明

### 首页 (Home.vue)
- 展示热门商品
- 提供快速导航
- 介绍平台特色

### 登录页 (Login.vue)
- 支持买家/卖家登录
- 表单验证
- 自动跳转

### 商品列表 (GoodsList.vue)
- 商品展示
- 搜索和筛选
- 分页功能
- 加入购物车

### 购物车 (Cart.vue)
- 购物车商品管理
- 数量修改
- 商品删除
- 结算功能

### 用户中心 (UserCenter.vue)
- 个人信息管理
- 订单查看
- 密码修改

### 卖家中心 (SellerCenter.vue)
- 商品管理
- 个人信息管理
- 销售数据

## API 接口

### 买家相关
- `POST /buyer/register` - 买家注册
- `POST /buyer/login` - 买家登录
- `PUT /buyer/update` - 更新买家信息

### 卖家相关
- `POST /seller/register` - 卖家注册
- `POST /seller/login` - 卖家登录
- `PUT /seller/update` - 更新卖家信息

### 商品相关
- `GET /goods/getAllGoods` - 获取所有商品
- `GET /goods/getGoodsById` - 根据ID获取商品
- `POST /goods/addGoods` - 添加商品
- `PUT /goods/updateGoods` - 更新商品
- `DELETE /goods/deleteGoods` - 删除商品

### 购物车相关
- `POST /cart/addToCart` - 添加到购物车
- `GET /cart/getCartContents` - 获取购物车内容
- `PUT /cart/updateCartItem` - 更新购物车商品
- `DELETE /cart/removeFromCart` - 从购物车移除
- `DELETE /cart/clearCart` - 清空购物车

## 开发指南

### 添加新页面
1. 在 `src/views/` 创建新的 Vue 组件
2. 在 `src/router/index.js` 添加路由配置
3. 在 `src/api/index.js` 添加相关 API 接口

### 样式规范
- 使用 Element Plus 组件库
- 遵循 BEM 命名规范
- 响应式设计优先

### 状态管理
- 使用 localStorage 存储用户信息
- 使用 Vue 3 Composition API
- 避免全局状态管理

## 注意事项

1. 确保后端服务已启动并运行在 `http://localhost:8686`
2. 登录状态通过 localStorage 管理
3. 所有 API 请求都包含错误处理
4. 表单验证使用 Element Plus 内置验证

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 许可证

MIT License
