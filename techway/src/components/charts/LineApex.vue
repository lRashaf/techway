<template>
  <apexchart
    type="area"
    height="224"
    :options="options"
    :series="seriesWrapped"
  />
</template>

<script setup>
import { computed } from 'vue'
import VueApexCharts from 'vue3-apexcharts'

// استخدامه محليًا داخل المكوّن
const apexchart = VueApexCharts

const props = defineProps({
  labels: { type: Array, default: () => [] },   // ['10/08', ...]
  series: { type: Array, default: () => [] },   // [0,0,...,3]
})

const maxY = computed(() => {
  const m = Math.max(0, ...props.series)
  // نخلي أقصى المحور مناسب ليبان الارتفاع حتى لو القيم قليلة
  if (m <= 3) return 5
  if (m <= 10) return 12
  return undefined
})

const seriesWrapped = computed(() => ([
  { name: 'التسجيلات', data: props.series }
]))

const options = computed(() => ({
  chart: {
    toolbar: { show: false },
    foreColor: '#64748b', // slate-500
    animations: { enabled: true, easing: 'easeinout', speed: 500 },
    background: 'transparent'
  },
  stroke: {
    curve: 'smooth',
    width: 3
  },
  fill: {
    type: 'gradient',
    gradient: {
      shadeIntensity: 0.3,
      opacityFrom: 0.35,
      opacityTo: 0.05,
      stops: [0, 90, 100]
    }
  },
  markers: {
    size: 4,
    strokeWidth: 2,
    hover: { size: 6 }
  },
  grid: {
    borderColor: '#e2e8f0', // slate-200
    strokeDashArray: 4
  },
  xaxis: {
    categories: props.labels,
    labels: { rotate: 0, style: { fontSize: '12px' } },
    tooltip: { enabled: false },
    axisBorder: { show: false },
    axisTicks: { show: false }
  },
  yaxis: {
    min: 0,
    max: maxY.value,       // يضبط تلقائيًا
    tickAmount: 4,
    labels: { formatter: v => Math.round(v) }
  },
  colors: ['#8b5cf6'],     // بنفسجي لطيف
  dataLabels: { enabled: false },
  tooltip: {
    theme: 'dark',
    y: { formatter: v => `${v}` },
    x: { formatter: (val) => `اليوم ${val}` }
  },
  legend: { show: false }
}))
</script>
