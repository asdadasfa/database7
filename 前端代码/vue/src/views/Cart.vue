<template>
  <div class="cart">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="cart-header">
              <h2>我的购物车</h2>
              <el-button type="danger" @click="clearCart" :disabled="!cartItems.length">
                清空购物车
              </el-button>
            </div>
          </template>
          
          <div v-if="!cartItems.length" class="empty-cart">
            <el-empty description="购物车是空的">
              <el-button type="primary" @click="$router.push('/goods')">
                去购物
              </el-button>
            </el-empty>
          </div>
          
          <div v-else>
            <el-table :data="cartItems" style="width: 100%">
              <el-table-column prop="goodsName" label="商品名称" width="200">
                <template #default="scope">
                  <div class="goods-info">
                    <img :src="scope.row.goodsImage || '/default-goods.jpg'" class="goods-thumb">
                    <span>{{ scope.row.goodsName }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="120" />
              <el-table-column prop="price" label="单价" width="120">
                <template #default="scope">
                  ¥{{ scope.row.price }}
                </template>
              </el-table-column>
              <el-table-column prop="num" label="数量" width="150">
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.num"
                    :min="1"
                    :max="999"
                    size="small"
                    @change="(value) => updateQuantity(scope.row, value)"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="sum" label="小计" width="120">
                <template #default="scope">
                  ¥{{ scope.row.sum }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button
                    type="danger"
                    size="small"
                    @click="removeItem(scope.row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="cart-summary">
              <div class="summary-info">
                <p>商品总数: {{ totalItems }} 件</p>
                <p class="total-price">总金额: ¥{{ totalAmount }}</p>
              </div>
              <div class="summary-actions">
                <el-button @click="$router.push('/goods')">继续购物</el-button>
                <el-button type="primary" @click="checkout">结算</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { cartAPI } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'Cart',
  setup() {
    const router = useRouter()
    const cartItems = ref([])
    const loading = ref(false)
    
    const totalItems = computed(() => {
      return cartItems.value.reduce((total, item) => total + item.num, 0)
    })
    
    const totalAmount = computed(() => {
      return cartItems.value.reduce((total, item) => total + item.sum, 0).toFixed(2)
    })
    
    const loadCart = async () => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (!userInfo.buyerId) {
        ElMessage.warning('请先登录')
        router.push('/login')
        return
      }
      
      try {
        loading.value = true
        const response = await cartAPI.getCartContents(userInfo.buyerId)
        if (response.code === 200) {
          cartItems.value = response.data.cartItems || []
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
        ElMessage.error('加载购物车失败')
      } finally {
        loading.value = false
      }
    }
    
    const updateQuantity = async (item, newQuantity) => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      try {
        const response = await cartAPI.updateCartItem(userInfo.buyerId, item.goodsId, newQuantity)
        if (response.code === 200) {
          // 更新本地数据
          item.num = newQuantity
          item.sum = (item.price * newQuantity).toFixed(2)
          ElMessage.success('数量更新成功')
        } else {
          ElMessage.error(response.msg || '更新失败')
          // 恢复原数量
          await loadCart()
        }
      } catch (error) {
        ElMessage.error('更新失败')
        await loadCart()
      }
    }
    
    const removeItem = async (item) => {
      try {
        await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const response = await cartAPI.removeFromCart(userInfo.buyerId, item.goodsId)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          await loadCart()
        } else {
          ElMessage.error(response.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    const clearCart = async () => {
      try {
        await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const response = await cartAPI.clearCart(userInfo.buyerId)
        if (response.code === 200) {
          ElMessage.success('购物车已清空')
          cartItems.value = []
        } else {
          ElMessage.error(response.msg || '清空失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('清空失败')
        }
      }
    }
    
    const checkout = () => {
      if (cartItems.value.length === 0) {
        ElMessage.warning('购物车是空的')
        return
      }
      
      ElMessage.info('结算功能开发中...')
      // 这里可以跳转到结算页面
      // router.push('/checkout')
    }
    
    onMounted(() => {
      loadCart()
    })
    
    return {
      cartItems,
      loading,
      totalItems,
      totalAmount,
      loadCart,
      updateQuantity,
      removeItem,
      clearCart,
      checkout
    }
  }
}
</script>

<style scoped>
.cart {
  max-width: 1200px;
  margin: 0 auto;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-header h2 {
  margin: 0;
}

.empty-cart {
  text-align: center;
  padding: 40px 0;
}

.goods-info {
  display: flex;
  align-items: center;
}

.goods-thumb {
  width: 50px;
  height: 50px;
  object-fit: cover;
  margin-right: 10px;
  border-radius: 4px;
}

.cart-summary {
  margin-top: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-info p {
  margin: 5px 0;
}

.total-price {
  font-size: 1.2em;
  font-weight: bold;
  color: #e74c3c;
}

.summary-actions {
  display: flex;
  gap: 10px;
}

@media (max-width: 768px) {
  .cart-summary {
    flex-direction: column;
    gap: 15px;
  }
  
  .summary-actions {
    width: 100%;
    justify-content: center;
  }
}
</style> 