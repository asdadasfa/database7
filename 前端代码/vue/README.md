# 电商平台前端

这是一个基于Vue 3的电商平台前端项目，提供完整的购物功能。

## 功能特性

### 用户功能
- **用户注册/登录**: 支持买家和卖家分别注册登录
- **商品浏览**: 查看所有商品，支持搜索和筛选
- **商品详情**: 查看商品详细信息，支持图片展示
- **购物车**: 添加商品到购物车，管理购物车内容
- **订单管理**: 创建订单、支付、取消订单
- **个人中心**: 管理个人信息

### 卖家功能
- **商品管理**: 添加、编辑、删除商品
- **图片上传**: 支持商品图片上传
- **库存管理**: 管理商品库存
- **卖家中心**: 管理卖家信息和商品

### 技术特性
- **响应式设计**: 支持移动端和桌面端
- **现代化UI**: 美观的用户界面
- **实时搜索**: 商品名称实时搜索
- **状态管理**: 完整的订单状态管理

## 项目结构

```
src/
├── api/           # API接口配置
├── components/    # 公共组件
├── router/        # 路由配置
├── utils/         # 工具函数
├── views/         # 页面组件
│   ├── Home.vue           # 首页
│   ├── Login.vue          # 登录页
│   ├── Register.vue       # 注册页
│   ├── GoodsList.vue      # 商品列表
│   ├── GoodsDetail.vue    # 商品详情
│   ├── Cart.vue           # 购物车
│   ├── Orders.vue         # 订单管理
│   ├── UserCenter.vue     # 用户中心
│   └── SellerCenter.vue   # 卖家中心
└── main.js        # 应用入口
```

## 安装和运行

### 安装依赖
```bash
npm install
```

### 开发环境运行
```bash
npm run dev
```

### 生产环境构建
```bash
npm run build
```

## API接口

项目使用以下后端API接口：

### 用户相关
- `POST /buyer/register` - 买家注册
- `POST /buyer/login` - 买家登录
- `POST /seller/register` - 卖家注册
- `POST /seller/login` - 卖家登录

### 商品相关
- `GET /goods/getAllGoods` - 获取所有商品
- `GET /goods/getGoodsById` - 根据ID获取商品
- `POST /goods/addGoods` - 添加商品
- `PUT /goods/updateGoods` - 更新商品
- `DELETE /goods/deleteGoods` - 删除商品

### 购物车相关
- `POST /cart/addToCart` - 添加到购物车
- `GET /cart/getCartContents` - 获取购物车内容
- `PUT /cart/updateCartItem` - 更新购物车商品数量
- `DELETE /cart/removeFromCart` - 从购物车移除商品

### 订单相关
- `POST /order/create` - 创建订单
- `POST /order/pay` - 支付订单
- `POST /order/cancel` - 取消订单
- `GET /order/pending` - 获取待支付订单
- `GET /order/paid` - 获取已支付订单

## 使用说明

### 买家使用流程
1. 注册/登录买家账号
2. 浏览商品列表，使用搜索和筛选功能
3. 点击商品查看详情
4. 添加到购物车或立即购买
5. 在购物车中管理商品，进行结算
6. 在订单页面管理订单状态

### 卖家使用流程
1. 注册/登录卖家账号
2. 进入卖家中心
3. 添加商品，上传图片
4. 管理商品库存和信息
5. 查看订单状态

## 技术栈

- **Vue 3**: 前端框架
- **Vue Router**: 路由管理
- **Axios**: HTTP客户端
- **CSS3**: 样式和动画
- **ES6+**: JavaScript语法

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## 开发说明

### 代码规范
- 使用Vue 3 Composition API
- 组件使用PascalCase命名
- 文件使用kebab-case命名
- 使用ES6+语法

### 样式规范
- 使用scoped样式
- 响应式设计优先
- 统一的颜色和间距规范

## 部署

### 开发环境
```bash
npm run dev
```

### 生产环境
```bash
npm run build
npm run preview
```

## 注意事项

1. 确保后端服务运行在 `http://localhost:8686`
2. 图片上传功能需要后端支持文件上传
3. 订单超时处理需要后端定时任务支持
4. 建议使用现代浏览器以获得最佳体验

## 更新日志

### v1.0.0
- 初始版本发布
- 完整的电商功能实现
- 响应式设计支持
- 用户和卖家功能分离
