<template>
  <div class="certificate-container">
    <!-- Certificate Display -->
    <div v-if="certificate" class="certificate-card">
      <div class="certificate-header">
        <div class="certificate-badge">
          <Award class="w-8 h-8 text-yellow-600" />
        </div>
        
        <h2 class="text-2xl font-bold text-slate-900 dark:text-white mb-2">
          شهادة إتمام الكورس
        </h2>
        
        <p class="text-slate-600 dark:text-slate-400 mb-6">
          هذا يشهد أن الطالب قد أتم الكورس بنجاح
        </p>
      </div>

      <div class="certificate-body">
        <!-- Certificate Preview -->
        <div class="certificate-preview">
          <div class="certificate-content">
            <!-- Logo Placeholder -->
            <div class="certificate-logo">
              <div class="w-16 h-16 bg-gradient-to-r from-purple-600 to-blue-600 rounded-full flex items-center justify-center mx-auto mb-4">
                <GraduationCap class="w-8 h-8 text-white" />
              </div>
            </div>

            <!-- Certificate Text -->
            <div class="text-center space-y-4">
              <h3 class="text-lg font-bold text-slate-900 dark:text-white">
                شهادة إتمام
              </h3>
              
              <p class="text-slate-700 dark:text-slate-300">
                هذا يشهد أن
              </p>
              
              <div class="text-2xl font-bold text-purple-600 dark:text-purple-400 py-2 border-b-2 border-purple-200 dark:border-purple-800">
                {{ certificate.user?.name || 'اسم الطالب' }}
              </div>
              
              <p class="text-slate-700 dark:text-slate-300">
                قد أتم بنجاح الكورس
              </p>
              
              <div class="text-xl font-semibold text-slate-900 dark:text-white">
                {{ certificate.course?.title || 'اسم الكورس' }}
              </div>
              
              <div class="flex items-center justify-between text-sm text-slate-600 dark:text-slate-400 pt-6">
                <div>
                  <strong>التاريخ:</strong> 
                  {{ formatDate(certificate.completionDate) }}
                </div>
                
                <div>
                  <strong>النتيجة النهائية:</strong> 
                  {{ Math.round(certificate.finalScore || 0) }}%
                </div>
                
                <div>
                  <strong>نتيجة الاختبار:</strong> 
                  {{ Math.round(certificate.quizScore || 0) }}%
                </div>
              </div>
              
              <div class="text-xs text-slate-500 dark:text-slate-400 pt-4 border-t border-slate-200 dark:border-slate-700">
                رقم الشهادة: {{ certificate.certificateNumber }}
              </div>
            </div>
          </div>
        </div>

        <!-- Certificate Actions -->
        <div class="certificate-actions">
          <div class="flex items-center justify-center space-x-4 rtl:space-x-reverse">
            <button
              @click="downloadPDF"
              :disabled="downloading"
              class="flex items-center space-x-2 rtl:space-x-reverse px-6 py-3 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg transition disabled:opacity-50"
            >
              <Download class="w-5 h-5" />
              <span>{{ downloading ? 'جاري التحميل...' : 'تحميل PDF' }}</span>
            </button>
            
            <button
              @click="viewPDF"
              class="flex items-center space-x-2 rtl:space-x-reverse px-6 py-3 border border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 rounded-lg transition"
            >
              <Eye class="w-5 h-5" />
              <span>معاينة</span>
            </button>
            
            <button
              @click="shareCertificate"
              class="flex items-center space-x-2 rtl:space-x-reverse px-6 py-3 border border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 rounded-lg transition"
            >
              <Share2 class="w-5 h-5" />
              <span>مشاركة</span>
            </button>
          </div>
        </div>

        <!-- Certificate Info -->
        <div class="certificate-info">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div class="bg-slate-50 dark:bg-slate-800 rounded-lg p-3">
              <div class="font-semibold text-slate-900 dark:text-white">تاريخ الإصدار</div>
              <div class="text-slate-600 dark:text-slate-400">
                {{ formatDate(certificate.issuedAt) }}
              </div>
            </div>
            
            <div class="bg-slate-50 dark:bg-slate-800 rounded-lg p-3">
              <div class="font-semibold text-slate-900 dark:text-white">الحالة</div>
              <div class="flex items-center space-x-2 rtl:space-x-reverse">
                <div class="w-2 h-2 bg-emerald-500 rounded-full"></div>
                <span class="text-emerald-600 dark:text-emerald-400">صالحة</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-else-if="loading" class="certificate-loading">
      <div class="animate-pulse space-y-4">
        <div class="bg-slate-200 dark:bg-slate-700 h-8 w-48 rounded mx-auto"></div>
        <div class="bg-slate-200 dark:bg-slate-700 h-64 rounded"></div>
        <div class="flex space-x-4 rtl:space-x-reverse justify-center">
          <div class="bg-slate-200 dark:bg-slate-700 h-10 w-32 rounded"></div>
          <div class="bg-slate-200 dark:bg-slate-700 h-10 w-32 rounded"></div>
        </div>
      </div>
    </div>

    <!-- No Certificate -->
    <div v-else class="no-certificate">
      <div class="text-center py-12">
        <div class="w-16 h-16 bg-slate-100 dark:bg-slate-800 rounded-full flex items-center justify-center mx-auto mb-4">
          <Award class="w-8 h-8 text-slate-400" />
        </div>
        
        <h3 class="text-lg font-semibold text-slate-900 dark:text-white mb-2">
          لا توجد شهادة متاحة
        </h3>
        
        <p class="text-slate-600 dark:text-slate-400 mb-4">
          يجب إكمال الكورس والنجاح في الاختبار النهائي للحصول على الشهادة
        </p>
        
        <button
          @click="$emit('go-to-quiz')"
          class="px-6 py-3 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg transition"
        >
          الانتقال للاختبار
        </button>
      </div>
    </div>

    <!-- Verification Modal -->
    <div v-if="verificationModal" class="verification-modal-overlay" @click="closeVerificationModal">
      <div class="verification-modal" @click.stop>
        <div class="flex items-center justify-between mb-4">
          <h4 class="text-lg font-semibold text-slate-900 dark:text-white">
            التحقق من الشهادة
          </h4>
          <button @click="closeVerificationModal" class="text-slate-400 hover:text-slate-600">
            <X class="w-5 h-5" />
          </button>
        </div>
        
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
              رقم الشهادة
            </label>
            <input
              v-model="verificationNumber"
              type="text"
              placeholder="أدخل رقم الشهادة"
              class="w-full px-3 py-2 border border-slate-300 dark:border-slate-600 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-transparent bg-white dark:bg-slate-800"
            />
          </div>
          
          <button
            @click="verifyCertificate"
            :disabled="!verificationNumber || verifying"
            class="w-full px-4 py-2 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg transition disabled:opacity-50"
          >
            {{ verifying ? 'جاري التحقق...' : 'تحقق' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { Award, Download, Eye, Share2, X, GraduationCap } from 'lucide-vue-next'
import { useToast } from 'vue-toastification'

export default {
  name: 'Certificate',
  components: { Award, Download, Eye, Share2, X, GraduationCap },
  props: {
    userId: {
      type: Number,
      required: true
    },
    courseId: {
      type: Number,
      required: true
    }
  },
  emits: ['go-to-quiz'],
  setup(props, { emit }) {
    const toast = useToast()
    
    // State
    const certificate = ref(null)
    const loading = ref(true)
    const downloading = ref(false)
    const verificationModal = ref(false)
    const verificationNumber = ref('')
    const verifying = ref(false)
    
    // Methods
    const fetchCertificate = async () => {
      try {
        const response = await fetch(`/api/certificates/user/${props.userId}/course/${props.courseId}`)
        
        if (response.ok) {
          certificate.value = await response.json()
        } else if (response.status === 404) {
          certificate.value = null
        } else {
          throw new Error('Failed to fetch certificate')
        }
      } catch (error) {
        toast.error('فشل في تحميل الشهادة')
        console.error(error)
      } finally {
        loading.value = false
      }
    }
    
    const downloadPDF = async () => {
      if (!certificate.value) return
      
      try {
        downloading.value = true
        
        const response = await fetch(`/api/certificates/${certificate.value.id}/download`)
        
        if (response.ok) {
          const blob = await response.blob()
          const url = window.URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = `certificate-${certificate.value.certificateNumber}.pdf`
          document.body.appendChild(a)
          a.click()
          document.body.removeChild(a)
          window.URL.revokeObjectURL(url)
          
          toast.success('تم تحميل الشهادة بنجاح')
        } else {
          throw new Error('Download failed')
        }
      } catch (error) {
        toast.error('فشل في تحميل الشهادة')
        console.error(error)
      } finally {
        downloading.value = false
      }
    }
    
    const viewPDF = async () => {
      if (!certificate.value) return
      
      try {
        const response = await fetch(`/api/certificates/${certificate.value.id}/view`)
        
        if (response.ok) {
          const blob = await response.blob()
          const url = window.URL.createObjectURL(blob)
          window.open(url, '_blank')
        } else {
          throw new Error('View failed')
        }
      } catch (error) {
        toast.error('فشل في عرض الشهادة')
        console.error(error)
      }
    }
    
    const shareCertificate = async () => {
      if (!certificate.value) return
      
      const shareData = {
        title: 'شهادة إتمام الكورس',
        text: `حصلت على شهادة إتمام كورس "${certificate.value.course?.title}" بنجاح!`,
        url: window.location.href
      }
      
      try {
        if (navigator.share) {
          await navigator.share(shareData)
        } else {
          await navigator.clipboard.writeText(shareData.url)
          toast.success('تم نسخ الرابط')
        }
      } catch (error) {
        console.error('Sharing failed:', error)
      }
    }
    
    const verifyCertificate = async () => {
      if (!verificationNumber.value) return
      
      try {
        verifying.value = true
        
        const response = await fetch(`/api/certificates/verify/${verificationNumber.value}`)
        
        if (response.ok) {
          const result = await response.json()
          
          if (result.valid) {
            toast.success('الشهادة صالحة ومُتحقق منها')
          } else {
            toast.error('الشهادة غير صالحة أو غير موجودة')
          }
        } else {
          throw new Error('Verification failed')
        }
      } catch (error) {
        toast.error('فشل في التحقق من الشهادة')
        console.error(error)
      } finally {
        verifying.value = false
        closeVerificationModal()
      }
    }
    
    const openVerificationModal = () => {
      verificationModal.value = true
      verificationNumber.value = ''
    }
    
    const closeVerificationModal = () => {
      verificationModal.value = false
      verificationNumber.value = ''
      verifying.value = false
    }
    
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('ar-SA', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }
    
    // Lifecycle
    onMounted(() => {
      fetchCertificate()
    })
    
    return {
      certificate,
      loading,
      downloading,
      verificationModal,
      verificationNumber,
      verifying,
      downloadPDF,
      viewPDF,
      shareCertificate,
      verifyCertificate,
      openVerificationModal,
      closeVerificationModal,
      formatDate
    }
  }
}
</script>

<style scoped>
.certificate-container {
  max-width: 800px;
  margin: 0 auto;
}

.certificate-card {
  @apply bg-white dark:bg-slate-900 rounded-xl shadow-lg border border-slate-200 dark:border-slate-800 overflow-hidden;
}

.certificate-header {
  @apply text-center p-8 bg-gradient-to-r from-purple-50 to-blue-50 dark:from-purple-900/20 dark:to-blue-900/20;
}

.certificate-badge {
  @apply w-16 h-16 bg-gradient-to-r from-yellow-400 to-yellow-600 rounded-full flex items-center justify-center mx-auto mb-4;
}

.certificate-body {
  @apply p-8 space-y-8;
}

.certificate-preview {
  @apply border border-slate-200 dark:border-slate-700 rounded-lg p-8 bg-slate-50 dark:bg-slate-800/50;
}

.certificate-content {
  @apply bg-white dark:bg-slate-900 rounded-lg p-8 border-2 border-dashed border-slate-300 dark:border-slate-600;
}

.certificate-actions {
  @apply border-t border-slate-200 dark:border-slate-700 pt-6;
}

.certificate-info {
  @apply border-t border-slate-200 dark:border-slate-700 pt-6;
}

.certificate-loading {
  @apply bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800 p-8;
}

.no-certificate {
  @apply bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800;
}

.verification-modal-overlay {
  @apply fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50;
}

.verification-modal {
  @apply bg-white dark:bg-slate-900 rounded-xl p-6 max-w-md w-full mx-4 border border-slate-200 dark:border-slate-800;
}
</style>