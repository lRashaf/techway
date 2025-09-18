import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Import views
import Home from '@/views/Home.vue'
import Courses from '@/views/Courses.vue'
import CourseDetails from '@/views/CourseDetails.vue'
import Profile from '@/views/Profile.vue'
import About from '@/views/About.vue'
import Contact from '@/views/Contact.vue'
import Login from '@/views/Login.vue'
import AdminDashboard from '@/views/AdminDashboard.vue'
import CoursesMap from '@/components/maps/CoursesMap.vue' // 👈 جديد
import CoursePlayer from '@/views/CoursePlayer.vue'


const routes = [
  // Splash (لا حمايات هنا)
  {
    path: '/splash',
    name: 'Splash',
    component: () => import('@/views/SplashPage.vue'),
    meta: { title: 'TechWay', skipAuthGuards: true, skipSplash: true }
  },

  // Public
  { path: '/', name: 'Home', component: Home, meta: { title: 'الرئيسية - TechWay' } },
  { path: '/courses', name: 'Courses', component: Courses, meta: { title: 'الكورسات - TechWay' } },
  { path: '/courses/:id', name: 'CourseDetails', component: CourseDetails, meta: { title: 'تفاصيل الكورس - TechWay' } },
  { path: '/about', name: 'About', component: About, meta: { title: 'من نحن - TechWay' } },
  { path: '/contact', name: 'Contact', component: Contact, meta: { title: 'اتصل بنا - TechWay' } },

  // Map (جديد)
  { path: '/map', name: 'CoursesMap', component: CoursesMap, meta: { title: 'الخريطة - TechWay' } },

  // Auth
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { title: 'الملف الشخصي - TechWay', requiresAuth: true }
  },
  { path: '/learn/:courseId/:lessonId?', name: 'CoursePlayer', component: CoursePlayer, meta: { title: 'مشغل الكورس - TechWay', requiresAuth: true } },

  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { title: 'لوحة تحكم المدير - TechWay', requiresAuth: true, requiresAdmin: true }
  },
  { path: '/login', name: 'Login', component: Login, meta: { title: 'تسجيل الدخول - TechWay', skipSplash: true } },

  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: 'الصفحة غير موجودة - TechWay', skipSplash: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  document.title = to.meta.title || 'TechWay'

  // === Splash فقط قبل الهوم ===
  const skipOnce = sessionStorage.getItem('skipNextSplash') === '1'
  if (to.name === 'Home' && !skipOnce) {
    sessionStorage.setItem('intendedPath', to.fullPath || '/')
    return next({ name: 'Splash' })
  }
  if (skipOnce) sessionStorage.removeItem('skipNextSplash')

  // === الحمايات الاعتيادية ===
  if (!to.meta?.skipAuthGuards) {
    if (to.meta.requiresAuth && !authStore.isAuthenticated) return next('/login')
    if (to.meta.requiresAdmin && !authStore.isAdmin) return next('/')
  }

  next()
})

export default router
