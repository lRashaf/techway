<template>
  <header class="bg-white dark:bg-slate-800 shadow-sm border-b border-slate-200 dark:border-slate-700 sticky top-0 z-50 transition-colors">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        
       <router-link to="/" class="flex items-center space-x-2 rtl:space-x-reverse">
  <img src="/logo.png" alt="Logo" class="w-16 h-auto" />
</router-link>

        <!-- Desktop Navigation -->
        <nav class="hidden md:flex items-center space-x-6 rtl:space-x-reverse">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            :class="[
              'px-3 py-2 rounded-md text-sm font-medium transition-colors',
              $route.path === item.path
                ? 'text-purple-600 bg-blue-50 dark:text-purple-400 dark:bg-blue-900/20'
                : 'text-slate-700 hover:text-purple-600 dark:text-slate-300 dark:hover:text-purple-400'
            ]"
          >
            {{ item.label }}
          </router-link>
        </nav>

        <!-- Desktop Actions -->
        <div class="hidden md:flex items-center space-x-4 rtl:space-x-reverse">
          <!-- Theme Toggle -->
          <button
            @click="themeStore.toggleTheme()"
            class="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
            :title="themeStore.isDark ? $t('light_mode') : $t('dark_mode')"
          >
            <Sun v-if="themeStore.isDark" class="w-5 h-5 text-slate-600 dark:text-slate-400" />
            <Moon v-else class="w-5 h-5 text-slate-600 dark:text-slate-400" />
          </button>

          <!-- Language Toggle -->
          <button
            @click="toggleLanguage"
            class="p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors flex items-center space-x-1 rtl:space-x-reverse"
            :title="$i18n.locale === 'ar' ? 'English' : 'العربية'"
          >
            <Globe class="w-5 h-5 text-slate-600 dark:text-slate-400" />
            <span class="text-sm text-slate-600 dark:text-slate-400">
              {{ $i18n.locale === 'ar' ? 'EN' : 'ع' }}
            </span>
          </button>

          <!-- User Menu -->
          <div v-if="authStore.isAuthenticated" class="relative">
            <button
              @click="isUserMenuOpen = !isUserMenuOpen"
              class="flex items-center space-x-2 rtl:space-x-reverse p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
            >
              <div :class="[
                'w-8 h-8 rounded-full flex items-center justify-center text-white font-semibold',
                authStore.isAdmin 
                  ? 'bg-gradient-to-br from-red-500 to-red-600' 
                  : 'bg-gradient-to-br from-blue-500 to-blue-600'
              ]">
                {{ (authStore.user?.name || authStore.user?.email || 'U')[0].toUpperCase() }}
              </div>
              <ChevronDown class="w-4 h-4 text-slate-600 dark:text-slate-400" />
            </button>

            <!-- User Dropdown -->
            <div
              v-if="isUserMenuOpen"
              class="absolute top-full mt-2 w-48 bg-white dark:bg-slate-800 rounded-lg shadow-lg border border-slate-200 dark:border-slate-700 py-1 right-0 rtl:right-auto rtl:left-0"
            >
              <div class="px-4 py-2 border-b border-slate-200 dark:border-slate-700">
                <p class="text-sm font-medium text-slate-900 dark:text-white">
                  {{ authStore.user?.name || authStore.user?.email }}
                </p>
                <p class="text-xs text-slate-500 dark:text-slate-400">
                  {{ authStore.user?.email }}
                </p>
                <p v-if="authStore.isAdmin" class="text-xs text-red-600 dark:text-red-400 font-medium">
                  مدير المنصة
                </p>
              </div>
              
              <router-link
                to="/profile"
                class="flex items-center space-x-2 rtl:space-x-reverse px-4 py-2 text-sm text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700"
                @click="isUserMenuOpen = false"
              >
                <User class="w-4 h-4" />
                <span>{{ $t('profile') }}</span>
              </router-link>

              <!-- Admin Dashboard Link -->
              <router-link
                v-if="authStore.isAdmin"
                to="/admin"
                class="flex items-center space-x-2 rtl:space-x-reverse px-4 py-2 text-sm text-red-700 dark:text-red-300 hover:bg-red-50 dark:hover:bg-red-900/20"
                @click="isUserMenuOpen = false"
              >
                <Shield class="w-4 h-4" />
                <span>لوحة التحكم</span>
              </router-link>

              <router-link
                to="/profile?tab=favorites"
                class="flex items-center space-x-2 rtl:space-x-reverse px-4 py-2 text-sm text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700"
                @click="isUserMenuOpen = false"
              >
                <Heart class="w-4 h-4" />
                <span>{{ $t('my_favorites') }}</span>
              </router-link>
              
              <button
                @click="handleLogout"
                class="flex items-center space-x-2 rtl:space-x-reverse w-full px-4 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-slate-100 dark:hover:bg-slate-700"
              >
                <LogOut class="w-4 h-4" />
                <span>{{ $t('logout') }}</span>
              </button>
            </div>
          </div>

          <!-- Login Button -->
          <router-link
            v-else
            to="/login"
            class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          >
            {{ $t('login') }}
          </router-link>
        </div>

        <!-- Mobile Menu Button -->
        <button
          @click="isMenuOpen = !isMenuOpen"
          class="md:hidden p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
        >
          <X v-if="isMenuOpen" class="w-6 h-6 text-slate-600 dark:text-slate-400" />
          <Menu v-else class="w-6 h-6 text-slate-600 dark:text-slate-400" />
        </button>
      </div>

      <!-- Mobile Menu -->
      <div v-if="isMenuOpen" class="md:hidden border-t border-slate-200 dark:border-slate-700 py-4">
        <nav class="space-y-2">
          <router-link
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            :class="[
              'block px-3 py-2 rounded-md text-base font-medium transition-colors',
              $route.path === item.path
                ? 'text-purple-600 bg-blue-50 dark:text-purple-400 dark:bg-blue-900/20'
                : 'text-slate-700 hover:text-purple-600 dark:text-slate-300 dark:hover:text-purple-400'
            ]"
            @click="isMenuOpen = false"
          >
            {{ item.label }}
          </router-link>
        </nav>

        <div class="border-t border-slate-200 dark:border-slate-700 mt-4 pt-4">
          <div class="flex items-center justify-between px-3">
            <button
              @click="themeStore.toggleTheme()"
              class="flex items-center space-x-2 rtl:space-x-reverse p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
            >
              <Sun v-if="themeStore.isDark" class="w-5 h-5 text-slate-600 dark:text-slate-400" />
              <Moon v-else class="w-5 h-5 text-slate-600 dark:text-slate-400" />
              <span class="text-sm text-slate-600 dark:text-slate-400">
                {{ themeStore.isDark ? $t('light_mode') : $t('dark_mode') }}
              </span>
            </button>

            <button
              @click="toggleLanguage"
              class="flex items-center space-x-2 rtl:space-x-reverse p-2 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-700 transition-colors"
            >
              <Globe class="w-5 h-5 text-slate-600 dark:text-slate-400" />
              <span class="text-sm text-slate-600 dark:text-slate-400">
                {{ $i18n.locale === 'ar' ? 'English' : 'العربية' }}
              </span>
            </button>
          </div>

          <!-- Mobile User Menu -->
          <div v-if="authStore.isAuthenticated" class="mt-4 px-3">
            <div class="flex items-center space-x-3 rtl:space-x-reverse mb-3">
              <div :class="[
                'w-10 h-10 rounded-full flex items-center justify-center text-white font-semibold',
                authStore.isAdmin 
                  ? 'bg-gradient-to-br from-red-500 to-red-600' 
                  : 'bg-gradient-to-br from-blue-500 to-blue-600'
              ]">
                {{ (authStore.user?.name || authStore.user?.email || 'U')[0].toUpperCase() }}
              </div>
              <div>
                <p class="text-sm font-medium text-slate-900 dark:text-white">
                  {{ authStore.user?.name || authStore.user?.email }}
                </p>
                <p class="text-xs text-slate-500 dark:text-slate-400">
                  {{ authStore.user?.email }}
                </p>
                <p v-if="authStore.isAdmin" class="text-xs text-red-600 dark:text-red-400 font-medium">
                  مدير المنصة
                </p>
              </div>
            </div>
            <div class="space-y-2">
              <router-link
                to="/profile"
                class="flex items-center space-x-2 rtl:space-x-reverse px-3 py-2 text-sm text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-lg"
                @click="isMenuOpen = false"
              >
                <User class="w-4 h-4" />
                <span>{{ $t('profile') }}</span>
              </router-link>

              <!-- Mobile Admin Dashboard Link -->
              <router-link
                v-if="authStore.isAdmin"
                to="/admin"
                class="flex items-center space-x-2 rtl:space-x-reverse px-3 py-2 text-sm text-red-700 dark:text-red-300 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg"
                @click="isMenuOpen = false"
              >
                <Shield class="w-4 h-4" />
                <span>لوحة التحكم</span>
              </router-link>

              <router-link
                to="/profile?tab=favorites"
                class="flex items-center space-x-2 rtl:space-x-reverse px-3 py-2 text-sm text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-lg"
                @click="isMenuOpen = false"
              >
                <Heart class="w-4 h-4" />
                <span>{{ $t('my_favorites') }}</span>
              </router-link>

              <button
                @click="handleLogout"
                class="flex items-center space-x-2 rtl:space-x-reverse w-full px-3 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-lg"
              >
                <LogOut class="w-4 h-4" />
                <span>{{ $t('logout') }}</span>
              </button>
            </div>
          </div>

          <!-- Mobile Login Button -->
          <div v-else class="mt-4 px-3">
            <router-link
              to="/login"
              class="w-full bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors block text-center"
              @click="isMenuOpen = false"
            >
              {{ $t('login') }}
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import { 
  Menu, 
  X, 
  Sun, 
  Moon, 
  Globe, 
  User, 
  LogOut,
  BookOpen,
  Heart,
  ChevronDown,
  Shield
} from 'lucide-vue-next'

