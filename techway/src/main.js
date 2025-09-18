// src/main.js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'

import App from './App.vue'
import router from './router'
import i18n from './i18n'
import './style.css'
import 'leaflet/dist/leaflet.css'


// مهم: نفعل الجلسة من التخزين قبل ما يشتغل الراوتر
import { useAuthStore } from '@/stores/auth'

const app = createApp(App)

// فعّلي Pinia وخليه متاح قبل الراوتر
const pinia = createPinia()
app.use(pinia)

// ← هنا نسترجع المستخدم من localStorage قبل تفعيل الراوتر
const auth = useAuthStore()
auth.initAuth() // هذا السطر هو اللي كان ناقص

// بعدها فعّلي الراوتر وباقي الإضافات
app.use(router)

app.use(Toast, {
  position: 'top-center',
  timeout: 3000,
  closeOnClick: true,
  pauseOnFocusLoss: true,
  pauseOnHover: true,
  draggable: true,
  draggablePercent: 0.6,
  showCloseButtonOnHover: false,
  hideProgressBar: false,
  closeButton: 'button',
  icon: true,
  rtl: false,
  newestOnTop: true,
  maxToasts: 5
})

app.use(i18n)

app.mount('#app')
