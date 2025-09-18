<template>
  <div v-if="open" class="fixed inset-0 z-50 flex items-center justify-center">
    <div class="absolute inset-0 bg-black/50" @click="$emit('close')"></div>
    <div class="relative bg-white dark:bg-slate-900 rounded-2xl shadow-xl w-full max-w-md p-6">
      <h3 class="text-lg font-bold text-slate-900 dark:text-white mb-2">الوصول مقفول</h3>
      <p class="text-slate-600 dark:text-slate-400 mb-4">
        هذا الدرس داخل وحدة مدفوعة. أول وحدتين مجانية للتجربة.
      </p>
      <div class="flex items-center justify-end gap-2">
        <button
          class="px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-800 transition"
          @click="$emit('close')"
        >
          إغلاق
        </button>
        <button
          class="px-4 py-2 rounded-lg bg-purple-600 hover:bg-purple-700 text-white font-semibold transition"
          @click="$emit('pay')"
        >
          ادفع الآن
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ open: { type: Boolean, default: false } })
defineEmits(['close', 'pay'])
</script>
