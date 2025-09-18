<template>
  <apexchart
    type="donut"
    height="224"
    :options="options"
    :series="series"
  />
</template>

<script setup>
import { computed } from 'vue'
import VueApexCharts from 'vue3-apexcharts'

// استخدامه محليًا داخل المكوّن
const apexchart = VueApexCharts

const props = defineProps({
  labels: { type: Array, default: () => [] },   // ['نشط', 'غير نشط' ...]
  series: { type: Array, default: () => [] },   // [3, 10 ...]
  colors: { type: Array, default: () => ['#6366f1','#f59e0b','#10b981','#8b5cf6','#ef4444','#14b8a6'] }
})

const total = computed(() => props.series.reduce((a,b)=>a+b,0))

const options = computed(() => ({
  chart: {
    toolbar: { show: false },
    background: 'transparent'
  },
  labels: props.labels,
  colors: props.colors,
  stroke: { width: 2 },
  dataLabels: {
    enabled: true,
    style: { fontSize: '12px', fontWeight: 600 },
    dropShadow: { enabled: false },
    formatter: (val) => `${Math.round(val)}%`
  },
  legend: {
    show: false // نخلي الليجند أنت مسويه يدويًا تحت الرسم
  },
  plotOptions: {
    pie: {
      donut: {
        size: '72%',
        labels: {
          show: true,
          name: { show: true, fontSize: '12px', color: '#64748b', offsetY: 10, formatter: ()=>'الإجمالي' },
          value: { show: true, fontSize: '20px', fontWeight: 700, formatter: () => `${total.value}` },
          total: { show: false }
        }
      }
    }
  },
  tooltip: {
    theme: 'dark',
    y: { formatter: (v) => `${v}` }
  }
}))
</script>
