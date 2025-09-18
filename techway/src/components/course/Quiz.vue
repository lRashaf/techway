  <template>
    <div class="quiz-container">
      <!-- Quiz Header -->
      <div v-if="!quizStarted && !quizCompleted" class="quiz-intro card p-8 text-center">
        <div class="w-16 h-16 bg-purple-100 dark:bg-purple-900/30 rounded-full flex items-center justify-center mx-auto mb-4">
          <HelpCircle class="w-8 h-8 text-purple-600 dark:text-purple-400" />
        </div>
        
        <h2 class="text-2xl font-bold text-slate-900 dark:text-white mb-4">
          {{ quiz.title || 'اختبار الكورس النهائي' }}
        </h2>
        
        <p class="text-slate-600 dark:text-slate-400 mb-6 leading-relaxed">
          {{ quiz.description || 'اختبر معلوماتك في هذا الكورس واحصل على شهادة الإتمام' }}
        </p>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6 text-sm">
          <div class="bg-slate-50 dark:bg-slate-800 rounded-lg p-3">
            <div class="font-semibold text-slate-900 dark:text-white">عدد الأسئلة</div>
            <div class="text-purple-600 dark:text-purple-400">{{ quiz.questions?.length || 0 }} سؤال</div>
          </div>
          
          <div class="bg-slate-50 dark:bg-slate-800 rounded-lg p-3">
            <div class="font-semibold text-slate-900 dark:text-white">درجة النجاح</div>
            <div class="text-purple-600 dark:text-purple-400">{{ quiz.passingScore || 70 }}%</div>
          </div>
          
          <div class="bg-slate-50 dark:bg-slate-800 rounded-lg p-3">
            <div class="font-semibold text-slate-900 dark:text-white">المدة المتاحة</div>
            <div class="text-purple-600 dark:text-purple-400">
              {{ quiz.timeLimitMinutes ? `${quiz.timeLimitMinutes} دقيقة` : 'غير محدودة' }}
            </div>
          </div>
        </div>

        <div v-if="previousAttempts.length > 0" class="mb-6 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
          <h4 class="font-semibold text-blue-900 dark:text-blue-300 mb-2">المحاولات السابقة</h4>
          <div class="text-sm text-blue-700 dark:text-blue-400">
            أفضل نتيجة: {{ bestScore }}% 
            <span v-if="bestAttempt?.isPassed" class="text-emerald-600 dark:text-emerald-400 mr-2">
              ✓ ناجح
            </span>
          </div>
        </div>

        <button
          @click="startQuiz"
          :disabled="loading"
          class="px-8 py-3 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg transition disabled:opacity-50"
        >
          {{ loading ? 'جاري التحميل...' : 'ابدأ الاختبار' }}
        </button>
      </div>

      <!-- Quiz Questions -->
      <div v-else-if="quizStarted && !quizCompleted" class="quiz-active">
        <!-- Progress Bar -->
        <div class="mb-6">
          <div class="flex items-center justify-between text-sm mb-2">
            <span class="text-slate-600 dark:text-slate-400">
              السؤال {{ currentQuestionIndex + 1 }} من {{ quiz.questions.length }}
            </span>
            <span v-if="quiz.timeLimitMinutes" class="text-slate-600 dark:text-slate-400">
              الوقت المتبقي: {{ formatTime(timeRemaining) }}
            </span>
          </div>
          
          <div class="w-full bg-slate-200 dark:bg-slate-700 rounded-full h-2">
            <div 
              class="bg-gradient-to-r from-purple-500 to-blue-500 h-2 rounded-full transition-all duration-300"
              :style="{ width: `${((currentQuestionIndex + 1) / quiz.questions.length) * 100}%` }"
            ></div>
          </div>
        </div>

        <!-- Current Question -->
        <div class="card p-6 mb-6">
          <h3 class="text-lg font-semibold text-slate-900 dark:text-white mb-4">
            {{ currentQuestion.questionText }}
          </h3>

          <div class="space-y-3">
            <div 
              v-for="option in currentQuestion.options" 
              :key="option.id"
              class="option-item"
              :class="{ 'selected': selectedAnswers[currentQuestion.id] === option.id }"
              @click="selectOption(currentQuestion.id, option.id)"
            >
              <div class="flex items-center space-x-3 rtl:space-x-reverse">
                <div class="radio-circle"></div>
                <span class="flex-1 text-slate-700 dark:text-slate-300">
                  {{ option.optionText }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Navigation Buttons -->
        <div class="flex items-center justify-between">
          <button
            @click="previousQuestion"
            :disabled="currentQuestionIndex === 0"
            class="px-6 py-2 rounded-lg border border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition disabled:opacity-50"
          >
            السؤال السابق
          </button>

          <div class="flex items-center space-x-3 rtl:space-x-reverse">
            <button
              v-if="currentQuestionIndex < quiz.questions.length - 1"
              @click="nextQuestion"
              class="px-6 py-2 rounded-lg bg-purple-600 hover:bg-purple-700 text-white font-semibold transition"
            >
              السؤال التالي
            </button>

            <button
              v-else
              @click="submitQuiz"
              :disabled="submitting"
              class="px-6 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white font-semibold transition disabled:opacity-50"
            >
              {{ submitting ? 'جاري الإرسال...' : 'إنهاء الاختبار' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Quiz Results -->
      <div v-else-if="quizCompleted" class="quiz-results card p-8 text-center">
        <div class="w-20 h-20 mx-auto mb-6 rounded-full flex items-center justify-center"
            :class="quizResult.passed ? 'bg-emerald-100 dark:bg-emerald-900/30' : 'bg-red-100 dark:bg-red-900/30'">
          <component :is="quizResult.passed ? CheckCircle : XCircle" 
                    :class="['w-10 h-10', quizResult.passed ? 'text-emerald-600 dark:text-emerald-400' : 'text-red-600 dark:text-red-400']" />
        </div>

        <h2 class="text-2xl font-bold mb-4"
            :class="quizResult.passed ? 'text-emerald-900 dark:text-emerald-100' : 'text-red-900 dark:text-red-100'">
          {{ quizResult.passed ? 'أحسنت! نجحت في الاختبار' : 'للأسف لم تنجح هذه المرة' }}
        </h2>

        <div class="text-3xl font-bold mb-6"
            :class="quizResult.passed ? 'text-emerald-600 dark:text-emerald-400' : 'text-red-600 dark:text-red-400'">
          {{ Math.round(quizResult.score) }}%
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6 text-sm">
          <div class="bg-slate-50 dark:bg-slate-800 rounded-lg p-3">
            <div class="font-semibold text-slate-900 dark:text-white">إجاباتك الصحيحة</div>
            <div class="text-blue-600 dark:text-blue-400">
              {{ quizResult.correctAnswers }} من {{ quizResult.totalQuestions }}
            </div>
          </div>
          
          <div class="bg-slate-50 dark:bg-slate-800 rounded-lg p-3">
            <div class="font-semibold text-slate-900 dark:text-white">الوقت المستغرق</div>
            <div class="text-blue-600 dark:text-blue-400">{{ quizResult.timeTaken || 'غير محدد' }} دقيقة</div>
          </div>
          
          <div class="bg-slate-50 dark:bg-slate-800 rounded-lg p-3">
            <div class="font-semibold text-slate-900 dark:text-white">رقم المحاولة</div>
            <div class="text-blue-600 dark:text-blue-400"># {{ quizResult.attemptNumber }}</div>
          </div>
        </div>

        <!-- Success Actions -->
        <div v-if="quizResult.passed" class="space-y-4">
          <div class="p-4 bg-emerald-50 dark:bg-emerald-900/20 rounded-lg">
            <p class="text-emerald-800 dark:text-emerald-200 font-semibold mb-2">
              🎉 تهانينا! أكملت الكورس بنجاح
            </p>
            <p class="text-emerald-700 dark:text-emerald-300 text-sm">
              تم إنشاء شهادة الإتمام تلقائياً ويمكنك تحميلها الآن
            </p>
          </div>

          <div class="flex items-center justify-center space-x-4 rtl:space-x-reverse">
            <button
              @click="downloadCertificate"
              class="px-6 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg transition"
            >
              تحميل الشهادة
            </button>
            
            <button
              @click="$emit('quiz-completed', quizResult)"
              class="px-6 py-3 border border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 rounded-lg transition"
            >
              العودة للكورس
            </button>
          </div>
        </div>

        <!-- Retry Actions -->
        <div v-else class="space-y-4">
          <div class="p-4 bg-orange-50 dark:bg-orange-900/20 rounded-lg">
            <p class="text-orange-800 dark:text-orange-200 font-semibold mb-2">
              تحتاج {{ quiz.passingScore }}% للنجاح
            </p>
            <p class="text-orange-700 dark:text-orange-300 text-sm">
              راجع المحتوى وحاول مرة أخرى
            </p>
          </div>

          <div class="flex items-center justify-center space-x-4 rtl:space-x-reverse">
            <button
              @click="retryQuiz"
              class="px-6 py-3 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg transition"
            >
              إعادة المحاولة
            </button>
            
            <button
              @click="$emit('quiz-failed', quizResult)"
              class="px-6 py-3 border border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 rounded-lg transition"
            >
              مراجعة الكورس
            </button>
          </div>
        </div>
      </div>
    </div>
  </template>

  <script>
  import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
  import { HelpCircle, CheckCircle, XCircle, Clock } from 'lucide-vue-next'
  import { useToast } from 'vue-toastification'

  export default {
    name: 'Quiz',
    components: { HelpCircle, CheckCircle, XCircle, Clock },
    props: {
      courseId: {
        type: Number,
        required: true
      },
      userId: {
        type: Number,
        required: true
      }
    },
    emits: ['quiz-completed', 'quiz-failed'],
    setup(props, { emit }) {
      const toast = useToast()
      
      // State
      const quiz = ref({})
      const loading = ref(true)
      const submitting = ref(false)
      const quizStarted = ref(false)
      const quizCompleted = ref(false)
      
      // Quiz data
      const currentAttempt = ref(null)
      const currentQuestionIndex = ref(0)
      const selectedAnswers = ref({})
      const previousAttempts = ref([])
      const bestAttempt = ref(null)
      
      // Timer
      const startTime = ref(null)
      const timeRemaining = ref(0)
      const timer = ref(null)
      
      // Results
      const quizResult = ref({})
      
      // Computed
      const currentQuestion = computed(() => {
        if (!quiz.value.questions || quiz.value.questions.length === 0) return null
        return quiz.value.questions[currentQuestionIndex.value]
      })
      
      const bestScore = computed(() => {
        if (!bestAttempt.value) return 0
        return Math.round(bestAttempt.value.scorePercentage)
      })
      
      // Methods
      const fetchQuiz = async () => {
        try {
          const response = await fetch(`/api/quiz/course/${props.courseId}`)
          if (response.ok) {
            quiz.value = await response.json()
          } else {
            throw new Error('Quiz not found')
          }
        } catch (error) {
          toast.error('فشل في تحميل الاختبار')
          console.error(error)
        } finally {
          loading.value = false
        }
      }
      
      const fetchPreviousAttempts = async () => {
        try {
          const response = await fetch(`/api/quiz/${quiz.value.id}/attempts?userId=${props.userId}`)
          if (response.ok) {
            previousAttempts.value = await response.json()
            
            // Get best attempt
            const bestResponse = await fetch(`/api/quiz/${quiz.value.id}/best-attempt?userId=${props.userId}`)
            if (bestResponse.ok) {
              bestAttempt.value = await bestResponse.json()
            }
          }
        } catch (error) {
          console.error('Failed to fetch attempts:', error)
        }
      }
      
      const startQuiz = async () => {
          // حواجز قبل الإرسال
    if (!props.userId) {
      toast.error('سجّل دخولك أولاً');
      return;
    }
    if (!quiz.value?.id) {
      toast.error('لا يوجد اختبار لهذا الكورس حالياً');
      return;
    }
    if (!quiz.value?.questions || quiz.value.questions.length === 0) {
      toast.error('لا توجد أسئلة مضافة للاختبار بعد');
      return;
    }
        try {
          loading.value = true
          
          const response = await fetch(`/api/quiz/${quiz.value.id}/start?userId=${props.userId}`, {
            method: 'POST'
          })
          
          if (response.ok) {
            currentAttempt.value = await response.json()
            quizStarted.value = true
            currentQuestionIndex.value = 0
            selectedAnswers.value = {}
            
            // Start timer
            if (quiz.value.timeLimitMinutes) {
              startTime.value = new Date()
              timeRemaining.value = quiz.value.timeLimitMinutes * 60
              startTimer()
            }
          } else {
            const error = await response.json()
            toast.error(error.error || 'فشل في بدء الاختبار')
          }
        } catch (error) {
          toast.error('فشل في بدء الاختبار')
          console.error(error)
        } finally {
          loading.value = false
        }
      }
      
      const startTimer = () => {
        timer.value = setInterval(() => {
          timeRemaining.value--
          if (timeRemaining.value <= 0) {
            submitQuiz() // Auto-submit when time runs out
          }
        }, 1000)
      }
      
      const selectOption = (questionId, optionId) => {
        selectedAnswers.value[questionId] = optionId
      }
      
      const nextQuestion = () => {
        if (currentQuestionIndex.value < quiz.value.questions.length - 1) {
          currentQuestionIndex.value++
        }
      }
      
      const previousQuestion = () => {
        if (currentQuestionIndex.value > 0) {
          currentQuestionIndex.value--
        }
      }
      
      const submitQuiz = async () => {
        try {
          submitting.value = true
          
          // Submit all answers
          for (const [questionId, optionId] of Object.entries(selectedAnswers.value)) {
            await fetch(`/api/quiz/attempt/${currentAttempt.value.id}/answer`, {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json'
              },
              body: JSON.stringify({
                questionId: parseInt(questionId),
                selectedOptionId: parseInt(optionId)
              })
            })
          }
          
          // Complete the quiz attempt
          const response = await fetch(`/api/quiz/attempt/${currentAttempt.value.id}/complete`, {
            method: 'POST'
          })
          
          if (response.ok) {
            const result = await response.json()
            quizResult.value = {
              passed: result.passed,
              score: result.score,
              correctAnswers: result.attempt.correctAnswers,
              totalQuestions: result.attempt.totalQuestions,
              attemptNumber: result.attempt.attemptNumber,
              timeTaken: result.attempt.timeTakenMinutes
            }
            
            quizStarted.value = false
            quizCompleted.value = true
            
            if (timer.value) {
              clearInterval(timer.value)
            }
            
            if (result.passed) {
              toast.success('أحسنت! نجحت في الاختبار وحصلت على الشهادة')
              emit('quiz-completed', quizResult.value)
            } else {
              toast.warning('لم تحصل على الدرجة المطلوبة، حاول مرة أخرى')
              emit('quiz-failed', quizResult.value)
            }
          } else {
            const error = await response.json()
            toast.error(error.error || 'فشل في إرسال الاختبار')
          }
          
        } catch (error) {
          toast.error('فشل في إرسال الاختبار')
          console.error(error)
        } finally {
          submitting.value = false
        }
      }
      
      const retryQuiz = () => {
        quizCompleted.value = false
        quizStarted.value = false
        currentQuestionIndex.value = 0
        selectedAnswers.value = {}
        quizResult.value = {}
      }
      
      const downloadCertificate = async () => {
        try {
          // Find user's certificate for this course
          const response = await fetch(`/api/certificates/user/${props.userId}/course/${props.courseId}`)
          
          if (response.ok) {
            const certificate = await response.json()
            
            // Download PDF
            const pdfResponse = await fetch(`/api/certificates/${certificate.id}/download`)
            if (pdfResponse.ok) {
              const blob = await pdfResponse.blob()
              const url = window.URL.createObjectURL(blob)
              const a = document.createElement('a')
              a.href = url
              a.download = `certificate-${certificate.certificateNumber}.pdf`
              document.body.appendChild(a)
              a.click()
              document.body.removeChild(a)
              window.URL.revokeObjectURL(url)
              
              toast.success('تم تحميل الشهادة بنجاح')
            }
          } else {
            toast.error('لم يتم العثور على الشهادة')
          }
        } catch (error) {
          toast.error('فشل في تحميل الشهادة')
          console.error(error)
        }
      }
      
      const formatTime = (seconds) => {
        const minutes = Math.floor(seconds / 60)
        const remainingSeconds = seconds % 60
        return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
      }
      
      // Lifecycle
      onMounted(async () => {
        await fetchQuiz()
        if (quiz.value.id) {
          await fetchPreviousAttempts()
        }
      })
      
      onBeforeUnmount(() => {
        if (timer.value) {
          clearInterval(timer.value)
        }
      })
      
      return {
        quiz,
        loading,
        submitting,
        quizStarted,
        quizCompleted,
        currentQuestionIndex,
        currentQuestion,
        selectedAnswers,
        previousAttempts,
        bestAttempt,
        bestScore,
        timeRemaining,
        quizResult,
        startQuiz,
        selectOption,
        nextQuestion,
        previousQuestion,
        submitQuiz,
        retryQuiz,
        downloadCertificate,
        formatTime
      }
    }
  }
  </script>

  <style scoped>
  .quiz-container {
    max-width: 800px;
    margin: 0 auto;
  }

  .option-item {
    @apply p-4 rounded-lg border border-slate-200 dark:border-slate-700 cursor-pointer hover:bg-slate-50 dark:hover:bg-slate-800 transition-all;
  }

  .option-item.selected {
    @apply border-purple-500 bg-purple-50 dark:bg-purple-900/20;
  }

  .radio-circle {
    @apply w-4 h-4 rounded-full border-2 border-slate-300 dark:border-slate-600 relative;
  }

  .option-item.selected .radio-circle {
    @apply border-purple-500 bg-purple-500;
  }

  .option-item.selected .radio-circle::after {
    content: '';
    @apply absolute inset-1 rounded-full bg-white;
  }

  .card {
    @apply bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800;
  }
  </style>