<template>
  <Layout>
    <div class="py-8">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Check if user is authenticated -->
        <div v-if="!authStore.isAuthenticated" class="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-900">
          <div class="text-center space-y-4">
            <User class="w-16 h-16 text-slate-400 mx-auto" />
            <h2 class="text-2xl font-bold text-slate-900 dark:text-black">
              يجب تسجيل الدخول
            </h2>
            <p class="text-slate-600 dark:text-slate-400">
              سجل دخولك لعرض ملفك الشخصي
            </p>
            <router-link
              to="/login"
              class="inline-block bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded-lg font-medium transition-colors"
            >
              تسجيل الدخول
            </router-link>
          </div>
        </div>

        <!-- Profile Content -->
        <div v-else>
          <!-- Profile Header -->
          <div class="card p-6 mb-8">
            <div class="flex flex-col sm:flex-row items-center space-y-4 sm:space-y-0 sm:space-x-6 rtl:space-x-reverse">
              <div class="w-24 h-24 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center text-white text-2xl font-bold">
                {{ (authStore.user?.name || authStore.user?.email || 'U')[0].toUpperCase() }}
              </div>
              <div class="text-center sm:text-right rtl:text-left flex-1">
                <h1 class="text-2xl font-bold text-slate-900 dark:text-white mb-1">
                  {{ authStore.user?.name || 'مستخدم' }}
                </h1>
                <p class="text-slate-600 dark:text-slate-400 mb-2">
                  {{ authStore.user?.email }}
                </p>
                <div class="flex items-center justify-center sm:justify-start rtl:justify-end space-x-4 rtl:space-x-reverse text-sm text-slate-500 dark:text-slate-400">
                  <div class="flex items-center space-x-1 rtl:space-x-reverse">
                    <Calendar class="w-4 h-4" />
                    <span>انضم في {{ formatDate(authStore.user?.createdAt) }}</span>
                  </div>
                  <div class="flex items-center space-x-1 rtl:space-x-reverse">
                    <Heart class="w-4 h-4" />
                    <span>{{ favorites.length }} كورس محفوظ</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Tabs -->
          <div class="mb-8">
            <div class="border-b border-slate-200 dark:border-slate-700">
              <nav class="flex space-x-8 rtl:space-x-reverse">
                <button
                  v-for="tab in tabs"
                  :key="tab.id"
                  @click="activeTab = tab.id"
                  :class="[
                    'flex items-center space-x-2 rtl:space-x-reverse py-4 px-1 border-b-2 font-medium text-sm transition-colors',
                    activeTab === tab.id
                      ? 'border-purple-500 text-purple-600 dark:text-purple-400'
                      : 'border-transparent text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-300'
                  ]"
                >
                  <component :is="tab.icon" class="w-4 h-4" />
                  <span>{{ tab.label }}</span>
                </button>
              </nav>
            </div>
          </div>

          <!-- Tab Content -->
          <div class="space-y-8">
            <!-- Overview Tab -->
            <div v-if="activeTab === 'overview'" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              <!-- Stats Cards -->
              <div class="card p-6">
                <div class="flex items-center space-x-3 rtl:space-x-reverse">
                  <div class="w-12 h-12 bg-blue-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                    <Heart class="w-6 h-6 text-purple-600 dark:text-purple-400" />
                  </div>
                  <div>
                    <p class="text-2xl font-bold text-slate-900 dark:text-white">
                      {{ favorites.length }}
                    </p>
                    <p class="text-sm text-slate-600 dark:text-slate-400">
                      كورس محفوظ
                    </p>
                  </div>
                </div>
              </div>

              <div class="card p-6">
                <div class="flex items-center space-x-3 rtl:space-x-reverse">
                  <div class="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                    <BookOpen class="w-6 h-6 text-green-600 dark:text-green-400" />
                  </div>
                  <div>
                    <p class="text-2xl font-bold text-slate-900 dark:text-white">0</p>
                    <p class="text-sm text-slate-600 dark:text-slate-400">
                      كورس مكتمل
                    </p>
                  </div>
                </div>
              </div>

              <div class="card p-6">
                <div class="flex items-center space-x-3 rtl:space-x-reverse">
                  <div class="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                    <User class="w-6 h-6 text-purple-600 dark:text-purple-400" />
                  </div>
                  <div>
                    <p class="text-2xl font-bold text-slate-900 dark:text-white">
                      {{ daysSinceJoined }}
                    </p>
                    <p class="text-sm text-slate-600 dark:text-slate-400">
                      يوم في المنصة
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Favorites Tab -->
            <div v-else-if="activeTab === 'favorites'">
              <h2 class="text-2xl font-bold text-slate-900 dark:text-white mb-6">
                الكورسات المحفوظة ({{ favorites.length }})
              </h2>
              
              <div v-if="favoritesLoading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div v-for="i in 3" :key="i" class="card p-6 animate-pulse">
                  <div class="bg-slate-200 dark:bg-slate-700 h-48 rounded-lg mb-4"></div>
                  <div class="space-y-3">
                    <div class="bg-slate-200 dark:bg-slate-700 h-4 rounded w-3/4"></div>
                    <div class="bg-slate-200 dark:bg-slate-700 h-4 rounded w-1/2"></div>
                  </div>
                </div>
              </div>
              
              <div v-else-if="favorites.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <CourseCard
                  v-for="course in favorites"
                  :key="course.id"
                  :course="course"
                />
              </div>
              
              <div v-else class="text-center py-16">
                <Heart class="w-16 h-16 text-slate-300 dark:text-slate-600 mx-auto mb-4" />
                <h3 class="text-xl font-semibold text-slate-900 dark:text-white mb-2">
                  لا توجد كورسات محفوظة
                </h3>
                <p class="text-slate-600 dark:text-slate-400">
                  ابدأ بحفظ الكورسات التي تهتم بها
                </p>
              </div>
            </div>

            <!-- Settings Tab -->
            <div v-else-if="activeTab === 'settings'" class="space-y-6">
              <h2 class="text-2xl font-bold text-slate-900 dark:text-white">
                إعدادات الحساب
              </h2>
              
              <div class="card p-6">
                <h3 class="text-lg font-semibold text-slate-900 dark:text-white mb-4">
                  معلومات الحساب
                </h3>
                
                <form @submit.prevent="handleUpdateProfile" class="space-y-4">
                  <div>
                    <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                      الاسم
                    </label>
                    <input
                      v-model="profileForm.name"
                      type="text"
                      class="input-field"
                      placeholder="اسمك الكامل"
                    />
                  </div>
                  
                  <div>
                    <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                      البريد الإلكتروني
                    </label>
                    <input
                      v-model="profileForm.email"
                      type="email"
                      class="input-field"
                      placeholder="your@email.com"
                    />
                  </div>
                  
                  <div>
                    <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                      كلمة المرور الجديدة (اختيارية)
                    </label>
                    <input
                      v-model="profileForm.password"
                      type="password"
                      class="input-field"
                      placeholder="اتركها فارغة إذا لم ترد تغييرها"
                    />
                  </div>
                  
                  <button
                    type="submit"
                    :disabled="authStore.loading"
                    class="btn-primary flex items-center space-x-2 rtl:space-x-reverse"
                  >
                    <div v-if="authStore.loading" class="loading-spinner w-4 h-4"></div>
                    <span>حفظ التغييرات</span>
                  </button>
                </form>
              </div>

              <div class="card p-6">
                <h3 class="text-lg font-semibold text-slate-900 dark:text-white mb-4">
                  تفضيلات المنصة
                </h3>
                
                <div class="space-y-4">
                  <div class="flex items-center justify-between">
                    <div>
                      <p class="font-medium text-slate-900 dark:text-white">
                        اللغة المفضلة
                      </p>
                      <p class="text-sm text-slate-600 dark:text-slate-400">
                        {{ $i18n.locale === 'ar' ? 'العربية' : 'English' }}
                      </p>
                    </div>
                    <button
                      @click="toggleLanguage"
                      class="text-purple-600 dark:text-purple-400 hover:underline text-sm"
                    >
                      تغيير
                    </button>
                  </div>
                  
                  <div class="flex items-center justify-between">
                    <div>
                      <p class="font-medium text-slate-900 dark:text-white">
                        المظهر
                      </p>
                      <p class="text-sm text-slate-600 dark:text-slate-400">
                        {{ themeStore.isDark ? 'الوضع الليلي' : 'الوضع النهاري' }}
                      </p>
                    </div>
                    <button
                      @click="themeStore.toggleTheme()"
                      class="text-purple-600 dark:text-purple-400 hover:underline text-sm"
                    >
                      تغيير
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import { useToast } from 'vue-toastification'
import { User, Heart, BookOpen, Settings, Calendar } from 'lucide-vue-next'
import Layout from '@/components/Layout.vue'
import CourseCard from '@/components/CourseCard.vue'

