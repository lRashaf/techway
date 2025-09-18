import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authService } from '@/services/api'
import { useToast } from 'vue-toastification'

export const useAuthStore = defineStore('auth', () => {
  const toast = useToast()

  // State
  const user = ref(null)
  const loading = ref(false)

  // Getters
  const isAuthenticated = computed(() => !!user.value)
  const isAdmin = computed(() => {
    const role = user.value?.role
    return role === 'ADMIN' || role === 'ROLE_ADMIN'
  })

  // Helpers
  const normalizeAuthPayload = (payload) => {
    // يدعم كل هالأشكال بدون ما يطيح:
    // 1) { user: {...}, token: '...' }
    // 2) { id, name, email, role, token }
    // 3) { user: {...}, jwt: '...' }
    // 4) { data: { user, token } } ← (لو انتي لغّيتي فك التغليف)
    let u = null
    let t = null

    // لو جايه داخل data (حالات نادرة لو الInterceptor مو مفعل)
    const base = payload?.user || payload?.data?.user ? (payload.data || payload) : payload

    if (base?.user) {
      u = base.user
      t = base.token || base.jwt || null
    } else {
      // مفكوك على المستوى الأعلى
      const { id, name, email, role, token, jwt, ...rest } = base || {}
      if (id || name || email || role) {
        u = { id, name, email, role, ...rest }
        t = token || jwt || null
      }
    }

    // fallback: لو ما وصل role، اعتبره USER
    if (u && !u.role && base?.role) u.role = base.role
    if (u && !u.role) u.role = 'USER'

    return { user: u, token: t }
  }

  // Actions
  const initAuth = () => {
    const savedUser = localStorage.getItem('user')
    if (savedUser) {
      try {
        const parsed = JSON.parse(savedUser)
        user.value = parsed && typeof parsed === 'object' ? parsed : null
      } catch {
        localStorage.removeItem('user')
        user.value = null
      }
    }
  }

  const login = async (credentials) => {
    loading.value = true
    try {
      // ملاحظة: تأكدي إن الفورم يرسل نفس مفاتيح الباك (مثلاً: email/password أو username/password)
      const response = await authService.login(credentials)
      // بعد الInterceptor, response.data هو payload الحقيقي
      const { user: u, token: t } = normalizeAuthPayload(response.data)

      if (!u) throw new Error('استجابة غير متوقعة من الخادم')

      // خزّن
      user.value = u
      localStorage.setItem('user', JSON.stringify({ ...u, token: t || '' }))

      toast.success('تم تسجيل الدخول بنجاح')

      // لو أدمن، ودّيه للوحة التحكم
      if (u.role === 'ADMIN' || u.role === 'ROLE_ADMIN') {
        window.location.href = '/admin'
      } else {
        // غير أدمن، رجّعيه للرئيسية (عدليها لو تبين مسار معيّن)
        window.location.href = '/'
      }
      return true
    } catch (error) {
      toast.error(error.message || 'خطأ في تسجيل الدخول')
      return false
    } finally {
      loading.value = false
    }
  }

  const register = async (userData) => {
    loading.value = true
    try {
      const response = await authService.register(userData)
      const { user: u, token: t } = normalizeAuthPayload(response.data)

      if (!u) throw new Error('استجابة غير متوقعة من الخادم')

      user.value = u
      localStorage.setItem('user', JSON.stringify({ ...u, token: t || '' }))

      toast.success('تم إنشاء الحساب بنجاح')
      return true
    } catch (error) {
      toast.error(error.message || 'خطأ في إنشاء الحساب')
      return false
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    user.value = null
    localStorage.removeItem('user')
    toast.success('تم تسجيل الخروج')
    // اختياري: إعادة توجيه
    window.location.href = '/login'
  }

  const updateProfile = async (updatedData) => {
    if (!user.value) return false

    loading.value = true
    try {
      const response = await authService.updateProfile(user.value.id, updatedData)
      const updatedUser = response.data

      user.value = { ...user.value, ...updatedUser }
      const savedUser = JSON.parse(localStorage.getItem('user') || '{}')
      localStorage.setItem('user', JSON.stringify({ ...savedUser, ...updatedUser }))

      toast.success('تم تحديث البيانات بنجاح')
      return true
    } catch (error) {
      toast.error(error.message || 'خطأ في تحديث البيانات')
      return false
    } finally {
      loading.value = false
    }
  }

  // تهيئة المتجر عند إنشاؤه
  initAuth()

  return {
    // State
    user,
    loading,

    // Getters
    isAuthenticated,
    isAdmin,

    // Actions
    login,
    register,
    logout,
    updateProfile,
    initAuth
  }
})
