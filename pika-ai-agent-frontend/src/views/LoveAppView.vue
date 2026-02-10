<template>
  <div class="chat-container">
    <div class="chat-header">
      <div class="ai-info">
        <img src="@/assets/images/love-ai-avatar.svg" alt="AI恋爱大师" class="ai-avatar" />
        <div class="ai-details">
          <h2>AI恋爱大师</h2>
          <p>聊天室ID: {{ chatId }}</p>
        </div>
      </div>
    </div>
    
    <div class="chat-messages" ref="messagesContainer">
      <div 
        v-for="(message, index) in messages" 
        :key="index" 
        :class="['message', message.sender === 'user' ? 'user-message' : 'ai-message']"
      >
        <div v-if="message.sender === 'ai'" class="message-avatar">
          <img src="@/assets/images/love-ai-avatar.svg" alt="AI恋爱大师" class="ai-avatar" />
        </div>
        <div class="message-content">{{ message.content }}</div>
        <div class="message-time">{{ formatTime(message.timestamp) }}</div>
      </div>
      
      <div v-if="isTyping" class="message ai-message typing">
        <div class="message-avatar">
          <img src="@/assets/images/love-ai-avatar.svg" alt="AI恋爱大师" class="ai-avatar" />
        </div>
        <div class="typing-indicator">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <textarea 
        v-model="inputMessage" 
        placeholder="输入您的问题..." 
        @keydown.enter.prevent="sendMessage"
        :disabled="isTyping"
      ></textarea>
      <button @click="sendMessage" :disabled="isTyping || !inputMessage.trim()">发送</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'

export default {
  name: 'LoveAppView',
  setup() {
    const messages = ref([])
    const inputMessage = ref('')
    const isTyping = ref(false)
    const chatId = ref('')
    const messagesContainer = ref(null)
    
    // 生成聊天室ID
    const generateChatId = () => {
      return 'love_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    }
    
    // 格式化时间
    const formatTime = (timestamp) => {
      const date = new Date(timestamp)
      return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    }
    
    // 滚动到底部
    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }
    
    // 发送消息
    const sendMessage = async () => {
      if (!inputMessage.value.trim() || isTyping.value) return
      
      const userMessage = {
        content: inputMessage.value,
        sender: 'user',
        timestamp: Date.now()
      }
      
      messages.value.push(userMessage)
      const messageContent = inputMessage.value
      inputMessage.value = ''
      isTyping.value = true
      scrollToBottom()
      
      try {
        // 使用SSE方式调用后端接口
        const eventSource = new EventSource(
          `http://localhost:8123/api/ai/love_app/chat/SseEmitter?message=${encodeURIComponent(messageContent)}&chatId=${chatId.value}`
        )
        
        let aiResponse = ''
        
        // 创建一个AI消息气泡用于接收流式响应
        const aiMessageIndex = messages.value.length
        messages.value.push({
          content: '',
          sender: 'ai',
          timestamp: Date.now(),
          isStreaming: true
        })
        
        eventSource.onmessage = (event) => {
          // 在同一个消息气泡中持续拼接消息，实现打字机效果
          messages.value[aiMessageIndex].content += event.data
          scrollToBottom()
        }
        
        eventSource.onerror = () => {
          eventSource.close()
          isTyping.value = false
          
          // 标记流式响应结束
          if (messages.value[aiMessageIndex]) {
            messages.value[aiMessageIndex].isStreaming = false
          }
        }
        
        eventSource.onclose = () => {
          isTyping.value = false
          
          // 标记流式响应结束
          if (messages.value[aiMessageIndex]) {
            messages.value[aiMessageIndex].isStreaming = false
          }
        }
      } catch (error) {
        console.error('发送消息失败:', error)
        isTyping.value = false
        
        messages.value.push({
          content: '抱歉，发送消息时出现错误，请稍后再试。',
          sender: 'ai',
          timestamp: Date.now()
        })
        
        scrollToBottom()
      }
    }
    
    onMounted(() => {
      chatId.value = generateChatId()
      
      // 添加欢迎消息
      messages.value.push({
        content: '您好！我是AI恋爱大师，很高兴为您服务。请问有什么可以帮助您的吗？',
        sender: 'ai',
        timestamp: Date.now()
      })
    })
    
    return {
      messages,
      inputMessage,
      isTyping,
      chatId,
      messagesContainer,
      sendMessage,
      formatTime
    }
  }
}
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 80vh;
  max-width: 800px;
  margin: 0 auto;
  background-color: #fff5f8; /* 浅粉色背景，营造初恋氛围 */
  border: 2px solid #ffb6c1; /* 浅粉色边框 */
  border-radius: 20px; /* 更圆润的边角 */
  box-shadow: 0 8px 24px rgba(255, 182, 193, 0.3); /* 粉色阴影 */
  overflow: hidden;
  position: relative;
}

.chat-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 5px;
  background: linear-gradient(90deg, #ff9a9e, #fad0c4, #ff9a9e); /* 粉色渐变 */
}

