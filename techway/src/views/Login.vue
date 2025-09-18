<template>
  <Layout>
    <div class="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-900 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8">
        <!-- Header -->
        <div class="text-center">
          <div class="flex justify-center">
            <div class="w-16 h-16 bg-gradient-to-br from-purple-600 to-purple-700 rounded-xl flex items-center justify-center">
              <BookOpen class="w-8 h-8 text-white" />
            </div>
          </div>
          <h2 class="mt-6 text-3xl font-bold text-slate-900 dark:text-white">
            {{ isLogin ? $t('welcome_back') : 'إنشاء حساب جديد' }}
          </h2>
          <p class="mt-2 text-sm text-slate-600 dark:text-slate-400">
            {{ isLogin ? 'سجل دخولك للوصول لحسابك' : 'انضم إلى مجتمع TechWay' }}
          </p>
        </div>

        <!-- Admin Login Toggle -->
        <div v-if="isLogin" class="flex justify-center">
          <div class="bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg p-1 flex">
            <button
              @click="isAdminLogin = false"
              :class="[
                'px-4 py-2 text-sm font-medium rounded-md transition-all',
                !isAdminLogin
                  ? 'bg-purple-600 text-white shadow-sm'
                  : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
              ]"
            >
              <Users class="w-4 h-4 inline mr-2" />
              تسجيل عادي
            </button>
            <button
              @click="isAdminLogin = true"
              :class="[
                'px-4 py-2 text-sm font-medium rounded-md transition-all',
                isAdminLogin
                  ? 'bg-purple-600 text-white shadow-sm'
                  : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'
              ]"
            >
              <Shield class="w-4 h-4 inline mr-2" />
              تسجيل مدير
            </button>
          </div>
        </div>

        <!-- Form -->
        <div class="card p-8">
          <!-- Admin Notice -->
          <div v-if="isLogin && isAdminLogin" class="mb-6 p-4 bg-amber-50 dark:bg-amber-900/30 border border-amber-200 dark:border-amber-800 rounded-lg">
            <div class="flex items-center space-x-2 rtl:space-x-reverse">
              <Shield class="w-5 h-5 text-amber-600 dark:text-amber-400" />
              <p class="text-sm text-amber-700 dark:text-amber-300">
                تسجيل دخول كمدير - تحتاج صلاحيات خاصة
              </p>
            </div>
          </div>

          <form @submit.prevent="handleSubmit" class="space-y-6" novalidate>
            <!-- Name Field (للتسجيل فقط) -->
            <div v-if="!isLogin">
              <label for="name" class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                {{ $t('name') }} *
              </label>
              <input
                id="name"
                v-model="form.name"
                @blur="validateField('name')"
                type="text"
                :class="[
                  'input-field',
                  touched.name && errors.name ? 'border-red-500 focus:ring-red-500' : '',
                  touched.name && !errors.name ? 'border-green-500' : ''
                ]"
                :placeholder="$t('name')"
                required
                autocomplete="name"
              />
              <p v-if="touched.name && errors.name" class="mt-1 text-sm text-red-600 dark:text-red-400">
                {{ errors.name }}
              </p>
            </div>

            <!-- Email -->
            <div>
              <label for="email" class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                {{ $t('email') }} *
              </label>
              <input
                id="email"
                v-model="form.email"
                @blur="validateField('email')"
                type="email"
                :class="[
                  'input-field',
                  touched.email && errors.email ? 'border-red-500 focus:ring-red-500' : '',
                  touched.email && !errors.email ? 'border-green-500' : ''
                ]"
                :placeholder="isAdminLogin ? 'admin@techway.com' : $t('email')"
                required
                autocomplete="email"
              />
              <p v-if="touched.email && errors.email" class="mt-1 text-sm text-red-600 dark:text-red-400">
                {{ errors.email }}
              </p>
            </div>

            <!-- Password -->
            <div>
              <label for="password" class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                {{ $t('password') }} *
              </label>
              <div class="relative">
                <input
                  id="password"
                  v-model="form.password"
                  @blur="validateField('password')"
                  :type="showPassword ? 'text' : 'password'"
                  :class="[
                    'input-field pr-10',
                    touched.password && errors.password ? 'border-red-500 focus:ring-red-500' : '',
                    touched.password && !errors.password ? 'border-green-500' : ''
                  ]"
                  :placeholder="$t('password')"
                  required
                  autocomplete="current-password"
                />
                <button
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute inset-y-0 right-0 pr-3 flex items-center"
                  tabindex="-1"
                >
                  <EyeOff v-if="showPassword" class="w-5 h-5 text-slate-400" />
                  <Eye v-else class="w-5 h-5 text-slate-400" />
                </button>
              </div>
              <p v-if="touched.password && errors.password" class="mt-1 text-sm text-red-600 dark:text-red-400">
                {{ errors.password }}
              </p>
            </div>

            <!-- Demo Credentials (للمدير) -->
            <div v-if="isLogin && isAdminLogin" class="bg-blue-50 dark:bg-blue-900/30 border border-blue-200 dark:border-blue-800 rounded-lg p-4">
              <h4 class="text-sm font-medium text-blue-800 dark:text-blue-300 mb-2">
                بيانات تجريبية للمدير:
              </h4>
              <div class="space-y-1 text-xs">
                <p class="text-blue-700 dark:text-blue-400">
                  <strong>البريد:</strong> admin@techway.com
                </p>
                <p class="text-blue-700 dark:text-blue-400">
                  <strong>كلمة المرور:</strong> admin123
                </p>
              </div>
              <button
                type="button"
                @click="fillAdminCredentials"
                class="mt-2 text-xs text-blue-600 dark:text-blue-400 hover:underline"
              >
                ملء البيانات تلقائياً
              </button>
            </div>

            <!-- Submit Button -->
            <button
              type="submit"
              :disabled="authStore.loading || !formIsValid"
              :class="[
                'w-full px-4 py-3 rounded-lg font-medium transition-colors flex items-center justify-center space-x-2 rtl:space-x-reverse',
                isAdminLogin
                  ? 'bg-red-600 hover:bg-red-700 disabled:bg-red-400'
                  : 'bg-purple-600 hover:bg-purple-700 disabled:bg-purple-400',
                'text-white'
              ]"
            >
              <div v-if="authStore.loading" class="loading-spinner w-5 h-5"></div>
              <Shield v-else-if="isLogin && isAdminLogin" class="w-5 h-5" />
              <span>
                {{ isLogin ? (isAdminLogin ? 'دخول كمدير' : $t('login')) : $t('register') }}
              </span>
            </button>
          </form>

          <!-- Toggle Form Type -->
          <div class="mt-6 text-center">
            <p class="text-sm text-slate-600 dark:text-slate-400">
              {{ isLogin ? 'ليس لديك حساب؟' : 'لديك حساب بالفعل؟' }}
              <button
                @click="toggleFormType"
                class="font-medium text-purple-600 hover:text-purple-500 mr-1"
              >
                {{ isLogin ? 'إنشاء حساب' : 'تسجيل الدخول' }}
              </button>
            </p>
          </div>
        </div>

        <!-- Footer -->
        <p class="text-center text-xs text-slate-500 dark:text-slate-400">
          بالمتابعة، أنت توافق على
          <a href="#" class="underline">شروط الخدمة</a> و
          <a href="#" class="underline">سياسة الخصوصية</a>
        </p>
      </div>
    </div>
  </Layout>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { BookOpen, Eye, EyeOff, Shield, Users } from 'lucide-vue-next'
