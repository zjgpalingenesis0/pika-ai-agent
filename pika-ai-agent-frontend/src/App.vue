<template>
  <div id="app">
    <!-- 都市热血粒子效果 -->
    <div class="urban-particles">
      <div class="urban-particle"></div>
      <div class="urban-particle"></div>
      <div class="urban-particle"></div>
      <div class="urban-particle"></div>
      <div class="urban-particle"></div>
      <div class="urban-particle"></div>
      <div class="urban-particle"></div>
      <div class="urban-particle"></div>
      <div class="urban-particle"></div>
      <div class="urban-particle"></div>
    </div>
    <header class="app-header">
      <div class="container">
        <div class="logo">
          <h1>Pika AI Agent</h1>
        </div>
        <nav class="app-nav">
          <router-link to="/" class="nav-link">主页</router-link>
          <router-link to="/love-app" class="nav-link">AI恋爱大师</router-link>
          <router-link to="/super-agent" class="nav-link">AI超级智能体</router-link>
        </nav>
      </div>
    </header>
    <main class="app-main">
      <div class="container">
        <router-view />
      </div>
    </main>
    <footer class="app-footer">
      <div class="container">
        <div class="footer-content">
          <div class="copyright">
            <p>&copy; 2025 Pika AI Agent. 站长: wanfeng</p>
          </div>
          <div class="links">
            <span>友情链接：</span>
            <a href="https://www.baidu.com" target="_blank" rel="noopener noreferrer">百度</a>
            <a href="https://www.csdn.net" target="_blank" rel="noopener noreferrer">CSDN</a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
:root {
  --primary-color: #4a6cf7;
  --white-color: #ffffff;
  --text-color: #333333;
  --bg-color: #f5f5f5;
  --shadow-md: 0 2px 10px rgba(0, 0, 0, 0.1);
  --border-radius-md: 6px;
  --transition-normal: 0.3s;
  --spacing-xs: 0.25rem;
  --spacing-sm: 0.5rem;
  --spacing-md: 1rem;
  --spacing-lg: 1.5rem;
  --spacing-xl: 2rem;
  --font-size-sm: 0.875rem;
  --font-size-md: 1rem;
  --font-size-lg: 1.25rem;
  --font-size-xl: 1.5rem;

  /* 都市热血风格颜色 */
  --urban-red: #ff3b30;
  --urban-orange: #ff9500;
  --urban-yellow: #ffcc00;
  --urban-purple: #af52de;
  --urban-blue: #007aff;
  --urban-dark: #1c1c1e;
}

/* 都市热血动态背景 */
body {
  margin: 0;
  padding: 0;
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--text-color);
  background-color: var(--bg-color);
  position: relative;
  overflow-x: hidden;
}

body::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #1c1c1e 0%, #2c2c2e 50%, #3a3a3c 100%);
  z-index: -2;
}

body::after {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    radial-gradient(circle at 20% 30%, rgba(255, 59, 48, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(255, 149, 0, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 40% 80%, rgba(255, 204, 0, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 60% 20%, rgba(175, 82, 222, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 90% 40%, rgba(0, 122, 255, 0.2) 0%, transparent 50%);
  z-index: -1;
  animation: urbanPulse 8s ease-in-out infinite;
}

@keyframes urbanPulse {
  0%, 100% {
    opacity: 0.7;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
}

/* 都市热血粒子效果 */
.urban-particles {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: -1;
  overflow: hidden;
}

.urban-particle {
  position: absolute;
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  animation: float 15s infinite linear;
}

.urban-particle:nth-child(1) { left: 10%; animation-delay: 0s; background-color: rgba(255, 59, 48, 0.6); }
.urban-particle:nth-child(2) { left: 20%; animation-delay: 1s; background-color: rgba(255, 149, 0, 0.6); }
.urban-particle:nth-child(3) { left: 30%; animation-delay: 2s; background-color: rgba(255, 204, 0, 0.6); }
.urban-particle:nth-child(4) { left: 40%; animation-delay: 3s; background-color: rgba(175, 82, 222, 0.6); }
.urban-particle:nth-child(5) { left: 50%; animation-delay: 4s; background-color: rgba(0, 122, 255, 0.6); }
.urban-particle:nth-child(6) { left: 60%; animation-delay: 5s; background-color: rgba(255, 59, 48, 0.6); }
.urban-particle:nth-child(7) { left: 70%; animation-delay: 6s; background-color: rgba(255, 149, 0, 0.6); }
.urban-particle:nth-child(8) { left: 80%; animation-delay: 7s; background-color: rgba(255, 204, 0, 0.6); }
.urban-particle:nth-child(9) { left: 90%; animation-delay: 8s; background-color: rgba(175, 82, 222, 0.6); }
.urban-particle:nth-child(10) { left: 15%; animation-delay: 9s; background-color: rgba(0, 122, 255, 0.6); }

@keyframes float {
  0% {
    transform: translateY(100vh) translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) translateX(100px);
    opacity: 0;
  }
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Arial', sans-serif;
  background-color: var(--bg-color);
  color: var(--text-color);
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 1;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-md);
  width: 100%;
}

.app-header {
  background-color: rgba(28, 28, 30, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  padding: var(--spacing-md) 0;
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.app-header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo h1 {
  font-size: var(--font-size-xl);
  font-weight: 600;
  background: linear-gradient(90deg, var(--urban-red), var(--urban-orange), var(--urban-yellow), var(--urban-purple), var(--urban-blue));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

.app-nav {
  display: flex;
  gap: var(--spacing-lg);
}

.nav-link {
  color: var(--white-color);
  text-decoration: none;
  font-weight: 500;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-md);
  transition: background-color var(--transition-normal);
}

.nav-link:hover,
.nav-link.router-link-active {
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--white-color);
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.2);
}

.app-main {
  flex: 1;
  padding: var(--spacing-xl) 0;
}

.app-footer {
  background-color: rgba(28, 28, 30, 0.9);
  backdrop-filter: blur(10px);
  color: rgba(255, 255, 255, 0.8);
  padding: var(--spacing-lg) 0;
  margin-top: auto;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.copyright p {
  margin: 0;
}

.links {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.links a {
  color: var(--urban-blue);
  text-decoration: none;
  transition: all 0.3s;
  position: relative;
}

.links a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--urban-red), var(--urban-orange), var(--urban-yellow), var(--urban-purple), var(--urban-blue));
  transition: width 0.3s;
}

.links a:hover::after {
  width: 100%;
}

.links a:hover {
  color: var(--urban-orange);
  text-shadow: 0 0 5px rgba(255, 149, 0, 0.5);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-header .container {
    flex-direction: column;
    gap: var(--spacing-md);
  }
  
  .app-nav {
    width: 100%;
    justify-content: center;
  }
  
  .logo h1 {
    font-size: var(--font-size-lg);
  }
}

@media (max-width: 576px) {
  .app-nav {
    flex-wrap: wrap;
    gap: var(--spacing-sm);
  }
  
  .nav-link {
    padding: var(--spacing-xs) var(--spacing-sm);
    font-size: var(--font-size-sm);
  }
}
</style>