export default {
  name: 'Header',
  components: {
    Menu, 
    X, 
    Sun, 
    Moon, 
    Globe, 
    User, 
    LogOut,
    BookOpen,
    Heart,
    ChevronDown,
    Shield
  },
  setup() {
    const { t, locale } = useI18n()
    const authStore = useAuthStore()
    const themeStore = useThemeStore()
    
    const isMenuOpen = ref(false)
    const isUserMenuOpen = ref(false)
    
    const navItems = computed(() => [
      { path: '/', label: t('home') },
      { path: '/courses', label: t('courses') },
        { path: '/map', label: 'الخريطة' }, // ← أضفنا الخريطة هنا
      { path: '/about', label: t('about') },
      { path: '/contact', label: t('contact') }
    ])
    
    const toggleLanguage = () => {
      const newLang = locale.value === 'ar' ? 'en' : 'ar'
      locale.value = newLang
      localStorage.setItem('language', newLang)
    }
    
    const handleLogout = () => {
      authStore.logout()
      isUserMenuOpen.value = false
      isMenuOpen.value = false
    }
    
    // إغلاق القوائم عند النقر خارجها
    const handleClickOutside = (event) => {
      if (!event.target.closest('.relative')) {
        isUserMenuOpen.value = false
      }
    }
    
    onMounted(() => {
      document.addEventListener('click', handleClickOutside)
    })
    
    onUnmounted(() => {
      document.removeEventListener('click', handleClickOutside)
    })
    
    return {
      authStore,
      themeStore,
      isMenuOpen,
      isUserMenuOpen,
      navItems,
      toggleLanguage,
      handleLogout
    }
  }
}
</script>