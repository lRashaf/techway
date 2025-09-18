<template>
  <Layout>
    <div class="py-8">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Loading -->
        <div v-if="loading" class="animate-pulse space-y-8">
          <div class="bg-slate-200 dark:bg-slate-700 h-8 w-32 rounded"></div>
          <div class="bg-slate-200 dark:bg-slate-700 h-64 rounded-xl"></div>
          <div class="space-y-4">
            <div class="bg-slate-200 dark:bg-slate-700 h-8 w-3/4 rounded"></div>
            <div class="bg-slate-200 dark:bg-slate-700 h-4 w-1/2 rounded"></div>
            <div class="bg-slate-200 dark:bg-slate-700 h-32 rounded"></div>
          </div>
        </div>

        <div v-else-if="course">
          <!-- Back Button -->
          <button
            @click="$router.go(-1)"
            class="flex items-center space-x-2 rtl:space-x-reverse text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white mb-6 transition-colors"
          >
            <ArrowLeft :class="['w-5 h-5', $i18n.locale === 'ar' ? 'rotate-180' : '']" />
            <span>العودة</span>
          </button>

          <!-- Course Header -->
          <div class="mb-8">
            <div class="flex items-start justify-between mb-4">
              <div>
                <h1 class="text-3xl lg:text-4xl font-bold text-slate-900 dark:text-white mb-2">
                  {{ course.title }}
                </h1>
                <div class="flex items-center space-x-2 rtl:space-x-reverse text-slate-600 dark:text-slate-400">
                  <User class="w-4 h-4" />
                  <span>{{ course.instructor }}</span>
                  <span>•</span>
                  <span>{{ course.location || 'الرياض' }}</span>
                  <span>•</span>
                  <span>{{ course.duration || 30 }} ساعة</span>
                </div>
                
                <!-- Progress Bar - جديد -->
                <div v-if="enrolled && isOnlineCourse && courseProgress" class="mt-3">
                  <div class="flex items-center justify-between text-sm mb-1">
                    <span class="text-slate-600 dark:text-slate-400">التقدم الإجمالي</span>
                    <span class="font-medium text-slate-900 dark:text-white">{{ courseProgress.percent }}%</span>
                  </div>
                  <div class="w-full bg-slate-200 dark:bg-slate-700 rounded-full h-2">
                    <div 
                      class="bg-gradient-to-r from-purple-500 to-blue-500 h-2 rounded-full transition-all duration-500"
                      :style="{ width: `${courseProgress.percent}%` }"
                    ></div>
                  </div>
                </div>
              </div>
              
              <!-- Course Actions -->
              <div class="flex items-center space-x-3 rtl:space-x-reverse">
                <button @click="handleFavoriteToggle" class="p-2 rounded-lg border border-slate-300 dark:border-slate-600 hover:bg-slate-50 dark:hover:bg-slate-800">
                  <Heart :class="['w-5 h-5', isFavorited ? 'text-red-500 fill-current' : 'text-slate-400']" />
                </button>
                <button @click="handleShare" class="p-2 rounded-lg border border-slate-300 dark:border-slate-600 hover:bg-slate-50 dark:hover:bg-slate-800">
                  <Share2 class="w-5 h-5 text-slate-400" />
                </button>
              </div>
            </div>
          </div>

          <!-- Main Content Layout -->
          <div class="grid grid-cols-1 lg:grid-cols-4 gap-8">
            
            <!-- Left: Video Player + Course Content -->
            <div class="lg:col-span-3 space-y-6">
              
              <!-- Quiz Section (إضافة جديدة) -->
              <div v-if="showQuiz" class="quiz-section mb-6">
                <Quiz 
                  :course-id="Number(route.params.id)"
                  :user-id="userId"
                  @quiz-completed="handleQuizCompleted"
                  @quiz-failed="handleQuizFailed"
                />
              </div>

              <!-- Certificate Section (إضافة جديدة) -->
              <div v-if="showCertificate" class="certificate-section mb-6">
                <Certificate 
                  :course-id="Number(route.params.id)"
                  :user-id="userId"
                  @go-to-quiz="startQuiz"
                />
              </div>
              
              <!-- Video Player Section (For Online Courses) -->
              <div v-if="isOnlineCourse && currentLesson && !showQuiz && !showCertificate" class="space-y-4">
                <div class="rounded-xl overflow-hidden bg-black relative">
                  <!-- Lesson Lock Overlay - محسن -->
                  <div v-if="isCurrentLessonLocked" class="absolute inset-0 bg-black/80 backdrop-blur-sm z-10 flex items-center justify-center">
                    <div class="text-center text-white p-6">
                      <div class="w-16 h-16 bg-white/20 rounded-full flex items-center justify-center mx-auto mb-4">
                        <Lock class="w-8 h-8" />
                      </div>
                      <h3 class="text-xl font-semibold mb-2">هذا الدرس مقفول</h3>
                      <p class="text-white/80 mb-4">{{ lockMessage }}</p>
                      <button 
                        @click="openPaywall"
                        class="px-6 py-2 bg-purple-600 hover:bg-purple-700 rounded-lg font-semibold transition"
                      >
                        فتح الوصول
                      </button>
                    </div>
                  </div>
                  
                  <VideoPlayer 
                    :key="currentLessonId"
                    :src="videoSrc" 
                    @ended="handleVideoEnded"
                    @progress="handleVideoProgress"
                    class="w-full aspect-video"
                  />
                </div>
                
                <!-- Video Controls -->
                <div class="flex items-center justify-between">
                  <div class="flex-1">
                    <h3 class="font-semibold text-slate-900 dark:text-white">{{ currentLesson.title }}</h3>
                    <p class="text-sm text-slate-600 dark:text-slate-400">
                      الوحدة {{ currentModuleIndex + 1 }}: {{ currentModule?.title }}
                    </p>
                    
                    <!-- Lesson Progress - جديد -->
                    <div v-if="enrolled && !isCurrentLessonLocked" class="mt-2">
                      <div class="flex items-center justify-between text-xs mb-1">
                        <span class="text-slate-500 dark:text-slate-400">تقدم الدرس</span>
                        <span class="font-medium text-slate-700 dark:text-slate-300">{{ currentLessonProgress }}%</span>
                      </div>
                      <div class="w-full bg-slate-200 dark:bg-slate-700 rounded-full h-1">
                        <div 
                          class="bg-purple-500 h-1 rounded-full transition-all duration-300"
                          :style="{ width: `${currentLessonProgress}%` }"
                        ></div>
                      </div>
                    </div>
                  </div>
                  
                  <div class="flex items-center space-x-2 rtl:space-x-reverse ml-4">
                    <button 
                      @click="goToPrevious"
                      :disabled="!hasPrevious"
                      class="px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition disabled:opacity-50"
                    >
                      السابق
                    </button>
                    <button 
                      @click="goToNext"
                      :disabled="!hasNext"
                      class="px-4 py-2 rounded-lg bg-purple-600 hover:bg-purple-700 text-white font-semibold transition disabled:opacity-50"
                    >
                      التالي
                    </button>
                  </div>
                </div>
              </div>

              <!-- Physical Course Info (For Physical Courses) -->
              <div v-else-if="isPhysicalCourse && !showQuiz && !showCertificate" class="space-y-6">
                <div class="relative rounded-xl overflow-hidden">
                  <img
                    :src="course.imageUrl || 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800'"
                    :alt="course.title"
                    class="w-full h-64 md:h-80 object-cover"
                  />
                  <div class="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent">
                    <div class="absolute bottom-4 left-4 rtl:left-auto rtl:right-4 text-white">
                      <h3 class="text-lg font-semibold mb-2">كورس حضوري</h3>
                      <div class="flex items-center space-x-2 rtl:space-x-reverse text-sm">
                        <MapPin class="w-4 h-4" />
                        <span>{{ course.location || 'الرياض' }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Payment Required for Physical -->
                <div v-if="!isPaid" class="text-center py-8">
                  <h3 class="text-xl font-semibold text-slate-900 dark:text-white mb-4">
                    الدفع مطلوب للحصول على تفاصيل الموقع
                  </h3>
                  <button 
                    @click="startPayment"
                    :disabled="primaryLoading"
                    class="px-6 py-3 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg transition disabled:opacity-50"
                  >
                    {{ primaryLoading ? 'جاري التحويل...' : 'ادفع الآن' }}
                  </button>
                </div>

                <!-- Location Details (After Payment) -->
                <div v-else class="card p-6">
                  <h3 class="font-semibold text-slate-900 dark:text-white mb-4">تفاصيل الموقع</h3>
                  <div class="space-y-3">
                    <div class="flex items-center space-x-3 rtl:space-x-reverse">
                      <MapPin class="w-5 h-5 text-slate-500" />
                      <span>{{ course.fullAddress || course.location || 'الرياض، السعودية' }}</span>
                    </div>
                    <div class="flex items-center space-x-3 rtl:space-x-reverse">
                      <Clock class="w-5 h-5 text-slate-500" />
                      <span>المدة: {{ course.duration || 30 }} ساعة</span>
                    </div>
                  </div>
                  <button
                    @click="$router.push({ name: 'CoursesMap' })"
                    class="mt-4 w-full px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700 transition"
                  >
                    عرض على الخريطة
                  </button>
                </div>
              </div>

              <!-- Course Description -->
              <div v-if="!showQuiz && !showCertificate" class="card p-6">
                <div class="border-b border-slate-200 dark:border-slate-700 pb-4 mb-4">
                  <div class="flex space-x-4 rtl:space-x-reverse">
                    <button 
                      :class="['px-4 py-2 rounded-lg text-sm font-medium transition', 
                               activeTab === 'description' ? 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300' : 'text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100']"
                      @click="activeTab = 'description'"
                    >
                      الوصف
                    </button>
                    <button 
                      :class="['px-4 py-2 rounded-lg text-sm font-medium transition',
                               activeTab === 'instructor' ? 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300' : 'text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100']"
                      @click="activeTab = 'instructor'"
                    >
                      المدرب
                    </button>
                    <!-- تبويب التقدم - جديد -->
                    <button 
                      v-if="enrolled && courseProgress"
                      :class="['px-4 py-2 rounded-lg text-sm font-medium transition',
                               activeTab === 'progress' ? 'bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300' : 'text-slate-600 hover:text-slate-900 dark:text-slate-400 dark:hover:text-slate-100']"
                      @click="activeTab = 'progress'"
                    >
                      التقدم
                    </button>
                  </div>
                </div>

                <div v-if="activeTab === 'description'">
                  <p class="text-slate-700 dark:text-slate-300 leading-relaxed">
                    {{ course.description || 'لا يوجد وصف متاح حالياً.' }}
                  </p>
                </div>

                <div v-else-if="activeTab === 'instructor'">
                  <div class="flex items-start space-x-4 rtl:space-x-reverse">
                    <div class="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-full flex items-center justify-center">
                      <User class="w-6 h-6 text-purple-600 dark:text-purple-400" />
                    </div>
                    <div>
                      <h4 class="font-semibold text-slate-900 dark:text-white">{{ course.instructor }}</h4>
                      <p class="text-sm text-slate-600 dark:text-slate-400 mt-1">
                        خبير في مجال التكنولوجيا مع خبرة عملية واسعة
                      </p>
                    </div>
                  </div>
                </div>

                <!-- تبويب التقدم - جديد -->
                <div v-else-if="activeTab === 'progress' && courseProgress">
                  <div class="space-y-4">
                    <div class="text-center">
                      <div class="text-3xl font-bold text-purple-600 dark:text-purple-400 mb-1">
                        {{ courseProgress.percent }}%
                      </div>
                      <p class="text-sm text-slate-600 dark:text-slate-400">من الكورس مكتمل</p>
                    </div>
                    
                    <!-- تقدم الوحدات -->
                    <div class="space-y-3">
                      <h5 class="font-medium text-slate-900 dark:text-white">تقدم الوحدات</h5>
                      <div v-for="moduleProgress in courseProgress.modules" :key="moduleProgress.moduleId" class="space-y-1">
                        <div class="flex items-center justify-between text-sm">
                          <span class="text-slate-600 dark:text-slate-400">
                            {{ getModuleName(moduleProgress.moduleId) }}
                          </span>
                          <span class="font-medium text-slate-900 dark:text-white">{{ moduleProgress.percent }}%</span>
                        </div>
                        <div class="w-full bg-slate-200 dark:bg-slate-700 rounded-full h-1.5">
                          <div 
                            class="bg-purple-500 h-1.5 rounded-full transition-all duration-300"
                            :style="{ width: `${moduleProgress.percent}%` }"
                          ></div>
                        </div>
                      </div>
                    </div>

                    <!-- إضافة معلومات حالة الكويز -->
                    <div v-if="allModulesCompleted" class="border-t border-slate-200 dark:border-slate-700 pt-4">
                      <div class="flex items-center space-x-3 rtl:space-x-reverse p-3 rounded-lg bg-emerald-50 dark:bg-emerald-900/20">
                        <CheckCircle class="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
                        <div>
                          <p class="font-medium text-emerald-900 dark:text-emerald-100">جاهز للاختبار النهائي</p>
                          <p class="text-sm text-emerald-700 dark:text-emerald-300">أكملت جميع الوحدات بنجاح</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Right: Course Content Sidebar -->
            <div class="lg:col-span-1">
              <!-- Enrollment/Payment Status -->
              <div class="card p-6 mb-6">
                <div class="text-center mb-4">
                  <div v-if="course.isFree" class="text-2xl font-bold text-emerald-600 dark:text-emerald-400 mb-2">
                    مجاني
                  </div>
                  <div v-else class="text-2xl font-bold text-slate-900 dark:text-white mb-2">
                    {{ formatPrice(course.price) }} ر.س
                  </div>
                  <p class="text-sm text-slate-600 dark:text-slate-400">
                    {{ enrollmentStatusText }}
                  </p>
                </div>

                <button
                  @click="handlePrimaryAction"
                  :disabled="primaryLoading"
                  class="w-full px-4 py-3 rounded-lg bg-purple-600 hover:bg-purple-700 text-white font-semibold transition disabled:opacity-50 mb-3"
                >
                  {{ primaryButtonText }}
                </button>

                <div v-if="!course.isFree && isOnlineCourse" class="text-xs text-slate-500 dark:text-slate-400 text-center">
                  أول وحدتين مجانية للتجربة
                </div>
              </div>

              <!-- Course Content (For Online Courses) -->
              <div v-if="isOnlineCourse" class="card p-6">
                <h3 class="font-semibold text-slate-900 dark:text-white mb-4">محتوى الكورس</h3>
                <ModulesList 
                  :modules="outline?.modules || []"
                  :current-lesson-id="currentLessonId"
                  :is-paid="isPaid"
                  :enrolled="enrolled"
                  :course-progress="courseProgress"
                  @lesson-select="selectLesson"
                  @locked-lesson="openPaywall"
                />
                
                <!-- Quiz Module (إضافة جديدة) -->
                <div v-if="shouldShowQuiz" class="mt-4 border-t border-slate-200 dark:border-slate-700 pt-4">
                  <div class="quiz-module-item">
                    <div class="flex items-center space-x-3 rtl:space-x-reverse p-3 rounded-lg bg-purple-50 dark:bg-purple-900/20 border border-purple-200 dark:border-purple-800">
                      <div class="w-8 h-8 rounded-full bg-purple-100 dark:bg-purple-800 flex items-center justify-center">
                        <HelpCircle class="w-4 h-4 text-purple-600 dark:text-purple-400" />
                      </div>
                      
                      <div class="flex-1">
                        <h4 class="font-medium text-purple-900 dark:text-purple-100">الاختبار النهائي</h4>
                        <p class="text-sm text-purple-700 dark:text-purple-300">اختبر معلوماتك واحصل على الشهادة</p>
                      </div>
                      
                      <button
                        @click="startQuiz"
                        class="px-4 py-2 text-sm font-medium bg-purple-600 hover:bg-purple-700 text-white rounded-lg transition"
                      >
                        ابدأ الاختبار
                      </button>
                    </div>
                  </div>
                </div>
                
                <!-- Certificate Access (إضافة جديدة) -->
                <div v-if="quizCompleted" class="mt-4 border-t border-slate-200 dark:border-slate-700 pt-4">
                  <div class="certificate-access">
                    <div class="flex items-center space-x-3 rtl:space-x-reverse p-3 rounded-lg bg-emerald-50 dark:bg-emerald-900/20 border border-emerald-200 dark:border-emerald-800">
                      <div class="w-8 h-8 rounded-full bg-emerald-100 dark:bg-emerald-800 flex items-center justify-center">
                        <Award class="w-4 h-4 text-emerald-600 dark:text-emerald-400" />
                      </div>
                      
                      <div class="flex-1">
                        <h4 class="font-medium text-emerald-900 dark:text-emerald-100">شهادة الإتمام</h4>
                        <p class="text-sm text-emerald-700 dark:text-emerald-300">أحسنت! حصلت على الشهادة</p>
                      </div>
                      
                      <button
                        @click="viewCertificate"
                        class="px-4 py-2 text-sm font-medium bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg transition"
                      >
                        عرض الشهادة
                      </button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Course Stats -->
              <div class="card p-6">
                <h3 class="font-semibold text-slate-900 dark:text-white mb-4">تفاصيل إضافية</h3>
                <div class="space-y-3 text-sm">
                  <div class="flex items-center justify-between">
                    <span class="text-slate-600 dark:text-slate-400">النوع</span>
                    <span class="font-medium text-slate-900 dark:text-white">
                      {{ courseTypeText }}
                    </span>
                  </div>
                  <div class="flex items-center justify-between">
                    <span class="text-slate-600 dark:text-slate-400">المدة</span>
                    <span class="font-medium text-slate-900 dark:text-white">
                      {{ course.duration || 30 }} ساعة
                    </span>
                  </div>
                  <div class="flex items-center justify-between">
                    <span class="text-slate-600 dark:text-slate-400">المستوى</span>
                    <span class="font-medium text-slate-900 dark:text-white">
                      متقدم
                    </span>
                  </div>
                  <div v-if="isOnlineCourse && outline?.modules" class="flex items-center justify-between">
                    <span class="text-slate-600 dark:text-slate-400">الوحدات</span>
                    <span class="font-medium text-slate-900 dark:text-white">
                      {{ outline.modules.length }} وحدة
                    </span>
                  </div>
                  
                  <!-- إضافة معلومات الكويز -->
                  <div v-if="shouldShowQuiz" class="flex items-center justify-between">
                    <span class="text-slate-600 dark:text-slate-400">الاختبار النهائي</span>
                    <span class="font-medium text-emerald-600 dark:text-emerald-400">
                      متاح
                    </span>
                  </div>
                  
                  <div v-if="quizCompleted" class="flex items-center justify-between">
                    <span class="text-slate-600 dark:text-slate-400">الشهادة</span>
                    <span class="font-medium text-emerald-600 dark:text-emerald-400">
                      صادرة
                    </span>
                  </div>
                  
                  <!-- إضافة إحصائيات التقدم -->
                  <div v-if="enrolled && courseProgress" class="border-t border-slate-200 dark:border-slate-700 pt-3 mt-3">
                    <div class="flex items-center justify-between">
                      <span class="text-slate-600 dark:text-slate-400">الدروس المكتملة</span>
                      <span class="font-medium text-slate-900 dark:text-white">
                        {{ completedLessonsCount }} / {{ totalLessonsCount }}
                      </span>
                    </div>
                    
                    <!-- إضافة حالة إكمال الكورس -->
                    <div v-if="allModulesCompleted" class="flex items-center justify-between mt-2">
                      <span class="text-slate-600 dark:text-slate-400">حالة الكورس</span>
                      <span class="font-medium text-emerald-600 dark:text-emerald-400">
                        جاهز للاختبار
                      </span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Payment Modal -->
        <PaymentModal 
          v-if="course"
          :open="paymentModalOpen" 
          :course-id="Number(route.params.id)"
          :course-title="course.title"
          :price="course.price"
          @close="paymentModalOpen = false" 
          @success="handlePaymentSuccess"
        />
      </div>
    </div>
  </Layout>
