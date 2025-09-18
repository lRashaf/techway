<template>
  <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center p-4">
    <div class="absolute inset-0 bg-black/70 backdrop-blur-sm" @click="$emit('close')"></div>
    
    <div class="relative bg-white dark:bg-slate-900 rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden">
      <!-- Header -->
      <div class="relative px-6 py-6 bg-gradient-to-r from-purple-600 to-blue-600 text-white">
        <button 
          @click="$emit('close')"
          class="absolute top-4 right-4 text-white/80 hover:text-white transition"
        >
          <X class="w-6 h-6" />
        </button>
        
        <div class="pr-8">
          <h3 class="text-xl font-bold mb-1">إكمال الدفع</h3>
          <p class="text-purple-100 text-sm">افتح جميع وحدات الكورس واحصل على شهادتك</p>
        </div>
      </div>

      <!-- Course Summary -->
      <div class="px-6 py-4 bg-slate-50 dark:bg-slate-800/50 border-b border-slate-200 dark:border-slate-700">
        <div class="flex items-center gap-4">
          <div class="w-16 h-16 bg-gradient-to-br from-purple-100 to-blue-100 dark:from-purple-900/30 dark:to-blue-900/30 rounded-xl flex items-center justify-center">
            <BookOpen class="w-8 h-8 text-purple-600 dark:text-purple-400" />
          </div>
          <div class="flex-1">
            <h4 class="font-semibold text-slate-900 dark:text-white">{{ courseTitle || 'الكورس المحدد' }}</h4>
            <div class="flex items-center gap-4 mt-1 text-sm text-slate-600 dark:text-slate-400">
              <span>وصول مدى الحياة</span>
              <span>•</span>
              <span>شهادة إتمام</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Pricing -->
      <div class="px-6 py-4 border-b border-slate-200 dark:border-slate-700">
        <div class="flex items-center justify-between">
          <span class="text-slate-600 dark:text-slate-400">سعر الكورس</span>
          <div class="text-right">
            <span class="text-2xl font-bold text-slate-900 dark:text-white">
              {{ formatPrice(price) }}
            </span>
            <span class="text-sm text-slate-500 dark:text-slate-400 mr-1">ر.س</span>
          </div>
        </div>
        
        <!-- Features -->
        <div class="mt-4 space-y-2">
          <div v-for="feature in features" :key="feature" class="flex items-center gap-2 text-sm text-slate-600 dark:text-slate-400">
            <Check class="w-4 h-4 text-green-500" />
            <span>{{ feature }}</span>
          </div>
        </div>
      </div>

      <!-- Payment Methods -->
      <div class="px-6 py-6">
        <h4 class="font-semibold text-slate-900 dark:text-white mb-4">اختر طريقة الدفع</h4>
        
        <div class="space-y-3">
          <!-- Credit Card -->
          <button
            @click="pay('card')"
            :disabled="loading"
            class="w-full group relative overflow-hidden rounded-xl border-2 border-slate-200 dark:border-slate-700 hover:border-purple-300 dark:hover:border-purple-600 p-4 transition-all hover:shadow-lg disabled:opacity-50"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-600 rounded-xl flex items-center justify-center">
                  <CreditCard class="w-6 h-6 text-white" />
                </div>
                <div class="text-right">
                  <div class="font-semibold text-slate-900 dark:text-white">البطاقة البنكية</div>
                  <div class="text-sm text-slate-500 dark:text-slate-400">Visa • Mastercard • mada</div>
                </div>
              </div>
              <div class="flex items-center gap-2">
                <div class="w-8 h-5 bg-blue-600 rounded-sm flex items-center justify-center">
                  <span class="text-[8px] text-white font-bold">VISA</span>
                </div>
                <div class="w-8 h-5 bg-red-500 rounded-sm flex items-center justify-center">
                  <span class="text-[8px] text-white font-bold">MC</span>
                </div>
                <div class="w-8 h-5 bg-green-600 rounded-sm flex items-center justify-center">
                  <span class="text-[8px] text-white font-bold">mada</span>
                </div>
              </div>
            </div>
          </button>

          <!-- Apple Pay -->
          <button
            @click="pay('applepay')"
            :disabled="loading || !applePayAvailable"
            class="w-full group relative overflow-hidden rounded-xl border-2 border-slate-200 dark:border-slate-700 hover:border-purple-300 dark:hover:border-purple-600 p-4 transition-all hover:shadow-lg disabled:opacity-50"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-black rounded-xl flex items-center justify-center">
                  <Apple class="w-6 h-6 text-white" />
                </div>
                <div class="text-right">
                  <div class="font-semibold text-slate-900 dark:text-white">Apple Pay</div>
                  <div class="text-sm text-slate-500 dark:text-slate-400">
                    {{ applePayAvailable ? 'دفع سريع وآمن' : 'غير متاح على هذا الجهاز' }}
                  </div>
                </div>
              </div>
              <ChevronLeft class="w-5 h-5 text-slate-400 group-hover:text-slate-600 dark:group-hover:text-slate-300" />
            </div>
          </button>

          <!-- STC Pay -->
          <button
            @click="pay('stcpay')"
            :disabled="loading"
            class="w-full group relative overflow-hidden rounded-xl border-2 border-slate-200 dark:border-slate-700 hover:border-purple-300 dark:hover:border-purple-600 p-4 transition-all hover:shadow-lg disabled:opacity-50"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-gradient-to-br from-green-500 to-emerald-600 rounded-xl flex items-center justify-center">
                  <Smartphone class="w-6 h-6 text-white" />
                </div>
                <div class="text-right">
                  <div class="font-semibold text-slate-900 dark:text-white">STC Pay</div>
                  <div class="text-sm text-slate-500 dark:text-slate-400">المحفظة الرقمية السعودية</div>
                </div>
              </div>
              <ChevronLeft class="w-5 h-5 text-slate-400 group-hover:text-slate-600 dark:group-hover:text-slate-300" />
            </div>
          </button>

          <!-- Bank Transfer -->
          <button
            @click="pay('banktransfer')"
            :disabled="loading"
            class="w-full group relative overflow-hidden rounded-xl border-2 border-slate-200 dark:border-slate-700 hover:border-purple-300 dark:hover:border-purple-600 p-4 transition-all hover:shadow-lg disabled:opacity-50"
          >
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-4">
                <div class="w-12 h-12 bg-gradient-to-br from-indigo-500 to-blue-600 rounded-xl flex items-center justify-center">
                  <Building2 class="w-6 h-6 text-white" />
                </div>
                <div class="text-right">
                  <div class="font-semibold text-slate-900 dark:text-white">تحويل بنكي</div>
                  <div class="text-sm text-slate-500 dark:text-slate-400">جميع البنوك السعودية</div>
                </div>
              </div>
              <ChevronLeft class="w-5 h-5 text-slate-400 group-hover:text-slate-600 dark:group-hover:text-slate-300" />
            </div>
          </button>
        </div>

        <!-- Error Message -->
        <div v-if="error" class="mt-4 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
          <div class="flex items-center gap-3">
            <AlertCircle class="w-5 h-5 text-red-600 dark:text-red-400 flex-shrink-0" />
            <span class="text-sm text-red-700 dark:text-red-300">{{ error }}</span>
          </div>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="mt-4 flex items-center justify-center py-4">
          <div class="flex items-center gap-3 text-purple-600 dark:text-purple-400">
            <div class="w-5 h-5 border-2 border-current border-t-transparent rounded-full animate-spin"></div>
            <span class="text-sm">جاري التحويل لبوابة الدفع...</span>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="px-6 py-4 bg-slate-50 dark:bg-slate-800/50 border-t border-slate-200 dark:border-slate-700">
        <div class="flex items-center justify-center gap-2 text-xs text-slate-500 dark:text-slate-400">
          <Shield class="w-4 h-4" />
          <span>معاملاتك محمية بتشفير SSL 256-bit</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { paymentService } from '@/services/api'
