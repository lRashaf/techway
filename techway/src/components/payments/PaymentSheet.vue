<template>
  <div v-if="open" class="fixed inset-0 z-50 flex items-end sm:items-center justify-center">
    <div class="absolute inset-0 bg-black/50" @click="$emit('close')"></div>

    <div class="relative w-full sm:max-w-md bg-white dark:bg-slate-900 rounded-t-2xl sm:rounded-2xl shadow-xl p-6">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-semibold text-slate-900 dark:text-white">خيارات الدفع</h3>
        <button class="text-slate-500 hover:text-slate-700 dark:hover:text-slate-300" @click="$emit('close')">✕</button>
      </div>

      <div class="space-y-3">
        <button class="w-full px-4 py-3 rounded-xl border hover:bg-slate-50 dark:hover:bg-slate-800 transition"
                :disabled="loading" @click="pay('card')">
          بطاقة (Visa / Master / Mada)
        </button>
        <button class="w-full px-4 py-3 rounded-xl border hover:bg-slate-50 dark:hover:bg-slate-800 transition disabled:opacity-50"
                :disabled="loading || !applePayAvailable" @click="pay('applepay')">
          Apple Pay <span v-if="!applePayAvailable" class="text-xs text-slate-400">(غير مدعوم هنا)</span>
        </button>
        <button class="w-full px-4 py-3 rounded-xl border hover:bg-slate-50 dark:hover:bg-slate-800 transition"
                :disabled="loading" @click="pay('stcpay')">
          STC Pay
        </button>
      </div>

      <p v-if="price != null" class="mt-4 text-sm text-slate-600 dark:text-slate-400">
        إجمالي: <strong class="text-slate-900 dark:text-white">{{ price }} ر.س</strong>
      </p>

      <div v-if="error" class="mt-3 text-sm text-red-600">{{ error }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { paymentService } from '@/services/api'

const props = defineProps({
  open: { type: Boolean, default: false },
  courseId: { type: Number, required: true },
  price: { type: Number, default: null },
})

const emit = defineEmits(['close', 'success'])

const auth = useAuthStore()
const loading = ref(false)
const error = ref('')

const applePayAvailable = computed(() => !!window.ApplePaySession) // في التطوير غالبًا false

async function pay(method) {
  error.value = ''
  if (!auth?.isAuthenticated) {
    error.value = 'سجّل الدخول أولاً'
    return
  }
  try {
    loading.value = true
    // حالياً الباك ما يستقبل method؛ نستخدم نفس endpoint. لاحقاً نمرر method.
    await paymentService.redirectToCheckout({ userId: auth.user.id, courseId: props.courseId })
    // redirect سيحدث؛ لن نكمل.
  } catch (e) {
    error.value = e?.message || 'تعذر بدء الدفع'
  } finally {
    loading.value = false
  }
}
</script>
