<template>
  <div class="w-full h-full flex items-center justify-center">
    <svg viewBox="0 0 42 42" class="w-40 h-40">
      <circle cx="21" cy="21" r="16" fill="none" stroke="#e5e7eb" stroke-width="8"/>
      <g v-for="(s,idx) in segs" :key="idx" :transform="'rotate(' + s.rot + ' 21 21)'">
        <circle
          cx="21" cy="21" r="16"
          fill="none"
          :stroke="s.color"
          stroke-width="8"
          :stroke-dasharray="s.dash + ' ' + s.gap"
          stroke-linecap="butt"
        />
      </g>
      <text x="21" y="22" text-anchor="middle" class="fill-slate-700 dark:fill-slate-200" font-size="6" font-weight="bold">
        {{ total }}
      </text>
    </svg>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  labels: { type: Array, default: () => [] },
  series: { type: Array, default: () => [] },
  colors: { type: Array, default: () => ['#6366f1','#f59e0b','#10b981','#8b5cf6','#ef4444','#14b8a6'] }
})

const total = computed(() => (props.series || []).reduce((a,b)=> a + (Number(b) || 0), 0) || 1)

const segs = computed(() => {
  let acc = 0
  const stroke = 100
  return (props.series || []).map((v, i) => {
    const val = Number(v) || 0
    const pct = val / total.value
    const dash = pct * stroke
    const gap = stroke - dash
    const rot = acc * 360
    acc += pct
    return { dash, gap, rot, color: props.colors[i % props.colors.length] }
  })
})
</script>
