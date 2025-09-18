// src/services/api.js
import axios from 'axios'

// إعداد Axios للربط مع Spring Boot
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10000
})

// ===== Request Interceptor =====
api.interceptors.request.use(
  (config) => {
    try {
      const raw = localStorage.getItem('user')
      if (raw) {
        const saved = JSON.parse(raw)
        if (saved && saved.token) {
          config.headers.Authorization = `Bearer ${saved.token}`
        }
      }
    } catch {
      // لو JSON خربان نشيله
      localStorage.removeItem('user')
    }
    return config
  },
  (error) => Promise.reject(error)
)

// ===== Response Interceptor =====
api.interceptors.response.use(
  (response) => {
    // نفكّ تغليف ApiResponse فقط إذا كان بالشكل المتوقع
    const payload = response?.data
    if (
      payload &&
      typeof payload === 'object' &&
      Object.prototype.hasOwnProperty.call(payload, 'data') &&
      Object.prototype.hasOwnProperty.call(payload, 'success')
    ) {
      return {
        ...response,
        data: payload.data, // ← الداتا الحقيقية
        message: payload.message,
        success: payload.success
      }
    }
    // لو الـ API رجع شكل مختلف (String, Array, Object بدون الغلاف) نمرّره كما هو
    return response
  },
  (error) => {
    const status = error?.response?.status
    const serverMsg =
      error?.response?.data?.message ||
      error?.response?.data?.error ||
      error?.message ||
      'حدث خطأ غير متوقع'

    if (status === 401) {
      try { localStorage.removeItem('user') } catch {}
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }

    return Promise.reject(new Error(serverMsg))
  }
)

// ======= Services =======

// المصادقة
export const authService = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  getProfile: (userId) => api.get('/auth/profile', { params: { userId } }),
  updateProfile: (userId, userData) => api.put('/auth/profile', userData, { params: { userId } })
}

// الكورسات
export const courseService = {
  getAllCourses: () => api.get('/courses'),
  getCourseById: (id) => api.get(`/courses/${id}`),
  createCourse: (courseData) => api.post('/courses', courseData),
  updateCourse: (id, courseData) => api.put(`/courses/${id}`, courseData),
  deleteCourse: (id) => api.delete(`/courses/${id}`),
  getCourseOutline: (id) => api.get(`/courses/${id}/outline`),

  searchCourses: (params = {}) => {
    const queryParams = new URLSearchParams()
    if (params.keyword) queryParams.append('keyword', params.keyword)
    if (params.location) queryParams.append('location', params.location)
    if (params.isFree !== undefined) queryParams.append('isFree', params.isFree)
    return api.get(`/courses/search?${queryParams.toString()}`)
  },

  getFreeCourses: () => api.get('/courses/free'),
  getPaidCourses: () => api.get('/courses/paid'),
  getCoursesByLocation: (location) => api.get('/courses/search', { params: { location } })
}
// إضافة في api.js
export const userService = {
  getUserStats: (userId) => api.get(`/users/${userId}/stats`),
  getActiveCourses: (userId) => api.get(`/users/${userId}/active-courses`),
  getUserDashboard: (userId) => api.get(`/users/${userId}/dashboard`)
}

// ===== Enrollments =====
export const enrollmentService = {
  // تسجيل في الكورس (Enroll)
  enroll: ({ userId, courseId }) => api.post('/enrollments', { userId, courseId }),
  // جلب حالة الوصول للكورس: { enrolled, paid, status, freeModules }
  getAccess: ({ userId, courseId }) =>
    api.get('/enrollments/access', { params: { userId, courseId } }),
}

// التقييمات
export const reviewService = {
  getCourseReviews: (courseId) => api.get(`/reviews/course/${courseId}`),
  getUserReviews: (userId) => api.get(`/reviews/user/${userId}`),
  createReview: (reviewData) => api.post('/reviews', reviewData),
  updateReview: (reviewId, reviewData) => api.put(`/reviews/${reviewId}`, reviewData),
  deleteReview: (reviewId, userId) => api.delete(`/reviews/${reviewId}`, { params: { userId } }),
  getCourseReviewStats: (courseId) => api.get(`/reviews/course/${courseId}/stats`),
  getRecentReviews: (limit = 10) => {
    const safe = Number(limit)
    return api.get('/reviews/recent', { params: { limit: Number.isFinite(safe) ? safe : 10 } })
  }
}

// المفضلة
export const favoriteService = {
  getUserFavorites: (userId) => api.get(`/favorites/user/${userId}`),
  addToFavorites: (userId, courseId) => api.post('/favorites', { userId, courseId }),
  removeFromFavorites: (userId, courseId) => api.delete(`/favorites/user/${userId}/course/${courseId}`),
  toggleFavorite: (userId, courseId) => api.post('/favorites/toggle', { userId, courseId }),
  checkIfFavorite: (userId, courseId) => api.get(`/favorites/user/${userId}/course/${courseId}/check`),
  getUserFavoritesCount: (userId) => api.get(`/favorites/user/${userId}/count`),
  getCourseFavoritesCount: (courseId) => api.get(`/favorites/course/${courseId}/count`),
  getMostFavoritedCourses: () => api.get('/favorites/popular')
}

// الإدارة (ADMIN)
export const adminService = {
  getDashboardStats: () => api.get('/admin/dashboard'),
  getAllCoursesAdmin: () => api.get('/admin/courses'),
  createCourseAdmin: (courseData) => api.post('/admin/courses', courseData),
  updateCourseAdmin: (id, courseData) => api.put(`/admin/courses/${id}`, courseData),
  deleteCourseAdmin: (id) => api.delete(`/admin/courses/${id}`),
  getAllUsersAdmin: () => api.get('/admin/users'),
  updateUserRole: (userId, role) => api.put(`/admin/users/${userId}/role`, { role }),
  deleteUserAdmin: (userId) => api.delete(`/admin/users/${userId}`),
  getCourseAnalytics: () => api.get('/admin/analytics/courses'),
  getUserAnalytics: () => api.get('/admin/analytics/users'),
  getEnrollmentsTrend: (range = '30d') =>
    api.get('/admin/stats/enrollments-trend', { params: { range: String(range || '30d') } }),
  getCourseDistribution: () => api.get('/admin/stats/course-distribution'),
  getTopCourses: (limit = 5) => {
    const n = Number(limit)
    return api.get('/admin/stats/top-courses', { params: { limit: Number.isFinite(n) ? n : 5 } })
  },
  getRecentActivity: (limit = 8) => {
    const n = Number(limit)
    return api.get('/admin/activity/recent', { params: { limit: Number.isFinite(n) ? n : 8 } })
  }
}

// الدفع — موحّد مع الباك (Query Params) ويرجع String URL
export const paymentService = {
  createPaymentSession: ({ userId, courseId }) =>
    api.post('/payments/create', null, { params: { userId, courseId } }),

  // مساعد: ينادي الإنشاء ويحوّل مباشرة
  async redirectToCheckout({ userId, courseId }) {
    const res = await this.createPaymentSession({ userId, courseId })
    const url = typeof res?.data === 'string' ? res.data : res
    if (typeof url === 'string' && url.length) {
      window.location.href = url
    } else {
      throw new Error('تعذر بدء عملية الدفع')
    }
  }
}

// تقدّم الدروس
export const progressService = {
  getCourseProgress: (userId, courseId) => 
    api.get(`/progress/course/${courseId}`, { params: { userId } }),
  updateLessonProgress: (userId, payload) =>
    api.post('/progress', payload, { params: { userId } })
}

export default api
