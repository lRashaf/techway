<template>
  <div class="course-card card overflow-hidden">
    <router-link :to="`/courses/${course.id}`" class="block">
      <div class="relative">
        <img
          :src="course.imageUrl || 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=600'"
          :alt="course.title"
          class="w-full h-48 object-cover"
        />
        <div class="absolute top-4 left-4 rtl:left-auto rtl:right-4">
          <span :class="[
            'inline-block px-2 py-1 rounded-full text-xs font-medium',
            course.isFree 
              ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
              : 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-purple-400'
          ]">
            {{ course.isFree ? $t('free') : $t('paid') }}
          </span>
        </div>
        
        <button
          v-if="showFavoriteButton && authStore.isAuthenticated"
          @click.prevent="handleFavoriteToggle"
          :disabled="favoriteLoading"
          :class="[
            'absolute top-4 right-4 rtl:right-auto rtl:left-4 p-2 rounded-full transition-all',
            isFavorited
              ? 'bg-red-500 text-black shadow-lg'
              : 'bg-white/90 dark:bg-slate-800/90 text-slate-600 dark:text-slate-400 hover:bg-white dark:hover:bg-slate-800',
            favoriteLoading ? 'opacity-50' : ''
          ]"
        >
          <Heart :class="['w-4 h-4', isFavorited ? 'fill-current' : '']" />
        </button>
      </div>

      <div class="p-6 space-y-4">
        <div>
          <h3 class="text-lg font-semibold text-slate-900 dark:text-white line-clamp-2 mb-2">
            {{ course.title }}
          </h3>
          <p class="text-sm text-slate-600 dark:text-slate-400 line-clamp-2">
            {{ course.description }}
          </p>
        </div>

        <div class="flex items-center justify-between text-sm text-slate-500 dark:text-slate-400">
          <div class="flex items-center space-x-1 rtl:space-x-reverse">
            <Clock class="w-4 h-4" />
            <span>{{ course.duration }} {{ $t('hours') }}</span>
          </div>
          <div class="flex items-center space-x-1 rtl:space-x-reverse">
            <MapPin class="w-4 h-4" />
            <span>{{ course.location }}</span>
          </div>
        </div>

        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-1 rtl:space-x-reverse">
            <Star class="w-4 h-4 text-yellow-400 fill-current" />
            <span class="text-sm font-medium text-slate-700 dark:text-slate-300">
              {{ (course.rating || 0).toFixed(1) }}
            </span>
          </div>
          <span 
            v-if="course.level" 
            :class="[
              'text-sm px-2 py-1 rounded-md',
              levelClasses[course.level]
            ]"
          >
            {{ levelTexts[course.level] }}
          </span>
        </div>

        <div class="flex items-center justify-between pt-4 border-t border-slate-200 dark:border-slate-700">
          <div class="text-lg font-bold text-slate-900 dark:text-black">
            <span v-if="course.isFree" class="text-green-600 dark:text-green-400">
              {{ $t('free') }}
            </span>
            <div v-else class="flex items-center space-x-1 rtl:space-x-reverse">
              <span>{{ course.price }}</span>
              <span class="text-sm text-slate-500">ريال</span>
            </div>
          </div>
          <div class="text-sm text-slate-600 dark:text-slate-400">
            {{ course.instructor }}
          </div>
        </div>
      </div>
    </router-link>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { Clock, MapPin, Star, Heart } from 'lucide-vue-next'
import { useToast } from 'vue-toastification'

export default {
  name: 'CourseCard',
  components: {
    Clock,
    MapPin,
    Star,
    Heart
  },
  props: {
    course: {
      type: Object,
      required: true
    },
    showFavoriteButton: {
      type: Boolean,
      default: true
    }
  },
  setup(props) {
    const { t, locale } = useI18n()
    const authStore = useAuthStore()
    const toast = useToast()
    
    const isFavorited = ref(false)
    const favoriteLoading = ref(false)
    
    const isRTL = computed(() => locale.value === 'ar')
    
    const levelTexts = computed(() => ({
      beginner: isRTL.value ? 'مبتدئ' : 'Beginner',
      intermediate: isRTL.value ? 'متوسط' : 'Intermediate',
      advanced: isRTL.value ? 'متقدم' : 'Advanced'
    }))
    
    const levelClasses = {
      beginner: 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400',
      intermediate: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-black-400',
      advanced: 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
    }
    
    const handleFavoriteToggle = async (event) => {
      event.preventDefault()
      event.stopPropagation()

      if (!authStore.isAuthenticated) {
        toast.error(isRTL.value ? 'يجب تسجيل الدخول أولاً' : 'Please login first')
        return
      }

      favoriteLoading.value = true
      try {
        // محاكاة API call للمفضلة - يمكن ربطها بـ Spring Boot لاحقاً
        await new Promise(resolve => setTimeout(resolve, 500))
        
        isFavorited.value = !isFavorited.value
        toast.success(
          isFavorited.value 
            ? t('course_added_to_favorites')
            : t('course_removed_from_favorites')
        )
      } catch (error) {
        toast.error(isRTL.value ? 'حدث خطأ، حاول مرة أخرى' : 'Something went wrong')
      } finally {
        favoriteLoading.value = false
      }
    }
    
    return {
      authStore,
      isFavorited,
      favoriteLoading,
      levelTexts,
      levelClasses,
      handleFavoriteToggle
    }
  }
}
</script>