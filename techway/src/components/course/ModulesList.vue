<template>
  <div class="space-y-2">
    <div
      v-for="(module, moduleIndex) in modules"
      :key="module.id || moduleIndex"
      class="border border-slate-200 dark:border-slate-700 rounded-lg overflow-hidden"
    >
      <!-- Module Header -->
      <div
        class="flex items-center justify-between px-4 py-3 bg-slate-50 dark:bg-slate-800/50 cursor-pointer hover:bg-slate-100 dark:hover:bg-slate-800"
        @click="toggleModule(moduleIndex)"
      >
        <div class="flex items-center gap-3">
          <div :class="['text-xs px-2 py-1 rounded-full', module.isFree || isPaid 
            ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300' 
            : 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300']">
            {{ (module.isFree || isPaid) ? 'مفتوح' : 'مقفل' }}
          </div>
          <h3 class="font-medium text-slate-900 dark:text-white">
            {{ module.title || `الوحدة ${moduleIndex + 1}` }}
          </h3>
          
          <!-- Module Progress Indicator - جديد -->
          <div v-if="enrolled && getModuleProgress(module.id) > 0" class="flex items-center gap-2">
            <div class="w-16 bg-slate-200 dark:bg-slate-700 rounded-full h-1.5">
              <div 
                class="bg-purple-500 h-1.5 rounded-full transition-all duration-300"
                :style="{ width: `${getModuleProgress(module.id)}%` }"
              ></div>
            </div>
            <span class="text-xs text-slate-500 dark:text-slate-400">
              {{ getModuleProgress(module.id) }}%
            </span>
          </div>
        </div>
        
        <div class="flex items-center gap-2">
          <span class="text-xs text-slate-500 dark:text-slate-400">
            {{ module.lessons?.length || 0 }} درس
          </span>
          <!-- عدد الدروس المكتملة - جديد -->
          <span v-if="enrolled && getCompletedLessonsInModule(module).length > 0" 
                class="text-xs text-emerald-600 dark:text-emerald-400">
            ({{ getCompletedLessonsInModule(module).length }} مكتمل)
          </span>
          <ChevronDown 
            :class="['w-4 h-4 text-slate-400 transition-transform', 
                     expandedModules.has(moduleIndex) ? 'rotate-180' : '']" 
          />
        </div>
      </div>

      <!-- Module Lessons -->
      <div 
        v-show="expandedModules.has(moduleIndex)"
        class="divide-y divide-slate-100 dark:divide-slate-700"
      >
        <div
          v-for="(lesson, lessonIndex) in module.lessons || []"
          :key="lesson.id || lessonIndex"
          :class="[
            'flex items-center justify-between px-4 py-3 hover:bg-slate-50 dark:hover:bg-slate-800/50 cursor-pointer transition-colors',
            lesson.id === currentLessonId ? 'bg-purple-50 dark:bg-purple-900/20 border-r-2 border-purple-500' : ''
          ]"
          @click="handleLessonClick(lesson, module, moduleIndex)"
        >
          <div class="flex items-center gap-3">
            <!-- Lesson Status Icon -->
            <div :class="[
              'w-6 h-6 rounded-full flex items-center justify-center text-xs font-medium',
              isLessonLocked(lesson, module) 
                ? 'bg-slate-200 text-slate-500 dark:bg-slate-700 dark:text-slate-400'
                : isLessonCompleted(lesson.id)
                  ? 'bg-emerald-100 text-emerald-600 dark:bg-emerald-900/50 dark:text-emerald-400'
                  : lesson.id === currentLessonId
                    ? 'bg-purple-100 text-purple-600 dark:bg-purple-900/50 dark:text-purple-400'
                    : 'bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-400'
            ]">
              <span v-if="isLessonLocked(lesson, module)">🔒</span>
              <span v-else-if="isLessonCompleted(lesson.id)">✓</span>
              <span v-else-if="lesson.id === currentLessonId">▶</span>
              <span v-else>{{ lessonIndex + 1 }}</span>
            </div>
            
            <!-- Lesson Title and Info -->
            <div class="flex-1">
              <h4 :class="[
                'text-sm font-medium',
                lesson.id === currentLessonId 
                  ? 'text-purple-700 dark:text-purple-300'
                  : isLessonLocked(lesson, module)
                    ? 'text-slate-500 dark:text-slate-400'
                    : isLessonCompleted(lesson.id)
                      ? 'text-emerald-700 dark:text-emerald-300'
                      : 'text-slate-700 dark:text-slate-300'
              ]">
                {{ lesson.title || `الدرس ${lessonIndex + 1}` }}
              </h4>
              
              <div class="flex items-center gap-2 mt-1">
                <span class="text-xs text-slate-500 dark:text-slate-400">
                  {{ lesson.contentType || 'VIDEO' }}
                </span>
                <span v-if="lesson.durationMinutes" class="text-xs text-slate-500 dark:text-slate-400">
                  {{ lesson.durationMinutes }} دقيقة
                </span>
                <span v-if="lesson.isPreview" class="text-xs px-1.5 py-0.5 bg-blue-100 text-blue-700 rounded dark:bg-blue-900/30 dark:text-blue-300">
                  معاينة
                </span>
                
                <!-- Lesson Progress Bar - جديد -->
                <div v-if="enrolled && !isLessonLocked(lesson, module) && getLessonProgress(lesson.id) > 0" 
                     class="flex items-center gap-1">
                  <div class="w-12 bg-slate-200 dark:bg-slate-700 rounded-full h-1">
                    <div 
                      class="bg-purple-500 h-1 rounded-full transition-all duration-300"
                      :style="{ width: `${getLessonProgress(lesson.id)}%` }"
                    ></div>
                  </div>
                  <span class="text-xs text-slate-500 dark:text-slate-400">
                    {{ getLessonProgress(lesson.id) }}%
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Action Button -->
          <div class="flex items-center">
            <button
              v-if="isLessonLocked(lesson, module)"
              @click.stop="$emit('locked-lesson', { lesson, module })"
              class="text-xs px-2 py-1 rounded bg-amber-100 text-amber-700 hover:bg-amber-200 dark:bg-amber-900/30 dark:text-amber-300 transition"
            >
              فتح
            </button>
            <button
              v-else-if="lesson.id !== currentLessonId"
              class="text-xs px-2 py-1 rounded bg-emerald-100 text-emerald-700 hover:bg-emerald-200 dark:bg-emerald-900/30 dark:text-emerald-300 transition"
            >
              {{ isLessonCompleted(lesson.id) ? 'مراجعة' : 'تشغيل' }}
            </button>
            <div v-else class="text-xs px-2 py-1 rounded bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300">
              جاري التشغيل
            </div>
          </div>
        </div>

        <!-- No lessons message -->
        <div 
          v-if="!module.lessons || module.lessons.length === 0"
          class="px-4 py-6 text-center text-sm text-slate-500 dark:text-slate-400"
        >
          لا توجد دروس في هذه الوحدة بعد
        </div>
      </div>
    </div>

    <!-- No modules message -->
    <div 
      v-if="!modules || modules.length === 0"
      class="text-center py-8 text-slate-500 dark:text-slate-400"
    >
      <div class="text-4xl mb-3">📚</div>
      <p>لا يوجد محتوى متاح حالياً</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ChevronDown } from 'lucide-vue-next'

