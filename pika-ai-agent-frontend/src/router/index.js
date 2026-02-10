import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoveAppView from '../views/LoveAppView.vue'
import SuperAgentView from '../views/SuperAgentView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/love-app',
      name: 'love-app',
      component: LoveAppView
    },
    {
      path: '/super-agent',
      name: 'super-agent',
      component: SuperAgentView
    }
  ]
})

export default router
