<template>
  <div class="flex items-center justify-between mt-4">
    <button
      class="px-4 py-2 rounded-lg border border-slate-300 dark:border-slate-600 text-slate-700 dark:text-slate-300 hover:bg-slate-50 dark:hover:bg-slate-700 transition disabled:opacity-50"
      :disabled="!prev"
      @click="$emit('goPrev')"
    >
      السابق
    </button>
    <button
      class="px-4 py-2 rounded-lg bg-purple-600 hover:bg-purple-700 text-white font-semibold transition disabled:opacity-50"
      :disabled="!next"
      @click="$emit('goNext')"
    >
      التالي
    </button>
  </div>
</template>

<script setup>
defineProps({
  prev: { type: Object, default: null },
  next: { type: Object, default: null }
})
defineEmits(['goPrev', 'goNext'])
</script>
