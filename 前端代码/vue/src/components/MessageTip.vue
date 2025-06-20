<template>
  <div v-if="visible" :class="['message-tip', type]" :style="position">
    {{ message }}
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'MessageTip',
  props: {
    message: {
      type: String,
      required: true
    },
    type: {
      type: String,
      default: 'info',
      validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
    },
    duration: {
      type: Number,
      default: 3000
    },
    position: {
      type: Object,
      default: () => ({ top: '20px', right: '20px' })
    }
  },
  emits: ['close'],
  setup(props, { emit }) {
    const visible = ref(true)

    onMounted(() => {
      if (props.duration > 0) {
        setTimeout(() => {
          visible.value = false
          emit('close')
        }, props.duration)
      }
    })

    return {
      visible
    }
  }
}
</script>

<style scoped>
.message-tip {
  position: fixed;
  z-index: 9999;
  padding: 12px 20px;
  border-radius: 4px;
  color: white;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  animation: slideIn 0.3s ease;
}

.message-tip.success {
  background: #67c23a;
}

.message-tip.error {
  background: #f56c6c;
}

.message-tip.warning {
  background: #e6a23c;
}

.message-tip.info {
  background: #909399;
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
</style> 