const props = defineProps({
  modules: { type: Array, default: () => [] },
  currentLessonId: { type: Number, default: null },
  isPaid: { type: Boolean, default: false },
  enrolled: { type: Boolean, default: false },
  courseProgress: { type: Object, default: () => null } // جديد
})

const emit = defineEmits(['lesson-select', 'locked-lesson'])

const expandedModules = ref(new Set())

// Progress helper methods - جديد
const getLessonProgress = (lessonId) => {
  if (!props.courseProgress?.lessons) return 0
  return props.courseProgress.lessons[lessonId] || 0
}

const isLessonCompleted = (lessonId) => {
  return getLessonProgress(lessonId) >= 100
}

const getModuleProgress = (moduleId) => {
  if (!props.courseProgress?.modules) return 0
  const moduleProgress = props.courseProgress.modules.find(m => m.moduleId === moduleId)
  return moduleProgress?.percent || 0
}

const getCompletedLessonsInModule = (module) => {
  if (!module.lessons || !props.courseProgress?.lessons) return []
  return module.lessons.filter(lesson => isLessonCompleted(lesson.id))
}

const toggleModule = (index) => {
  if (expandedModules.value.has(index)) {
    expandedModules.value.delete(index)
  } else {
    expandedModules.value.add(index)
  }
}

const isLessonLocked = (lesson, module) => {
  // Free course or paid user can access everything
  if (props.isPaid) return false
  
  // Not enrolled users see everything as locked
  if (!props.enrolled) return true
  
  // Free modules (first 2) are always accessible
  if (module.isFree) return false
  
  // Preview lessons are always accessible
  if (lesson.isPreview) return false
  
  // Everything else is locked for unpaid users
  return true
}

const handleLessonClick = (lesson, module, moduleIndex) => {
  if (!props.enrolled) {
    emit('locked-lesson', { lesson, module })
    return
  }
  
  if (isLessonLocked(lesson, module)) {
    emit('locked-lesson', { lesson, module })
    return
  }
  
  emit('lesson-select', { lesson, module, moduleIndex })
}

// Auto-expand modules with progress or current lesson
onMounted(() => {
  if (props.modules.length > 0) {
    // Find module with current lesson
    const currentModuleIndex = props.modules.findIndex(module => 
      module.lessons?.some(lesson => lesson.id === props.currentLessonId)
    )
    
    if (currentModuleIndex >= 0) {
      expandedModules.value.add(currentModuleIndex)
    } else {
      // Expand first module by default
      expandedModules.value.add(0)
    }
    
    // Also expand modules with progress
    if (props.courseProgress?.modules) {
      props.modules.forEach((module, index) => {
        const moduleProgress = getModuleProgress(module.id)
        if (moduleProgress > 0) {
          expandedModules.value.add(index)
        }
      })
    }
  }
})
</script>