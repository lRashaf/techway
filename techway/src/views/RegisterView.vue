<template>
  <Layout>
    <div class="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-900 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8">
        <!-- Logo -->
        <div class="text-center">
          <img src="/logo.png" alt="Logo" class="w-24 h-auto mx-auto" />
          <h2 class="mt-6 text-3xl font-bold text-slate-900 dark:text-white">إنشاء حساب جديد</h2>
          <p class="mt-2 text-sm text-slate-600 dark:text-slate-400">انضم إلى مجتمع TechWay</p>
        </div>

        <!-- Form -->
        <div class="card p-8">
          <form @submit.prevent="handleSubmit" class="space-y-6">

            <!-- Full Name -->
            <div>
              <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">الاسم *</label>
              <input
                v-model="form.name"
                @blur="nameTouched = true"
                type="text"
                placeholder="اسمك الكامل"
                required
                :class="[
                  'input-field',
                  nameTouched && !nameValid ? 'border-red-500 focus:ring-red-500' : '',
                  nameTouched && nameValid ? 'border-green-500 focus:ring-green-500' : ''
                ]"
              />
              <p v-if="nameTouched && !nameValid" class="mt-1 text-sm text-red-600">الاسم يجب أن يكون 3 أحرف على الأقل</p>
            </div>

            <!-- Email -->
            <div>
              <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">البريد الإلكتروني *</label>
              <input
                v-model="form.email"
                @blur="emailTouched = true"
                type="email"
                placeholder="example@email.com"
                required
                :class="[
                  'input-field',
                  emailTouched && !emailValid ? 'border-red-500 focus:ring-red-500' : '',
                  emailTouched && emailValid ? 'border-green-500 focus:ring-green-500' : ''
                ]"
              />
              <p v-if="emailTouched && !emailValid" class="mt-1 text-sm text-red-600">صيغة البريد الإلكتروني غير صحيحة</p>
            </div>

            <!-- Password -->
            <div>
              <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">كلمة المرور *</label>
              <div class="relative">
                <input
                  v-model="form.password"
                  @blur="passwordTouched = true"
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="••••••••"
                  required
                  :class="[
                    'input-field pr-10',
                    passwordTouched && !passwordValid ? 'border-red-500 focus:ring-red-500' : '',
                    passwordTouched && passwordValid ? 'border-green-500 focus:ring-green-500' : ''
                  ]"
                />
                <button type="button" @click="showPassword = !showPassword" class="absolute inset-y-0 right-0 pr-3 flex items-center">
                  <EyeOff v-if="showPassword" class="w-5 h-5 text-slate-400" />
                  <Eye v-else class="w-5 h-5 text-slate-400" />
                </button>
              </div>
              <p v-if="passwordTouched && !passwordValid" class="mt-1 text-sm text-red-600">كلمة المرور يجب أن تكون 6 أحرف على الأقل</p>
            </div>

            <!-- Submit -->
            <button
              type="submit"
              :disabled="!formIsValid || authStore.loading"
              class="w-full bg-purple-600 hover:bg-purple-700 disabled:bg-purple-300 text-white px-4 py-3 rounded-lg font-medium transition-colors"
            >
              <span>تسجيل</span>
            </button>
          </form>

          <!-- Link to login -->
          <div class="mt-6 text-center">
            <p class="text-sm text-slate-600 dark:text-slate-400">
              لديك حساب بالفعل؟
              <RouterLink to="/login" class="font-medium text-purple-600 hover:text-blue-500 mr-1">تسجيل الدخول</RouterLink>
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
import { Eye, EyeOff } from 'lucide-vue-next'
import Layout from '@/components/Layout.vue'

export default {
  components: { Layout, Eye, EyeOff },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()

    const showPassword = ref(false)

    const form = reactive({
      name: '',
      email: '',
      password: ''
    })

    const nameTouched = ref(false)
    const emailTouched = ref(false)
    const passwordTouched = ref(false)

    const nameValid = computed(() => form.name.trim().length >= 3)
    const emailValid = computed(() => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
    const passwordValid = computed(() => form.password.length >= 6)

    const formIsValid = computed(() =>
      nameValid.value && emailValid.value && passwordValid.value
    )

    const handleSubmit = async () => {
      nameTouched.value = true
      emailTouched.value = true
      passwordTouched.value = true

      if (!formIsValid.value) return

      const success = await authStore.register({
        name: form.name,
        email: form.email,
        password: form.password
      })

      if (success) {
        const returnTo = route.query.returnTo || '/'
        router.push(returnTo)
      }
    }

    onMounted(() => {
      if (authStore.isAuthenticated) {
        const returnTo = route.query.returnTo || '/'
        router.push(returnTo)
      }
    })

    return {
      form,
      showPassword,
      nameTouched,
      emailTouched,
      passwordTouched,
      nameValid,
      emailValid,
      passwordValid,
      formIsValid,
      handleSubmit,
      authStore
    }
  }
}
</script>