</template>

<script>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { courseService, paymentService, enrollmentService, progressService } from '@/services/api'
import { useToast } from 'vue-toastification'
import { Clock, MapPin, Star, Heart, User, ArrowLeft, Share2, Lock, HelpCircle, Award, CheckCircle } from 'lucide-vue-next'
import Layout from '@/components/Layout.vue'
import VideoPlayer from '@/components/player/VideoPlayer.vue'
import ModulesList from '@/components/course/ModulesList.vue'
import PaymentModal from '@/components/payments/PaymentModal.vue'
import Quiz from '@/components/course/Quiz.vue'
import Certificate from '@/components/course/Certificate.vue'
// في بداية CourseDetails.vue

export default {
  name: 'CourseDetails',
  components: { 
    Layout, Clock, MapPin, Star, Heart, User, ArrowLeft, Share2, Lock, HelpCircle, Award, CheckCircle,
    VideoPlayer, ModulesList, PaymentModal, Quiz, Certificate
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const { t, locale } = useI18n()
    const authStore = useAuthStore()
    const toast = useToast()

    // State
    const course = ref(null)
    const outline = ref(null)
    const loading = ref(true)
    const primaryLoading = ref(false)
    const isFavorited = ref(false)
    const enrolled = ref(false)
    const isPaid = ref(false)
    const paymentModalOpen = ref(false)
    const activeTab = ref('description')
    
    // Video Player State
    const currentLessonId = ref(null)
    const currentModuleIndex = ref(0)
    
    // Progress State - جديد
    const courseProgress = ref(null)
    const currentVideoTime = ref(0)
    const currentVideoDuration = ref(0)

    // Quiz & Certificate State - جديد
    const showQuiz = ref(false)
    const showCertificate = ref(false)
    const quizCompleted = ref(false)

    // Computed
    const userId = computed(() => authStore?.user?.id)
    const isRTL = computed(() => locale.value === 'ar')
    
    const isOnlineCourse = computed(() => {
      const type = course.value?.locationType || 'ONLINE'
      return type === 'ONLINE' || type === 'HYBRID'
    })
    
    const isPhysicalCourse = computed(() => {
      return course.value?.locationType === 'PHYSICAL'
    })

    const courseTypeText = computed(() => {
      const type = course.value?.locationType || 'ONLINE'
      const types = { ONLINE: 'أونلاين', PHYSICAL: 'حضوري', HYBRID: 'مختلط' }
      return types[type] || 'أونلاين'
    })

    // Current lesson and module
    const currentModule = computed(() => {
      if (!outline.value?.modules || currentModuleIndex.value < 0) return null
      return outline.value.modules[currentModuleIndex.value]
    })

    const currentLesson = computed(() => {
      if (!currentModule.value || !currentLessonId.value) return null
      return currentModule.value.lessons?.find(l => l.id === currentLessonId.value)
    })

    const videoSrc = computed(() => {
      return currentLesson.value?.videoUrl || ''
    })

    // Navigation
    const allLessons = computed(() => {
      if (!outline.value?.modules) return []
      const lessons = []
      outline.value.modules.forEach((module, moduleIndex) => {
        module.lessons?.forEach(lesson => {
          lessons.push({ lesson, module, moduleIndex })
        })
      })
      return lessons
    })

    const currentLessonIndex = computed(() => {
      return allLessons.value.findIndex(item => item.lesson.id === currentLessonId.value)
    })

    const hasPrevious = computed(() => currentLessonIndex.value > 0)
    const hasNext = computed(() => currentLessonIndex.value < allLessons.value.length - 1)

    // Progress computeds - جديد
    const currentLessonProgress = computed(() => {
      if (!courseProgress.value?.lessons || !currentLessonId.value) return 0
      return courseProgress.value.lessons[currentLessonId.value] || 0
    })

    const isCurrentLessonLocked = computed(() => {
      if (!currentLesson.value || !currentModule.value) return false
      return isLessonLocked(currentLesson.value, currentModule.value)
    })

    const lockMessage = computed(() => {
      if (!enrolled.value) return 'سجّل في الكورس أولاً لمشاهدة هذا المحتوى'
      if (course.value?.isFree || isPaid.value) return ''
      return 'هذا الدرس ضمن المحتوى المدفوع. أكمل الدفع للوصول'
    })

    const completedLessonsCount = computed(() => {
      if (!courseProgress.value?.lessons) return 0
      return Object.values(courseProgress.value.lessons).filter(progress => progress >= 100).length
    })

    const totalLessonsCount = computed(() => {
      if (!outline.value?.modules) return 0
      return outline.value.modules.reduce((total, module) => total + (module.lessons?.length || 0), 0)
    })

    // إضافة computed للتحقق من إكمال جميع الوحدات
    const allModulesCompleted = computed(() => {
      if (!courseProgress.value?.modules || !outline.value?.modules) return false
      
      const totalModules = outline.value.modules.filter(m => !m.isQuizModule).length
      const completedModules = courseProgress.value.modules.filter(m => m.percent >= 100).length
      
      return completedModules >= totalModules
    })

    const shouldShowQuiz = computed(() => {
      return allModulesCompleted.value && enrolled.value && (course.value?.isFree || isPaid.value)
    })

    // Enrollment status
    const enrollmentStatusText = computed(() => {
      if (!enrolled.value) return 'سجّل لبدء التعلم'
      if (course.value?.isFree || isPaid.value) return 'يمكنك الوصول لجميع المحتويات'
      return 'أكمل الدفع للوصول الكامل'
    })

    const primaryButtonText = computed(() => {
      if (!enrolled.value) return 'سجّل في الكورس'
      if (course.value?.isFree) return 'ابدأ التعلم'
      if (isPaid.value) return 'أكمل التعلم'
      return 'ادفع لفتح المحتوى'
    })

    // Helper functions - جديد
    const isLessonLocked = (lesson, module) => {
      // Free course or paid user can access everything
      if (course.value?.isFree || isPaid.value) return false
      
      // Not enrolled users see everything as locked
      if (!enrolled.value) return true
      
      // Free modules (first 2) are always accessible
      if (module.isFree) return false
      
      // Preview lessons are always accessible
      if (lesson.isPreview) return false
      
      // Everything else is locked for unpaid users
      return true
    }

    const getModuleName = (moduleId) => {
      const module = outline.value?.modules?.find(m => m.id === moduleId)
      return module?.title || `الوحدة ${moduleId}`
    }

    // Progress methods - جديد
    const fetchProgress = async () => {
      if (!userId.value || !route.params.id) {
        console.log('No userId or courseId for progress fetch')
        return
      }
      
      try {
        console.log(`Fetching progress for user ${userId.value} course ${route.params.id}`)
        
        // إصلاح: تمرير userId بشكل صحيح
        const res = await progressService.getCourseProgress(userId.value, Number(route.params.id))
        courseProgress.value = res.data || res
        
        console.log('Course progress loaded:', courseProgress.value)
        console.log('Lessons progress:', courseProgress.value?.lessons)
        
      } catch (e) {
        console.error('Error fetching progress:', e)
        // إنشاء بيانات فارغة بدلاً من null
        courseProgress.value = {
          courseId: Number(route.params.id),
          percent: 0,
          modules: [],
          lessons: {}
        }
      }
    }

    const updateLessonProgress = async (lessonId, progressPercent) => {
      if (!userId.value || !lessonId || isLessonLocked(currentLesson.value, currentModule.value)) {
        console.log('Cannot update progress: missing data or lesson locked')
        return
      }
      
      try {
        const payload = {
          lessonId: lessonId,
          progressPercent: Math.min(100, Math.max(0, progressPercent))
        }
        
        console.log('Updating lesson progress:', payload)
        
        // إصلاح: تمرير userId بشكل صحيح  
        const res = await progressService.updateLessonProgress(userId.value, payload)
        courseProgress.value = res.data || res
        
        console.log('Progress updated successfully:', courseProgress.value)
        
        if (progressPercent >= 100) {
          toast.success('تم إكمال الدرس!')
          
          // تحديث إحصائيات الإكمال
          await nextTick(() => {
            checkModuleCompletion()
            checkCourseCompletion()
          })
        }
        
      } catch (e) {
        console.error('Error updating progress:', e)
        toast.error('فشل في حفظ التقدم')
      }
    }

    // جديد: فحص إكمال الوحدة
    const checkModuleCompletion = () => {
      if (!courseProgress.value?.modules || !currentModule.value) return
      
      const moduleProgress = courseProgress.value.modules.find(
        m => m.moduleId === currentModule.value.id
      )
      
      if (moduleProgress && moduleProgress.percent >= 100) {
        toast.success(`أحسنت! أكملت وحدة "${currentModule.value.title}" كاملة`)
      }
    }

    // جديد: إظهار مودال إكمال الكورس
    const showCourseCompletionModal = () => {
      // يمكن إضافة مودال للتهنئة وإظهار الشهادة
      console.log('Course completed! Show certificate modal')
    }

    const handleVideoProgress = (currentTime, duration) => {
      if (!currentLessonId.value || !duration || isCurrentLessonLocked.value) return
      
      currentVideoTime.value = currentTime
      currentVideoDuration.value = duration
      
      const progressPercent = Math.round((currentTime / duration) * 100)
      
      // تسجيل التقدم عند النقاط المهمة فقط لتجنب الطلبات الكثيرة
      const milestones = [10, 25, 50, 75, 90, 100]
      const currentMilestone = milestones.find(m => 
        progressPercent >= m && (currentLessonProgress.value < m)
      )
      
      if (currentMilestone) {
        console.log(`Reached milestone: ${currentMilestone}%`)
        updateLessonProgress(currentLessonId.value, currentMilestone)
      }
    }

    // Quiz & Certificate methods - جديد
    const handleQuizCompleted = (result) => {
      quizCompleted.value = true
      showQuiz.value = false
      
      // تحديث التقدم
      fetchProgress()
      
      toast.success('أحسنت! أكملت الكورس بنجاح وحصلت على الشهادة')
      
      // إظهار الشهادة تلقائياً
      setTimeout(() => {
        showCertificate.value = true
      }, 2000)
    }

    const handleQuizFailed = (result) => {
      showQuiz.value = false
      toast.warning(`حصلت على ${Math.round(result.score)}% - تحتاج 70% للنجاح`)
    }

    const startQuiz = () => {
      if (!shouldShowQuiz.value) {
        toast.error('يجب إكمال جميع الوحدات أولاً')
        return
      }
      showQuiz.value = true
      showCertificate.value = false
    }

    const viewCertificate = () => {
      showCertificate.value = true
      showQuiz.value = false
    }

    // Methods
    const fetchCourse = async () => {
      const courseId = Number(route.params.id)
      if (!courseId) return
      loading.value = true
      try {
        const res = await courseService.getCourseById(courseId)
        course.value = res.data
      } catch (e) {
        toast.error(isRTL.value ? 'لم يتم العثور على الكورس' : 'Course not found')
        router.push('/courses')
      } finally {
        loading.value = false
      }
    }

    const fetchOutline = async () => {
      try {
        const res = await courseService.getCourseOutline(Number(route.params.id))
        outline.value = res.data || res
        
        console.log('Outline received:', outline.value)
        console.log('Modules:', outline.value?.modules)
        
        // Set first free lesson as current
        if (outline.value?.modules?.length && !currentLessonId.value) {
          const firstFreeModule = outline.value.modules.find(m => m.isFree) || outline.value.modules[0]
          console.log('First free module:', firstFreeModule)
          
          if (firstFreeModule?.lessons?.length) {
            const firstLesson = firstFreeModule.lessons[0]
            console.log('First lesson:', firstLesson)
            console.log('Video URL:', firstLesson.videoUrl)
            
            currentLessonId.value = firstLesson.id
            currentModuleIndex.value = outline.value.modules.indexOf(firstFreeModule)
          }
        }
      } catch (e) {
        console.error('Error fetching outline:', e)
        outline.value = null
      }
    }

    const fetchAccess = async () => {
      if (!userId.value) {
        enrolled.value = false
        isPaid.value = false
        return
      }
      try {
        const res = await enrollmentService.getAccess({ 
          userId: userId.value, 
          courseId: Number(route.params.id) 
        })
        const acc = res?.data || res
        enrolled.value = !!acc?.enrolled
        isPaid.value = !!acc?.paid
      } catch {
        enrolled.value = false
        isPaid.value = false
      }
    }

    const ensureAuthenticated = () => {
      if (!authStore.isAuthenticated) {
        toast.error('سجّل الدخول أولاً')
        router.push('/login')
        return false
      }
      return true
    }

    const enrollUser = async () => {
      if (!ensureAuthenticated()) return false
      if (!enrolled.value) {
        try {
          primaryLoading.value = true
          await enrollmentService.enroll({ 
            userId: userId.value, 
            courseId: Number(route.params.id) 
          })
          await fetchAccess()
          toast.success('تم تسجيلك في الكورس')
          return true
        } catch (e) {
          toast.error(e.message || 'تعذر التسجيل في الكورس')
          return false
        } finally {
          primaryLoading.value = false
        }
      }
      return true
    }

    const startPayment = async () => {
      if (!ensureAuthenticated()) return
      const enrolled = await enrollUser()
      if (!enrolled) return
      
      // بدلاً من استدعاء createPaymentSession، نعرض PaymentModal مباشرة
      paymentModalOpen.value = true
    }

    const handlePrimaryAction = async () => {
      if (!enrolled.value) {
        await enrollUser()
        return
      }
      
      if (course.value?.isFree || isPaid.value) {
        // Start learning
        startLearning()
        return
      }
      
      // Need payment
      await startPayment()
    }

    const startLearning = () => {
      if (isOnlineCourse.value && currentLessonId.value) {
        // Already showing video player, just scroll to top
        window.scrollTo({ top: 0, behavior: 'smooth' })
      } else if (isPhysicalCourse.value) {
        toast.info('هذا كورس حضوري - تفاصيل الموقع متاحة أعلاه')
      }
    }

    // إصلاح دالة selectLesson - محسنة
    const selectLesson = ({ lesson, module }) => {
      console.log('Selecting lesson:', lesson.title, 'ID:', lesson.id)
      console.log('Video URL:', lesson.videoUrl)
      
      // تحديث الدرس الحالي أولاً (للتنقل)
      currentLessonId.value = lesson.id
      const moduleIndex = outline.value?.modules?.indexOf(module) || 0
      currentModuleIndex.value = moduleIndex
      
      // إخفاء الكويز والشهادة عند تشغيل فيديو
      showQuiz.value = false
      showCertificate.value = false
      
      // إجبار إعادة تحديث الفيديو باستخدام nextTick
      nextTick(() => {
        console.log('Current video src after selection:', videoSrc.value)
        
        // Scroll to video player
        window.scrollTo({ top: 0, behavior: 'smooth' })
        
        // فحص القفل بعد التحديث (لا نمنع التنقل)
        if (isLessonLocked(lesson, module)) {
          setTimeout(() => {
            openPaywall()
          }, 100)
        }
      })
    }

    const openPaywall = () => {
      paymentModalOpen.value = true
    }

    // إصلاح أزرار التنقل - محسنة
    const goToPrevious = () => {
      if (!hasPrevious.value) return
      
      const prevItem = allLessons.value[currentLessonIndex.value - 1]
      
      console.log('Navigating to previous lesson:', prevItem.lesson.title)
      console.log('Video URL:', prevItem.lesson.videoUrl)
      
      // السماح بالتنقل دائماً
      currentLessonId.value = prevItem.lesson.id
      const moduleIndex = outline.value?.modules?.indexOf(prevItem.module) || 0
      currentModuleIndex.value = moduleIndex
      
      // إخفاء الكويز والشهادة عند التنقل
      showQuiz.value = false
      showCertificate.value = false
      
      // إجبار إعادة تحديث الفيديو
      nextTick(() => {
        console.log('Current video src after previous navigation:', videoSrc.value)
        
        // فحص القفل بعد التنقل
        if (isLessonLocked(prevItem.lesson, prevItem.module)) {
          setTimeout(() => {
            openPaywall()
          }, 100)
        }
      })
    }

    const goToNext = () => {
      if (!hasNext.value) return
      
      const nextItem = allLessons.value[currentLessonIndex.value + 1]
      
      console.log('Navigating to next lesson:', nextItem.lesson.title)
      console.log('Video URL:', nextItem.lesson.videoUrl)
      
      // السماح بالتنقل دائماً
      currentLessonId.value = nextItem.lesson.id
      const moduleIndex = outline.value?.modules?.indexOf(nextItem.module) || 0
      currentModuleIndex.value = moduleIndex
      
      // إخفاء الكويز والشهادة عند التنقل
      showQuiz.value = false
      showCertificate.value = false
      
      // إجبار إعادة تحديث الفيديو
      nextTick(() => {
        console.log('Current video src after next navigation:', videoSrc.value)
        
        // فحص القفل بعد التنقل
        if (isLessonLocked(nextItem.lesson, nextItem.module)) {
          setTimeout(() => {
            openPaywall()
          }, 100)
        }
      })
    }

    const handleVideoEnded = async () => {
      if (!currentLessonId.value || isCurrentLessonLocked.value) return
      
      // تسجيل 100% مكتمل
      await updateLessonProgress(currentLessonId.value, 100)
      
      toast.success('تم إكمال الدرس!')
      
      // الانتقال للدرس التالي بعد ثانيتين
      if (hasNext.value) {
        setTimeout(() => {
          goToNext()
        }, 2000)
      } else {
        // تحقق من إكمال الكورس
        checkCourseCompletion()
      }
    }

    const checkCourseCompletion = () => {
      if (!courseProgress.value) return
      
      const { percent } = courseProgress.value
      
      if (percent >= 100) {
        toast.success('أحسنت! أنهيت الكورس كاملاً 🎉')
        // يمكن إضافة إظهار الشهادة هنا
        showCourseCompletionModal()
      } else if (percent >= 75) {
        toast.success(`أحسنت! وصلت لـ ${percent}% من الكورس`)
      }
    }

    const handlePaymentSuccess = async () => {
      paymentModalOpen.value = false
      toast.success('تم تأكيد الدفع - المحتوى مفتوح الآن')
      await fetchAccess()
      await fetchOutline()
      await fetchProgress()
    }

    const handleFavoriteToggle = async () => {
      if (!ensureAuthenticated()) return
      isFavorited.value = !isFavorited.value
      toast.success(isFavorited.value ? 'أُضيف للمفضلة' : 'أُزيل من المفضلة')
    }

    const handleShare = async () => {
      try {
        if (navigator.share) {
          await navigator.share({ 
            title: course.value?.title || '', 
            url: window.location.href 
          })
        } else {
          await navigator.clipboard.writeText(window.location.href)
          toast.success('تم نسخ الرابط')
        }
      } catch {}
    }

    const formatPrice = (price) => {
      const num = Number(price)
      if (!Number.isFinite(num) || num <= 0) return '0'
      return num.toLocaleString('ar-SA')
    }

    // Watchers - جديد
    watch(currentLessonId, (newLessonId, oldLessonId) => {
      if (newLessonId !== oldLessonId) {
        console.log(`Lesson changed from ${oldLessonId} to ${newLessonId}`)
        console.log('New video URL:', videoSrc.value)
        
        if (courseProgress.value) {
          const progress = courseProgress.value.lessons?.[newLessonId] || 0
          console.log(`New lesson progress: ${progress}%`)
        }
      }
    }, { immediate: true })

    // إضافة watcher للـ videoSrc للتأكد من التحديث
    watch(videoSrc, (newSrc, oldSrc) => {
      if (newSrc !== oldSrc) {
        console.log(`Video source changed from ${oldSrc} to ${newSrc}`)
      }
    }, { immediate: true })

    // Lifecycle
    onMounted(async () => {
      await fetchCourse()
      await fetchOutline()
      await fetchAccess()
      await fetchProgress()

      // Handle payment success redirect
      if (route.query.paid === '1') {
        toast.success('تم تأكيد الدفع - المحتوى مفتوح الآن')
        await fetchAccess()
        await fetchOutline()
        await fetchProgress()
        const { paid, ...rest } = route.query
        router.replace({ query: rest })
      }
    })

    return {
      route,
      course,
      userId,
      outline,
      loading,
      primaryLoading,
      isFavorited,
      enrolled,
      isPaid,
      paymentModalOpen,
      activeTab,
      currentLessonId,
      currentModuleIndex,
      courseProgress,
      currentLessonProgress,
      isCurrentLessonLocked,
      lockMessage,
      completedLessonsCount,
      totalLessonsCount,
      isOnlineCourse,
      isPhysicalCourse,
      courseTypeText,
      currentModule,
      currentLesson,
      videoSrc,
      hasPrevious,
      hasNext,
      enrollmentStatusText,
      primaryButtonText,
      getModuleName,
      handlePrimaryAction,
      selectLesson,
      openPaywall,
      goToPrevious,
      goToNext,
      handleVideoEnded,
      handleVideoProgress,
      handlePaymentSuccess,
      handleFavoriteToggle,
      handleShare,
      startPayment,
      formatPrice,
      // Quiz & Certificate returns - جديد
      showQuiz,
      showCertificate,
      quizCompleted,
      allModulesCompleted,
      shouldShowQuiz,
      handleQuizCompleted,
      handleQuizFailed,
      startQuiz,
      viewCertificate
    }
  }
}
</script>