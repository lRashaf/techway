<template>
  <div class="space-y-3">
    <div
      v-for="(m, mi) in (outline?.modules || [])"
      :key="m.id || mi"
      class="border border-slate-200 dark:border-slate-700 rounded-xl overflow-hidden"
    >
      <!-- Module Header -->
      <button
        class="w-full flex items-center justify-between px-4 py-3 bg-slate-50 dark:bg-slate-800/50"
        @click="toggle(mi)"
      >
        <div class="flex items-center gap-3">
          <span class="font-semibold text-slate-900 dark:text-white">{{ m.title || ('وحدة #' + (mi+1)) }}</span>
          <span
            v-if="m.isFree"
            class="text-xs px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300"
          >مجاني</span>
        </div>
        <svg :class="['w-4 h-4 transition-transform', openIndex === mi ? 'rotate-180' : '']" viewBox="0 0 20 20" fill="currentColor">
          <path fill-rule="evenodd" d="M5.23 7.21a.75.75 0 011.06.02L10 11.17l3.71-3.94a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z" clip-rule="evenodd" />
        </svg>
      </button>

      <!-- Lessons -->
      <div v-show="openIndex === mi" class="divide-y divide-slate-200 dark:divide-slate-700">
        <button
          v-for="(l, li) in (m.lessons || [])"
          :key="l.id || li"
          class="w-full flex items-center justify-between px-4 py-3 hover:bg-slate-50/70 dark:hover:bg-slate-800/40 transition"
          @click="onLessonClick(mi, m, l)"
        >
          <div class="flex items-center gap-3">
            <span class="inline-flex w-6 h-6 items-center justify-center rounded-md border text-xs"
                  :class="locked(mi, m, l)
                    ? 'border-slate-300 text-slate-500 dark:border-slate-600 dark:text-slate-400'
                    : 'border-emerald-300 text-emerald-600 dark:border-emerald-600 dark:text-emerald-400'">
              {{ locked(mi, m, l) ? '🔒' : '▶' }}
            </span>
            <span class="text-slate-800 dark:text-slate-100">{{ l.title || ('درس #' + (li+1)) }}</span>
          </div>
          <div class="text-xs text-slate-500 dark:text-slate-400">
            <span v-if="l.durationMinutes">{{ l.durationMinutes }} د</span>
          </div>
        </button>

        <div v-if="!m.lessons || m.lessons.length === 0" class="px-4 py-3 text-sm text-slate-500 dark:text-slate-400">
          لا يوجد دروس في هذه الوحدة
        </div>
      </div>
    </div>

    <div v-if="!outline || !(outline.modules?.length)" class="text-sm text-slate-500 dark:text-slate-400">
      لا يوجد محتوى بعد.
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  outline: { type: Object, default: () => ({ modules: [] }) },
  enrolled: { type: Boolean, default: false },
  isPaid: { type: Boolean, default: false },
  deliveryType: { type: String, default: 'ONLINE' }, // ONLINE | PHYSICAL | HYBRID
})

const emit = defineEmits(['play', 'locked', 'need-enroll'])

const openIndex = ref(0)

function toggle(i) {
  openIndex.value = openIndex.value === i ? -1 : i
}

function locked(mi, moduleObj, lessonObj) {
  if (props.deliveryType !== 'ONLINE') return false
  if (props.isPaid) return false
  if (!props.enrolled) return true

  const isModuleFree = !!moduleObj?.isFree
  const isPreview = !!lessonObj?.isPreview
  if (isModuleFree || isPreview) return false

  return true
}

function onLessonClick(mi, m, l) {
  if (!props.enrolled) { emit('need-enroll'); return }
  if (locked(mi, m, l)) { emit('locked'); return }
  emit('play', { lesson: l, module: m })
}
</script>
