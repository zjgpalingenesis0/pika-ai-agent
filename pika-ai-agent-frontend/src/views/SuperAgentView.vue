<template>
  <div class="chat-container">
    <div class="chat-header">
      <div class="ai-info">
        <img src="@/assets/images/super-agent-avatar.svg" alt="AI超级智能体" class="ai-avatar" />
        <div class="ai-details">
          <h2>AI超级智能体</h2>
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
          <img src="@/assets/images/super-agent-avatar.svg" alt="AI超级智能体" class="ai-avatar" />
        </div>
        <div class="message-content">{{ message.content }}</div>
        <div class="message-time">{{ formatTime(message.timestamp) }}</div>
      </div>
      
      <div v-if="isTyping" class="message ai-message typing">
        <div class="message-avatar">
          <img src="@/assets/images/super-agent-avatar.svg" alt="AI超级智能体" class="ai-avatar" />
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
        placeholder="输入你的消息..." 
        @keydown.enter.prevent="sendMessage"
        rows="3"
      ></textarea>
      <button @click="sendMessage" :disabled="!inputMessage.trim() || isTyping">发送</button>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'

export default {
  name: 'SuperAgentView',
  setup() {
    const messages = ref([])
    const inputMessage = ref('')
    const isTyping = ref(false)
    const chatId = ref('')
    const messagesContainer = ref(null)
    
    // 生成聊天室ID
    const generateChatId = () => {
      return 'agent_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
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
          `http://localhost:8123/api/ai/agent/love_app?messages=${encodeURIComponent(messageContent)}`
        )
        
        let aiResponse = ''
        
        eventSource.onmessage = (event) => {
          // 每个step返回在不同的聊天气泡中显示
          messages.value.push({
            content: event.data,
            sender: 'ai',
            timestamp: Date.now(),
            isStreaming: true
          })
          
          scrollToBottom()
        }
        
        eventSource.onerror = () => {
          eventSource.close()
          isTyping.value = false
          
          // 标记流式响应结束
          const lastMessageIndex = messages.value.findIndex(m => m.sender === 'ai' && m.isStreaming)
          if (lastMessageIndex !== -1) {
            messages.value[lastMessageIndex].isStreaming = false
          }
        }
        
        eventSource.onclose = () => {
          isTyping.value = false
          
          // 标记流式响应结束
          const lastMessageIndex = messages.value.findIndex(m => m.sender === 'ai' && m.isStreaming)
          if (lastMessageIndex !== -1) {
            messages.value[lastMessageIndex].isStreaming = false
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
        content: '您好！我是AI超级智能体，可以帮助您解决各种复杂问题。请问有什么可以帮助您的吗？',
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
  background-color: #f8f9fa; /* 浅灰白色背景，简约清新 */
  border: 1px solid #e9ecef; /* 淡灰色边框 */
  border-radius: 12px; /* 简约的圆角 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); /* 轻微阴影 */
  overflow: hidden;
}

.chat-header {
  background-color: var(--agent-color);
  color: var(--white-color);
  padding: var(--spacing-md);
  position: relative;
}

.chat-header::after {
  content: '✨';
  position: absolute;
  right: var(--spacing-md);
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.2rem;
  animation: sparkle 2s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% { opacity: 1; transform: translateY(-50%) scale(1); }
  50% { opacity: 0.7; transform: translateY(-50%) scale(1.2); }
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
  border: 2px solid rgba(255, 255, 255, 0.3); /* 半透明白色边框 */
}

.ai-details h2 {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: 600; /* 稍微加粗 */
}

.ai-details p {
  margin: 0;
  opacity: 0.8;
  font-size: var(--font-size-sm);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  background-color: #ffffff; /* 纯白背景，简约 */
  border-top: 1px solid #e9ecef;
  border-bottom: 1px solid #e9ecef;
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
  background-color: #f8f9fa; /* 浅灰白色背景，简约 */
  color: var(--dark-color);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: 16px; /* 更圆润的气泡 */
  line-height: 1.5;
  word-wrap: break-word;
  white-space: pre-wrap;
  overflow-wrap: break-word;
  max-width: 100%;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05); /* 轻微阴影 */
  font-size: var(--font-size-base);
}

.user-message .message-content {
  background-color: var(--agent-color);
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
  border-top: 1px solid #e9ecef;
  background-color: #f8f9fa; /* 浅灰白色背景，简约 */
  gap: var(--spacing-sm);
}

textarea {
  flex: 1;
  border: 1px solid #e9ecef;
  border-radius: 16px; /* 更圆润的边角 */
  padding: var(--spacing-sm) var(--spacing-md);
  resize: none;
  font-family: inherit;
  font-size: var(--font-size-base);
  transition: all 0.3s;
  background-color: #ffffff;
}

textarea:focus {
  border-color: var(--agent-color);
  outline: none;
  box-shadow: 0 0 0 3px rgba(106, 90, 205, 0.1); /* 淡紫色焦点 */
}

button {
  background-color: var(--agent-color);
  color: var(--white-color);
  border: none;
  border-radius: 16px; /* 更圆润的边角 */
  padding: 0 var(--spacing-lg);
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(106, 90, 205, 0.2); /* 紫色阴影 */
}

button:hover:not(:disabled) {
  background-color: #5a4fc7; /* 稍微深一点的紫色 */
  transform: translateY(-1px); /* 轻微上移 */
  box-shadow: 0 4px 12px rgba(106, 90, 205, 0.3);
}

button:disabled {
  background-color: #e9ecef;
  color: #6c757d;
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
