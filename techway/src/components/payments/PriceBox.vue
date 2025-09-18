<template>
  <div class="card p-6">
    <div class="flex items-center justify-between mb-4">
      <h3 class="font-semibold text-slate-900 dark:text-white">الخطة</h3>
      <span v-if="!course?.isFree"
            class="text-xs px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300">مدفوع</span>
      <span v-else
            class="text-xs px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300">مجاني</span>
    </div>

    <div class="mb-4">
      <div v-if="!course?.isFree" class="text-3xl font-bold text-slate-900 dark:text-white">
        {{ formatPrice(course?.price) }}
        <span class="text-sm font-normal text-slate-500 dark:text-slate-400">ر.س</span>
      </div>
      <div v-else class="text-3xl font-bold text-emerald-600 dark:text-emerald-400">مجاني</div>
      <p class="mt-1 text-sm text-slate-500 dark:text-slate-400">
        {{ subtext }}
      </p>
    </div>

    <button
      class="w-full px-4 py-3 rounded-lg bg-slate-900 text-white dark:bg-white dark:text-slate-900 hover:opacity-90 transition disabled:opacity-60"
      :disabled="loading"
      @click="onPrimary"
    >
      {{ primaryLabel }}
    </button>

    <div class="mt-4 grid grid-cols-2 gap-3">
      <button class="px-4 py-2 rounded-lg border hover:bg-slate-50 dark:hover:bg-slate-800 transition"
              @click="$emit('share')">مشاركة</button>
      <button class="px-4 py-2 rounded-lg border hover:bg-slate-50 dark:hover:bg-slate-800 transition"
              @click="$emit('favorite')">{{ isFavorited ? 'إزالة من المفضلة' : 'أضف للمفضلة' }}</button>
    </div>

    <ul class="mt-6 space-y-2 text-sm text-slate-600 dark:text-slate-400">
      <li>وصول مدى الحياة للمحتوى</li>
      <li>أول وحدتين مجانية للمعاينة</li>
      <li>شهادة إتمام (عند الإكمال)</li>
    </ul>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  course: { type: Object, required: true },
  enrolled: { type: Boolean, default: false },
  isPaid: { type: Boolean, default: false },
  isFavorited: { type: Boolean, default: false },
  loading: { type: Boolean, default: false }
})
const emit = defineEmits(['favorite', 'share', 'enroll', 'pay'])

const primaryLabel = computed(() => {
  if (!props.enrolled) return 'سجّل في الكورس'
  if (!props.isPaid && !props.course?.isFree && Number(props.course?.price) > 0) return 'ادفع لفتح الكل'
  return 'أكمل التعلّم'
})

const subtext = computed(() => {
  if (!props.enrolled) return 'سجّل أولاً لعرض المحتوى وبدء التعلم'
  if (!props.isPaid && !props.course?.isFree) return 'أكمل الدفع لفتح جميع الوحدات'
  return 'ابدأ أو أكمل التعلّم من حيث توقفت'
})

function onPrimary() {
  if (!props.enrolled) { emit('enroll'); return }
  if (!props.isPaid && !props.course?.isFree && Number(props.course?.price) > 0) { emit('pay'); return }
  emit('enroll') // سيبدأ التعلّم من handler نفسه
}

function formatPrice(v) {
  const n = Number(v)
  if (!Number.isFinite(n) || n <= 0) return '—'
  return n.toLocaleString('ar-SA')
}
</script>
