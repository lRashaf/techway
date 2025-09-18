<template>
  <Layout>
    <div class="space-y-16">
      <!-- Hero Section -->
      <section class="relative bg-gradient-to-br from-blue-600 via-blue-700 to-blue-800 text-white overflow-hidden">
        <div class="absolute inset-0  bg-[#c49eff]/70"></div>
        <div class="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=1600')] bg-cover bg-center opacity-10"></div>
        
        <div class="relative container mx-auto px-4 sm:px-6 lg:px-8 py-24 lg:py-32">
          <div class="max-w-4xl mx-auto text-center space-y-8">
            <h1 class="text-4xl sm:text-5xl lg:text-6xl font-bold leading-tight">
              <span class="block">{{ $t('hero_title') }}</span>
              <span class="block bg-gradient-to-r from-yellow-700 to-yellow-400 bg-clip-text text-transparent"> مع أفضل الخبراء </span>

            </h1>
            
            <p class="text-xl lg:text-2xl text-blue-100 max-w-3xl mx-auto leading-relaxed">
              {{ $t('hero_subtitle') }}
            </p>
            
            <div class="flex flex-col sm:flex-row items-center justify-center gap-4 pt-8">
              <router-link
                to="/courses"
                class="bg-white text-purple-600 hover:bg-blue-50 px-8 py-4 rounded-lg font-semibold text-lg transition-all duration-300 transform hover:scale-105 shadow-lg flex items-center space-x-2 rtl:space-x-reverse"
              >
                <span>{{ $t('get_started') }}</span>
                <ArrowRight :class="['w-5 h-5', $i18n.locale === 'ar' ? 'rotate-180' : '']" />
              </router-link>
              
              <router-link
                to="/about"
                class="border-2 border-white text-white hover:bg-white hover:text-purple-600 px-8 py-4 rounded-lg font-semibold text-lg transition-all duration-300"
              >
                {{ $t('about') }}
              </router-link>
            </div>

            <div class="flex items-center justify-center space-x-8 rtl:space-x-reverse pt-8 text-blue-200">
              <div class="text-center">
                <div class="text-2xl font-bold">500+</div>
                <div class="text-sm">طالب نشط</div>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold">{{ featuredCourses.length }}+</div>
                <div class="text-sm">كورس متاح</div>
              </div>
              <div class="text-center">
                <div class="text-2xl font-bold">4.8</div>
                <div class="text-sm flex items-center justify-center space-x-1 rtl:space-x-reverse">
                  <Star class="w-4 h-4 fill-current" />
                  <span>تقييم المنصة</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features Section -->
      <section class="container mx-auto px-4 sm:px-6 lg:px-8">
        <div class="text-center space-y-4 mb-16">
          <h2 class="text-3xl lg:text-4xl font-bold text-slate-900 dark:text-white">
            لماذا تختار TechWay؟
          </h2>
          <p class="text-lg text-slate-600 dark:text-slate-400 max-w-2xl mx-auto">
            نوفر لك أفضل تجربة تعليمية مع مميزات متقدمة وإرشاد شخصي
          </p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          <div
            v-for="(feature, index) in features"
            :key="index"
            class="course-card card p-6 text-center"
          >
            <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-blue-600 rounded-lg flex items-center justify-center mb-4 mx-auto">
              <component :is="feature.icon" class="w-6 h-6 text-white" />
            </div>
            <h3 class="text-lg font-semibold text-slate-900 dark:text-white mb-2">
              {{ feature.title }}
            </h3>
            <p class="text-slate-600 dark:text-slate-400 text-sm leading-relaxed">
              {{ feature.description }}
            </p>
          </div>
        </div>
      </section>

      <!-- Featured Courses Section -->
      <section class="bg-slate-50 dark:bg-slate-800/50 py-16">
        <div class="container mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex flex-col sm:flex-row items-center justify-between mb-12">
            <div>
              <h2 class="text-3xl lg:text-4xl font-bold text-slate-900 dark:text-white mb-2">
                الكورسات المميزة
              </h2>
              <p class="text-lg text-slate-600 dark:text-slate-400">
                اكتشف أفضل الكورسات من منصتنا
              </p>
            </div>
            <router-link
              to="/courses"
              class="bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded-lg font-medium transition-colors flex items-center space-x-2 rtl:space-x-reverse"
            >
              <span>عرض جميع الكورسات</span>
              <ArrowRight :class="['w-4 h-4', $i18n.locale === 'ar' ? 'rotate-180' : '']" />
            </router-link>
          </div>

          <!-- Loading State -->
          <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <div v-for="i in 6" :key="i" class="card p-6 animate-pulse">
              <div class="bg-slate-200 dark:bg-slate-700 h-48 rounded-lg mb-4"></div>
              <div class="space-y-3">
                <div class="bg-slate-200 dark:bg-slate-700 h-4 rounded w-3/4"></div>
                <div class="bg-slate-200 dark:bg-slate-700 h-4 rounded w-1/2"></div>
                <div class="bg-slate-200 dark:bg-slate-700 h-4 rounded w-1/4"></div>
              </div>
            </div>
          </div>

          <!-- Courses Grid -->
          <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <CourseCard
              v-for="course in featuredCourses"
              :key="course.id"
              :course="course"
            />
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="bg-gradient-to-r from-slate-900 to-slate-800 text-white py-16">
        <div class="container mx-auto px-4 sm:px-6 lg:px-8 text-center space-y-8">
          <h2 class="text-3xl lg:text-4xl font-bold">
            ابدأ رحلتك التعليمية اليوم
          </h2>
          <p class="text-xl text-slate-300 max-w-2xl mx-auto">
            انضم لآلاف الطلاب الذين طوروا مهاراتهم وحققوا أهدافهم المهنية معنا
          </p>
          <router-link
            to="/courses"
            class="inline-block bg-purple-600 hover:bg-purple-700 text-white px-8 py-4 rounded-lg font-semibold text-lg transition-all duration-300 transform hover:scale-105"
          >
            استكشف الكورسات
          </router-link>
        </div>
      </section>
    </div>
  </Layout>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { courseService } from '@/services/api'