import { 
  X, CreditCard, Apple, Smartphone, ChevronLeft, 
  Shield, AlertCircle, Check, BookOpen, Building2 
} from 'lucide-vue-next'

const props = defineProps({
  open: { type: Boolean, default: false },
  courseId: { type: Number, required: true },
  price: { type: Number, default: null },
  courseTitle: { type: String, default: '' }
})

const emit = defineEmits(['close', 'success'])

const authStore = useAuthStore()
const loading = ref(false)
const error = ref('')

const features = [
  'وصول مدى الحياة للمحتوى',
  'جميع الوحدات والدروس',
  'شهادة إتمام رسمية',
  'دعم فني متاح',
  'تحديثات مجانية'
]

const applePayAvailable = computed(() => {
  return window.ApplePaySession && ApplePaySession.canMakePayments()
})

const formatPrice = (price) => {
  const num = Number(price)
  if (!Number.isFinite(num) || num <= 0) return '0'
  return num.toLocaleString('ar-SA')
}

const pay = async (method) => {
  if (!authStore.isAuthenticated) {
    error.value = 'سجّل الدخول أولاً'
    return
  }

  error.value = ''
  loading.value = true

  try {
    await paymentService.redirectToCheckout({ 
      userId: authStore.user.id, 
      courseId: props.courseId,
      method 
    })
  } catch (e) {
    error.value = e.message || 'تعذر بدء عملية الدفع'
    loading.value = false
  }
}

const resetState = () => {
  error.value = ''
  loading.value = false
}

watch(() => props.open, (newVal) => {
  if (newVal) {
    resetState()
  }
})
</script>