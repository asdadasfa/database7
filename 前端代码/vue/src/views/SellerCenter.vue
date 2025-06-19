<template>
  <div class="seller-center">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <h2>卖家中心</h2>
          </template>
          
          <el-tabs v-model="activeTab">
            <el-tab-pane label="我的商品" name="goods">
              <div class="goods-header">
                <el-button type="primary" @click="showAddGoodsDialog">
                  <el-icon><Plus /></el-icon>
                  添加商品
                </el-button>
              </div>
              
              <el-table :data="myGoods" style="width: 100%">
                <el-table-column prop="goodsName" label="商品名称" />
                <el-table-column prop="type" label="类型" width="120" />
                <el-table-column prop="price" label="价格" width="120">
                  <template #default="scope">
                    ¥{{ scope.row.price }}
                  </template>
                </el-table-column>
                <el-table-column prop="num" label="库存" width="100" />
                <el-table-column label="操作" width="200">
                  <template #default="scope">
                    <el-button size="small" @click="editGoods(scope.row)">
                      编辑
                    </el-button>
                    <el-button 
                      type="danger" 
                      size="small" 
                      @click="deleteGoods(scope.row)"
                    >
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            
            <el-tab-pane label="个人信息" name="profile">
              <el-form
                ref="profileFormRef"
                :model="profileForm"
                :rules="profileRules"
                label-width="100px"
              >
                <el-form-item label="卖家ID" prop="sellerId">
                  <el-input v-model="profileForm.sellerId" disabled />
                </el-form-item>
                <el-form-item label="用户名" prop="sellerName">
                  <el-input v-model="profileForm.sellerName" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="profileForm.newPassword"
                    type="password"
                    placeholder="留空表示不修改密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="updateProfile" :loading="loading">
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      v-model="goodsDialogVisible"
      :title="isEdit ? '编辑商品' : '添加商品'"
      width="500px"
    >
      <el-form
        ref="goodsFormRef"
        :model="goodsForm"
        :rules="goodsRules"
        label-width="100px"
      >
        <el-form-item label="商品名称" prop="goodsName">
          <el-input v-model="goodsForm.goodsName" />
        </el-form-item>
        <el-form-item label="商品类型" prop="type">
          <el-select v-model="goodsForm.type" placeholder="请选择商品类型">
            <el-option label="电子产品" value="电子产品" />
            <el-option label="服装" value="服装" />
            <el-option label="食品" value="食品" />
            <el-option label="图书" value="图书" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="goodsForm.price"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="库存" prop="num">
          <el-input-number
            v-model="goodsForm.num"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="goodsDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveGoods" :loading="loading">
            {{ isEdit ? '更新' : '添加' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { sellerAPI, goodsAPI } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'SellerCenter',
  setup() {
    const activeTab = ref('goods')
    const loading = ref(false)
    const profileFormRef = ref()
    const goodsFormRef = ref()
    const myGoods = ref([])
    const goodsDialogVisible = ref(false)
    const isEdit = ref(false)
    
    const profileForm = reactive({
      sellerId: '',
      sellerName: '',
      newPassword: ''
    })
    
    const goodsForm = reactive({
      goodsId: '',
      goodsName: '',
      type: '',
      price: 0,
      num: 0
    })
    
    const profileRules = {
      sellerName: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
      ]
    }
    
    const goodsRules = {
      goodsName: [
        { required: true, message: '请输入商品名称', trigger: 'blur' }
      ],
      type: [
        { required: true, message: '请选择商品类型', trigger: 'change' }
      ],
      price: [
        { required: true, message: '请输入价格', trigger: 'blur' }
      ],
      num: [
        { required: true, message: '请输入库存', trigger: 'blur' }
      ]
    }
    
    const loadUserInfo = () => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      profileForm.sellerId = userInfo.sellerId || ''
      profileForm.sellerName = userInfo.sellerName || ''
    }
    
    const loadMyGoods = async () => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (!userInfo.sellerId) return
      
      try {
        const response = await goodsAPI.getGoodsBySellerId(userInfo.sellerId)
        if (response.code === 200) {
          myGoods.value = response.data
        }
      } catch (error) {
        console.error('加载商品失败:', error)
      }
    }
    
    const showAddGoodsDialog = () => {
      isEdit.value = false
      Object.keys(goodsForm).forEach(key => {
        goodsForm[key] = key === 'price' || key === 'num' ? 0 : ''
      })
      goodsDialogVisible.value = true
    }
    
    const editGoods = (goods) => {
      isEdit.value = true
      Object.keys(goodsForm).forEach(key => {
        goodsForm[key] = goods[key]
      })
      goodsDialogVisible.value = true
    }
    
    const saveGoods = async () => {
      try {
        await goodsFormRef.value.validate()
        loading.value = true
        
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const goodsData = {
          ...goodsForm,
          sellerId: userInfo.sellerId
        }
        
        let response
        if (isEdit.value) {
          response = await goodsAPI.updateGoods(goodsData)
        } else {
          response = await goodsAPI.addGoods(goodsData)
        }
        
        if (response.code === 200) {
          ElMessage.success(isEdit.value ? '商品更新成功' : '商品添加成功')
          goodsDialogVisible.value = false
          loadMyGoods()
        } else {
          ElMessage.error(response.msg || '操作失败')
        }
      } catch (error) {
        ElMessage.error('操作失败')
      } finally {
        loading.value = false
      }
    }
    
    const deleteGoods = async (goods) => {
      try {
        await ElMessageBox.confirm('确定要删除这个商品吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const response = await goodsAPI.deleteGoods(goods.goodsId)
        if (response.code === 200) {
          ElMessage.success('删除成功')
          loadMyGoods()
        } else {
          ElMessage.error(response.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }
    
    const updateProfile = async () => {
      try {
        await profileFormRef.value.validate()
        loading.value = true
        
        const updateData = {
          sellerId: profileForm.sellerId,
          sellerName: profileForm.sellerName
        }
        
        if (profileForm.newPassword) {
          updateData.sellerPassword = profileForm.newPassword
        }
        
        const response = await sellerAPI.update(updateData)
        if (response.code === 200) {
          ElMessage.success('个人信息更新成功')
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
          userInfo.sellerName = profileForm.sellerName
          localStorage.setItem('userInfo', JSON.stringify(userInfo))
          profileForm.newPassword = ''
        } else {
          ElMessage.error(response.msg || '更新失败')
        }
      } catch (error) {
        ElMessage.error('更新失败')
      } finally {
        loading.value = false
      }
    }
    
    onMounted(() => {
      loadUserInfo()
      loadMyGoods()
    })
    
    return {
      activeTab,
      loading,
      profileFormRef,
      goodsFormRef,
      profileForm,
      goodsForm,
      profileRules,
      goodsRules,
      myGoods,
      goodsDialogVisible,
      isEdit,
      showAddGoodsDialog,
      editGoods,
      saveGoods,
      deleteGoods,
      updateProfile
    }
  }
}
</script>

<style scoped>
.seller-center {
  max-width: 1200px;
  margin: 0 auto;
}

.goods-header {
  margin-bottom: 20px;
}

.el-form {
  max-width: 500px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 