export default {
  name: 'Profile',
  components: {
    Layout,
    CourseCard,
    User,
    Heart,
    BookOpen,
    Settings,
    Calendar
  },
  setup() {
    const { locale } = useI18n()
    const authStore = useAuthStore()
    const themeStore = useThemeStore()
    const toast = useToast()
    
    const activeTab = ref('overview')
    const favorites = ref([])
    const favoritesLoading = ref(false)
    
    const profileForm = reactive({
      name: '',
      email: '',
      password: ''
    })
    
    const tabs = [
      { id: 'overview', label: 'نظرة عامة', icon: User },
      { id: 'favorites', label: 'المفضلة', icon: Heart },
      { id: 'settings', label: 'الإعدادات', icon: Settings }
    ]
    
    const daysSinceJoined = computed(() => {
      if (!authStore.user?.createdAt) return 0
      return Math.floor((new Date().getTime() - new Date(authStore.user.createdAt).getTime()) / (1000 * 60 * 60 * 24))
    })
    
    const formatDate = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleDateString('ar-SA', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }
    
    const fetchFavorites = async () => {
      if (!authStore.user) return
      
      favoritesLoading.value = true
      try {
        // محاكاة جلب المفضلة - يمكن ربطها بـ API لاحقاً
        await new Promise(resolve => setTimeout(resolve, 1000))
        favorites.value = []
      } catch (error) {
        console.error('Error fetching favorites:', error)
      } finally {
        favoritesLoading.value = false
      }
    }
    
    const handleUpdateProfile = async () => {
      if (!profileForm.name.trim() || !profileForm.email.trim()) {
        toast.error('الاسم والبريد الإلكتروني مطلوبان')
        return
      }
      
      const success = await authStore.updateProfile({
        name: profileForm.name,
        email: profileForm.email,
        password: profileForm.password || undefined
      })
      
      if (success) {
        profileForm.password = '' // مسح كلمة المرور بعد التحديث
      }
    }
    
    const toggleLanguage = () => {
      const newLang = locale.value === 'ar' ? 'en' : 'ar'
      locale.value = newLang
      localStorage.setItem('language', newLang)
    }
    
    onMounted(() => {
      if (authStore.user) {
        profileForm.name = authStore.user.name || ''
        profileForm.email = authStore.user.email || ''
        fetchFavorites()
      }
    })
    
    return {
      authStore,
      themeStore,
      activeTab,
      favorites,
      favoritesLoading,
      profileForm,
      tabs,
      daysSinceJoined,
      formatDate,
      handleUpdateProfile,
      toggleLanguage
    }
  }
}
</script>