import Layout from '@/components/Layout.vue'

export default {
  name: 'Login',
  components: {
    Layout,
    BookOpen,
    Eye,
    EyeOff,
    Shield,
    Users
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()

    const isLogin = ref(true)
    const isAdminLogin = ref(false)
    const showPassword = ref(false)

    const form = reactive({
      name: '',
      email: '',
      password: ''
    })

    const errors = reactive({
      name: '',
      email: '',
      password: ''
    })

    const touched = reactive({
      name: false,
      email: false,
      password: false
    })

    // التحقق من صحة الحقول
    const usernameValid = computed(() => form.name.trim().length >= 3)
    const emailValid = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
    const passwordValid = computed(() => form.password.length >= 6)

    const formIsValid = computed(() => {
      if (isLogin.value) {
        return emailValid.value && passwordValid.value
      } else {
        return usernameValid.value && emailValid.value && passwordValid.value
      }
    })

    const validateField = (field) => {
      touched[field] = true
      switch (field) {
        case 'name':
          errors.name = usernameValid.value ? '' : 'الاسم يجب أن يكون 3 أحرف على الأقل'
          break
        case 'email':
          errors.email = emailValid.value ? '' : 'صيغة البريد الإلكتروني غير صحيحة'
          break
        case 'password':
          errors.password = passwordValid.value ? '' : 'كلمة المرور يجب أن تكون 6 أحرف على الأقل'
          break
      }
    }

    const fillAdminCredentials = () => {
      form.email = 'admin@techway.com'
      form.password = 'admin123'
    }

    const handleSubmit = async () => {
      // اجبر اظهار الاخطاء لكل الحقول
      Object.keys(touched).forEach(key => {
        touched[key] = true
        validateField(key)
      })

      if (!formIsValid.value) return

      let success = false

      if (isLogin.value) {
        // تسجيل الدخول
        success = await authStore.login({
          email: form.email,
          password: form.password
        })

        // توجيه المدير للوحة التحكم
        if (success && authStore.isAdmin) {
          router.push('/admin')
          return
        }
      } else {
        // تسجيل جديد
        success = await authStore.register({
          name: form.name,
          email: form.email,
          password: form.password
        })
      }

      if (success) {
        const returnTo = route.query.returnTo || '/'
        router.push(returnTo)
      }
    }

    const toggleFormType = () => {
      isLogin.value = !isLogin.value
      isAdminLogin.value = false // إعادة تعيين وضع المدير
      // مسح النموذج والأخطاء واللمس
      Object.keys(form).forEach(key => (form[key] = ''))
      Object.keys(errors).forEach(key => (errors[key] = ''))
      Object.keys(touched).forEach(key => (touched[key] = false))
    }

    onMounted(() => {
      if (authStore.isAuthenticated) {
        const returnTo = route.query.returnTo || '/'
        router.push(returnTo)
      }
    })

    return {
      authStore,
      isLogin,
      isAdminLogin,
      showPassword,
      form,
      errors,
      touched,
      formIsValid,
      handleSubmit,
      toggleFormType,
      validateField,
      fillAdminCredentials
    }
  }
}
</script>

<style scoped>
.input-field {
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  padding: 0.5rem 0.75rem;
  width: 100%;
  font-size: 1rem;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.input-field:focus {
  outline: none;
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.4);
}

.dark .input-field {
  background-color: #1e293b;
  border-color: #475569;
  color: white;
}

.border-red-500 {
  border-color: #ef4444 !important;
}

.border-green-500 {
  border-color: #22c55e !important;
}

.loading-spinner {
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>