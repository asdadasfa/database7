import { createVNode, render } from 'vue'
import MessageTip from '../components/MessageTip.vue'

let messageInstance = null
let messageCount = 0

const createMessage = (options) => {
  const container = document.createElement('div')
  const vnode = createVNode(MessageTip, {
    ...options,
    onClose: () => {
      render(null, container)
      document.body.removeChild(container)
      messageInstance = null
    }
  })
  
  render(vnode, container)
  document.body.appendChild(container)
  messageInstance = vnode
  messageCount++
  
  return vnode
}

const Message = {
  success(message, duration = 3000) {
    return createMessage({
      message,
      type: 'success',
      duration
    })
  },
  
  error(message, duration = 3000) {
    return createMessage({
      message,
      type: 'error',
      duration
    })
  },
  
  warning(message, duration = 3000) {
    return createMessage({
      message,
      type: 'warning',
      duration
    })
  },
  
  info(message, duration = 3000) {
    return createMessage({
      message,
      type: 'info',
      duration
    })
  }
}

export default Message 