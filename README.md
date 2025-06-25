# 在线商城系统

## 项目简介

本项目是一个基于 **Spring Boot + MyBatis + MySQL + Vue3** 的全栈在线商城系统，支持买家、卖家和管理员多角色，涵盖商品浏览、购物车、订单、商品管理等完整电商业务流程。前后端分离，界面美观，功能完善，适合学习和二次开发。

---

## 技术栈

- **前端**：Vue 3、Vue Router、Axios、Vite、CSS3
- **后端**：Spring Boot 3、MyBatis、Spring MVC、Lombok、Redis
- **数据库**：MySQL 8
- **构建工具**：Maven

---

## 主要功能

### 买家端
- 用户注册/登录
- 商品浏览、搜索、筛选、详情查看
- 购物车管理
- 订单创建、支付、取消、历史订单查询
- 个人信息管理

### 卖家端
- 卖家注册/登录
- 商品管理（增删改查、图片上传、库存管理）
- 订单管理
- 卖家信息管理

### 管理员端
- 用户管理
- 商品管理
- 订单管理

---

## 项目结构

```
database7/
├── 前端代码/
│   └── vue/                # 前端Vue3项目
├── 后端服务代码/
│   └── database_cli/       # 后端Spring Boot项目
├── 数据库/
│   └── shop.sql            # MySQL建表及初始化数据
└── README.md
```

---

## 安装与运行

### 1. 数据库

1. 安装MySQL 8，并创建数据库 `shop`。
2. 执行 `数据库/shop.sql` 脚本，初始化表结构和测试数据。

### 2. 后端

```bash
cd 后端服务代码/database_cli
# 修改 src/main/resources/application.yml 数据库配置为你的本地信息
# 启动服务（需JDK 17+）
./mvnw spring-boot:run
# 默认端口：8686
```

### 3. 前端

```bash
cd 前端代码/vue
npm install
npm run dev
# 默认访问：http://localhost:5173
```

---

## 主要表结构（简要）

- `buyer`：买家用户
- `seller`：卖家用户
- `admin`：管理员
- `goods`：商品（含图片、库存、价格等）
- `cart`：购物车
- `order`：订单

详见 `数据库/shop.sql`。

---

## 常用API接口（部分）

- `/buyer/register`、`/buyer/login`、`/seller/register`、`/seller/login`
- `/goods/getAllGoods`、`/goods/getGoodsById`
- `/cart/addToCart`、`/cart/getCartContents`
- `/order/create`、`/order/pay`、`/order/cancel`、`/order/pending`、`/order/paid`



---

## 特色说明

- 商品图片支持上传与展示，静态资源统一管理
- 订单、购物车、商品等核心业务全覆盖
- 支持邮件通知（需配置邮箱）
- 代码结构清晰，便于二次开发

---

## 其他

- 前端静态资源位于 `前端代码/vue/public/`
- 商品图片上传后存储于 `uploaded_images/`
- 日志与开发文档见 `log/`

---

