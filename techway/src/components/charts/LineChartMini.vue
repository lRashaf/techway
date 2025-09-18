<template>
  <svg :viewBox="'0 0 100 40'" class="w-full h-full">
    <polyline
      :points="points"
      fill="none"
      stroke="currentColor"
      stroke-width="2"
      class="text-purple-500"
    />
    <line x1="0" y1="38" x2="100" y2="38"
          stroke="currentColor" stroke-width="0.5"
          class="text-slate-300 dark:text-slate-600"/>
  </svg>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  labels: { type: Array, default: () => [] },
  series: { type: Array, default: () => [] }
})

const points = computed(() => {
  const arr = (props.series || []).map(v => Number(v) || 0)
  if (!arr.length) return ''
  const max = Math.max(...arr, 1)
  const step = 100 / Math.max(1, arr.length - 1)
  return arr.map((v, i) => `${i * step},${40 - (v / max) * 36 - 2}`).join(' ')
})
</script>
