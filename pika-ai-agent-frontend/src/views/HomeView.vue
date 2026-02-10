<template>
  <div class="home">
    <div class="hero">
      <h1>Pika AI Agent</h1>
      <p>探索AI的无限可能，体验智能对话的乐趣</p>
    </div>
    
    <div class="app-cards">
      <div class="app-card" @click="navigateTo('/love-app')">
        <div class="card-icon">
          <img src="@/assets/images/love-ai-avatar.svg" alt="AI恋爱大师" class="ai-avatar" />
        </div>
        <h2>AI恋爱大师</h2>
        <p>体验浪漫的AI恋爱对话，让AI成为你的恋爱顾问</p>
      </div>
      
      <div class="app-card" @click="navigateTo('/super-agent')">
        <div class="card-icon">
          <img src="@/assets/images/super-agent-avatar.svg" alt="AI超级智能体" class="ai-avatar" />
        </div>
        <h2>AI超级智能体</h2>
        <p>与强大的AI智能体对话，解决各种问题和挑战</p>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'HomeView',
  setup() {
    const router = useRouter()
    
    const navigateTo = (path) => {
      router.push(path)
    }
    
    return {
      navigateTo
    }
  }
}
</script>

<style scoped>
.home {
  text-align: center;
  padding: var(--spacing-xl) 0;
}

.hero {
  margin-bottom: var(--spacing-xxl);
}

.hero h1 {
  font-size: var(--font-size-xxl);
  margin-bottom: var(--spacing-md);
  color: var(--primary-color);
  font-weight: 700;
}

.hero p {
  font-size: var(--font-size-lg);
  color: var(--gray-color);
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

.app-cards {
  display: flex;
  justify-content: center;
  gap: var(--spacing-xl);
  flex-wrap: wrap;
}

.app-card {
  background-color: var(--white-color);
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-xl);
  width: 300px;
  box-shadow: var(--shadow-md);
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
  cursor: pointer;
}

.app-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-lg);
}

.card-icon {
  margin-bottom: var(--spacing-md);
  display: flex;
  justify-content: center;
}

.ai-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
}

.app-card h2 {
  font-size: var(--font-size-xl);
  margin-bottom: var(--spacing-sm);
  color: var(--dark-color);
}

.app-card p {
  color: var(--gray-color);
  line-height: 1.5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero h1 {
    font-size: var(--font-size-xl);
  }
  
  .hero p {
    font-size: var(--font-size-base);
  }
  
  .app-cards {
    gap: var(--spacing-md);
  }
  
  .app-card {
    width: 280px;
    padding: var(--spacing-lg);
  }
}

@media (max-width: 576px) {
  .app-card {
    width: 100%;
    max-width: 320px;
  }
}
</style>