import { useToast } from 'vue-toastification'
import { BookOpen, Users, Award, Clock, ArrowRight, Star } from 'lucide-vue-next'
import Layout from '@/components/Layout.vue'
import CourseCard from '@/components/CourseCard.vue'

export default {
  name: 'Home',
  components: {
    Layout,
    CourseCard,
    ArrowRight,
    Star
  },
  setup() {
    const { t } = useI18n()
    const toast = useToast()
    
    const featuredCourses = ref([])
    const loading = ref(true)
    
    const features = [
      {
        icon: Users,
        title: t('expert_instructors'),
        description: 'مدربون معتمدون مع خبرة عملية واسعة'
      },
      {
        icon: Clock,
        title: t('flexible_learning'),
        description: 'تعلم في الوقت والمكان المناسب لك'
      },
      {
        icon: Award,
        title: t('certification'),
        description: 'احصل على شهادات معتمدة عند إكمال الكورسات'
      },
      {
        icon: BookOpen,
        title: t('community'),
        description: 'انضم لمجتمع من المتعلمين والخبراء'
      }
    ]
    
    const fetchFeaturedCourses = async () => {
      try {
        const response = await courseService.getAllCourses()
        // أخذ أول 6 كورسات كمميزة
        featuredCourses.value = response.data.slice(0, 6)
      } catch (error) {
        console.error('Error fetching courses:', error)
        toast.error('حدث خطأ في جلب الكورسات')
      } finally {
        loading.value = false
      }
    }
    
    onMounted(() => {
      fetchFeaturedCourses()
    })
    
    return {
      featuredCourses,
      loading,
      features
    }
  }
}
</script>