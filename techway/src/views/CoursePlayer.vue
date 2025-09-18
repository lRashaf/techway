<template>
  <Layout>
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <button
        @click="$router.push({ name: 'CourseDetails', params: { id: courseId } })"
        class="mb-4 text-sm text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white"
      >
        ← الرجوع إلى تفاصيل الكورس
      </button>

      <div v-if="loading" class="animate-pulse h-64 bg-slate-200 dark:bg-slate-700 rounded-xl"></div>

      <div v-else-if="currentLesson">
        <h1 class="text-xl md:text-2xl font-bold text-slate-900 dark:text-white mb-3">
          {{ currentLesson.title }}
        </h1>

        <VideoPlayer :src="videoSrc" @ended="handleEnded" />

        <NextPrevControls
          class="mt-3"
          :prev="prevLesson"
          :next="nextLesson"
          @goPrev="goPrev"
          @goNext="goNext"
        />

        <div class="mt-6 grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div class="lg:col-span-2">
            <div class="card p-4">
              <div class="border-b pb-2 mb-3">
                <button
                  class="px-3 py-1 rounded-md text-sm bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300"
                >
                  الوصف
                </button>
                <!-- تبويبات أخرى لاحقًا: مواد، أسئلة -->
              </div>
              <p class="text-slate-700 dark:text-slate-300">
                استمتع بالمشاهدة، تقدّمك يُحفظ تلقائيًا بعد الانتهاء من الفيديو.
              </p>
            </div>
          </div>

          <div>
            <div class="card p-4">
              <h3 class="font-semibold text-slate-900 dark:text-white mb-3">محتوى الكورس</h3>
              <CourseOutline
                v-if="outline"
                :outline="outline"
                :isPaid="isPaid"
                @play="jumpTo"
                @locked="openPaywall"
              />
            </div>
          </div>
        </div>
      </div>

      <Paywall :open="paywallOpen" @close="paywallOpen=false" @pay="startPay" />
    </div>
  </Layout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Layout from '@/components/Layout.vue'
import CourseOutline from '@/components/course/CourseOutline.vue'
import VideoPlayer from '@/components/player/VideoPlayer.vue'
import NextPrevControls from '@/components/player/NextPrevControls.vue'
import Paywall from '@/components/payments/Paywall.vue'
import { courseService, paymentService } from '@/services/api'
import { useAuthStore } from '@/stores/auth'
import { useToast } from 'vue-toastification'

const route = useRoute()
const router = useRouter()
const toast = useToast()
const auth = useAuthStore()

const courseId = Number(route.params.courseId)
const lessonIdParam = route.params.lessonId ? Number(route.params.lessonId) : null

const outline = ref(null)
const loading = ref(true)
const isPaid = ref(false) // يمكنك قراءتها من Enrollment لاحقاً
const paywallOpen = ref(false)

const flatLessons = computed(() => {
  if (!outline.value?.modules) return []
  const arr = []
  outline.value.modules.forEach(m => {
    m.lessons.forEach(l => arr.push({ module: m, lesson: l }))
  })
  return arr
})

const currentIndex = computed(() => {
  return flatLessons.value.findIndex(x => x.lesson.id === (currentLessonId.value))
})

const prevLesson = computed(() => {
  const i = currentIndex.value
  if (i > 0) return flatLessons.value[i - 1].lesson
  return null
})
const nextLesson = computed(() => {
  const i = currentIndex.value
  if (i >= 0 && i < flatLessons.value.length - 1) return flatLessons.value[i + 1].lesson
  return null
})

const currentLessonId = ref(lessonIdParam)
const currentLesson = computed(() => {
  return flatLessons.value.find(x => x.lesson.id === currentLessonId.value)?.lesson || null
})
const videoSrc = computed(() => currentLesson.value?.videoUrl || '')

const fetchOutline = async () => {
  loading.value = true
  try {
    const res = await courseService.getCourseOutline(courseId) // سنضيفها في CourseDetails أو api wrapper لاحق
    outline.value = res.data || res // حسب interceptor
    // لو ما وصل lessonId، خذ أول درس مفتوح
    if (!currentLessonId.value) {
      const firstOpen = flatLessons.value.find(x => !x.lesson.isLocked || x.module.isFree)
      currentLessonId.value = firstOpen?.lesson?.id
    }
  } catch (e) {
    toast.error('تعذر تحميل المحتوى')
    router.push('/courses')
  } finally {
    loading.value = false
  }
}

const jumpTo = ({ lesson }) => {
  currentLessonId.value = lesson.id
  router.replace({ name: 'CoursePlayer', params: { courseId, lessonId: lesson.id } })
}

const openPaywall = () => {
  paywallOpen.value = true
}

const startPay = async () => {
  paywallOpen.value = false
  if (!auth.isAuthenticated) {
    toast.error('سجّل الدخول أولاً')
    router.push('/login')
    return
  }
  try {
    const payload = { userId: auth.user.id, courseId }
    const res = await paymentService.createPaymentSession(payload)
    const url = res?.data?.checkoutUrl || res?.data || res // دعم كل الأشكال
    if (typeof url === 'string') window.location.href = url
    else toast.error('تعذر بدء الدفع')
  } catch (e) {
    toast.error(e.message || 'تعذر بدء الدفع')
  }
}

const goPrev = () => prevLesson.value && jumpTo({ lesson: prevLesson.value })
const goNext = () => nextLesson.value && jumpTo({ lesson: nextLesson.value })
const handleEnded = () => {
  // مستقبلاً: استدعاء progressService.markComplete(currentLessonId.value)
  // الآن: ننتقل تلقائياً للدرس التالي
  if (nextLesson.value) goNext()
  else toast.success('أحسنت! أنهيت هذا القسم 👏')
}

onMounted(fetchOutline)
watch(() => route.params.lessonId, (v) => {
  if (v) currentLessonId.value = Number(v)
})
</script>