.chat-header {
  background: linear-gradient(135deg, #ff9a9e, #fad0c4); /* 粉色渐变背景 */
  color: var(--white-color);
  padding: var(--spacing-md);
  position: relative;
}

.chat-header::after {
  content: '❤️';
  position: absolute;
  right: var(--spacing-md);
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.2rem;
  animation: heartbeat 1.5s ease-in-out infinite;
}

@keyframes heartbeat {
  0% { transform: translateY(-50%) scale(1); }
  50% { transform: translateY(-50%) scale(1.1); }
  100% { transform: translateY(-50%) scale(1); }
}

.ai-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.7); /* 白色边框 */
}

.ai-details h2 {
  margin: 0;
  font-size: var(--font-size-lg);
  font-family: 'Georgia', serif; /* 更浪漫的字体 */
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.ai-details p {
  margin: 0;
  opacity: 0.8;
  font-size: var(--font-size-sm);
  font-style: italic; /* 斜体增加浪漫感 */
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  background-color: #fff5f8; /* 浅粉色背景 */
  border-top: 1px solid rgba(255, 182, 193, 0.3);
  border-bottom: 1px solid rgba(255, 182, 193, 0.3);
  position: relative;
}

.chat-messages::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 182, 193, 0.5), transparent);
}

.message {
  display: flex;
  gap: var(--spacing-sm);
  max-width: 70%;
}

.user-message {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.ai-message {
  align-self: flex-start;
}

.message-avatar {
  flex-shrink: 0;
}

.message-avatar .ai-avatar {
  width: 32px;
  height: 32px;
}

.message-content {
  background-color: #ffffff;
  color: var(--dark-color);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: 18px; /* 更圆润的气泡 */
  line-height: 1.5;
  word-wrap: break-word;
  white-space: pre-wrap;
  overflow-wrap: break-word;
  max-width: 100%;
  box-shadow: 0 2px 8px rgba(255, 182, 193, 0.2); /* 粉色阴影 */
  position: relative;
  font-family: 'Georgia', serif; /* 更浪漫的字体 */
}

.ai-message .message-content::before {
  content: '💕';
  position: absolute;
  top: -8px;
  left: 10px;
  font-size: 0.8rem;
}

.user-message .message-content {
  background: linear-gradient(135deg, #ff9a9e, #fad0c4); /* 粉色渐变 */
  color: var(--white-color);
  font-weight: 500;
}

.message-time {
  font-size: var(--font-size-xs);
  opacity: 0.7;
  margin-top: var(--spacing-xs);
  text-align: right;
}

.typing {
  background-color: var(--gray-light-color);
  color: var(--dark-color);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-lg);
  max-width: 70%;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: var(--gray-color);
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

.chat-input {
  display: flex;
  padding: var(--spacing-md);
  border-top: 2px solid rgba(255, 182, 193, 0.5);
  background-color: #fff5f8; /* 浅粉色背景 */
  gap: var(--spacing-sm);
  position: relative;
}

.chat-input::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 182, 193, 0.5), transparent);
}

.chat-input textarea {
  flex: 1;
  border: 2px solid rgba(255, 182, 193, 0.3);
  border-radius: 20px; /* 更圆润的边角 */
  padding: var(--spacing-sm) var(--spacing-md);
  resize: none;
  font-family: 'Georgia', serif; /* 更浪漫的字体 */
  outline: none;
  min-height: 40px;
  max-height: 120px;
  font-size: var(--font-size-base);
  transition: all 0.3s;
  background-color: rgba(255, 255, 255, 0.7);
}

.chat-input textarea:focus {
  border-color: #ff9a9e;
  box-shadow: 0 0 0 3px rgba(255, 154, 158, 0.2);
}

.chat-input button {
  background: linear-gradient(135deg, #ff9a9e, #fad0c4); /* 粉色渐变 */
  color: var(--white-color);
  border: none;
  border-radius: 20px; /* 更圆润的边角 */
  padding: 0 var(--spacing-lg);
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.3);
  position: relative;
  overflow: hidden;
}

.chat-input button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s;
}

.chat-input button:hover:not(:disabled)::before {
  left: 100%;
}

.chat-input button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 154, 158, 0.4);
}

.chat-input button:disabled {
  background: linear-gradient(135deg, #f0f0f0, #e0e0e0);
  cursor: not-allowed;
  box-shadow: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-container {
    height: 85vh;
    margin: 0 var(--spacing-md);
  }
  
  .message {
    max-width: 80%;
  }
}

@media (max-width: 576px) {
  .chat-container {
    height: 90vh;
    margin: 0 var(--spacing-sm);
    border-radius: var(--border-radius-lg);
  }
  
  .chat-header {
    padding: var(--spacing-sm) var(--spacing-md);
  }
  
  .ai-avatar {
    width: 32px;
    height: 32px;
  }
  
  .ai-details h2 {
    font-size: var(--font-size-base);
  }
  
  .message {
    max-width: 90%;
  }
  
  .chat-input {
    padding: var(--spacing-sm);
  }
  
  textarea {
    font-size: var(--font-size-sm);
  }
}
</style>
