<template>
  <div class="w-full rounded-xl overflow-hidden bg-black relative">
    <!-- YouTube Video -->
    <div v-if="isYouTubeUrl" class="relative">
      <iframe
        ref="youtubePlayer"
        :src="youTubeEmbedUrl"
        class="w-full aspect-video"
        frameborder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowfullscreen
        @load="onYouTubeLoad"
      />
      
      <!-- Auto Progress Display -->
      <div class="absolute bottom-2 left-2 bg-black/70 text-white text-xs px-2 py-1 rounded">
        تقدم تلقائي: {{ autoProgress }}% ({{ formatTime(currentWatchTime) }}/{{ formatTime(estimatedDuration) }})
      </div>
      
      <!-- Control Buttons -->
      <div class="absolute bottom-2 right-2 flex gap-2">
        <button 
          v-if="!isWatching"
          @click="startWatching"
          class="bg-green-600 text-white text-xs px-3 py-1 rounded hover:bg-green-700"
        >
          ▶ بدء المشاهدة
        </button>
        <button 
          v-if="isWatching"
          @click="pauseWatching"
          class="bg-yellow-600 text-white text-xs px-3 py-1 rounded hover:bg-yellow-700"
        >
          ⏸ إيقاف مؤقت
        </button>
        <button 
          v-if="isWatching"
          @click="finishWatching"
          class="bg-blue-600 text-white text-xs px-3 py-1 rounded hover:bg-blue-700"
        >
          ✓ انتهيت
        </button>
      </div>
    </div>
    
    <!-- Regular Video -->
    <video
      v-else-if="src"
      ref="videoElement"
      class="w-full h-auto"
      :src="src"
      controls
      playsinline
      @ended="handleEnded"
      @timeupdate="handleTimeUpdate"
      @loadedmetadata="handleMetadataLoaded"
      @play="handlePlay"
      @pause="handlePause"
    />
    
    <!-- No Video Message -->
    <div v-else class="p-8 text-center text-slate-300">
      <div class="text-4xl mb-3">📹</div>
      <p>لا يوجد فيديو</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'

const props = defineProps({ 
  src: { type: String, default: '' },
  lessonDuration: { type: Number, default: 15 }, // بالدقائق
  autoSaveProgress: { type: Boolean, default: true }
})

const emit = defineEmits(['ended', 'progress', 'play', 'pause', 'milestone'])

// Regular video refs
const videoElement = ref(null)
const youtubePlayer = ref(null)
const currentTime = ref(0)
const duration = ref(0)

// Auto progress tracking
const isWatching = ref(false)
const watchStartTime = ref(0)
const currentWatchTime = ref(0) // بالثواني
const autoProgress = ref(0)
const progressTimer = ref(null)
const lastSavedProgress = ref(0)

// Computed
const estimatedDuration = computed(() => props.lessonDuration * 60) // تحويل لثواني

const isYouTubeUrl = computed(() => {
  return props.src && (props.src.includes('youtube.com') || props.src.includes('youtu.be'))
})

const youTubeEmbedUrl = computed(() => {
  if (!isYouTubeUrl.value) return ''
  
  let videoId = ''
  
  if (props.src.includes('youtube.com/watch')) {
    videoId = props.src.split('v=')[1]?.split('&')[0]
  } else if (props.src.includes('youtu.be/')) {
    videoId = props.src.split('youtu.be/')[1]?.split('?')[0]
  }
  
  return videoId ? `https://www.youtube.com/embed/${videoId}?enablejsapi=1&rel=0` : ''
})

// Auto Progress Methods
const startWatching = () => {
  console.log('Starting auto watch tracking...')
  isWatching.value = true
  watchStartTime.value = Date.now()
  startProgressTimer()
  emit('play')
}

const pauseWatching = () => {
  console.log('Pausing auto watch tracking...')
  isWatching.value = false
  stopProgressTimer()
  emit('pause')
}

const finishWatching = () => {
  console.log('Finishing lesson manually...')
  autoProgress.value = 100
  currentWatchTime.value = estimatedDuration.value
  emitProgress(100)
  stopWatching()
  emit('ended')
}

const stopWatching = () => {
  isWatching.value = false
  stopProgressTimer()
}

const startProgressTimer = () => {
  stopProgressTimer() // تنظيف أي timer موجود
  
  progressTimer.value = setInterval(() => {
    if (!isWatching.value) return
    
    // حساب الوقت المنقضي
    const elapsed = (Date.now() - watchStartTime.value) / 1000 // بالثواني
    currentWatchTime.value = Math.min(elapsed, estimatedDuration.value)
    
    // حساب النسبة المئوية
    const newProgress = Math.min(100, (currentWatchTime.value / estimatedDuration.value) * 100)
    autoProgress.value = Math.round(newProgress)
    
    // إرسال milestone كل 10%
    if (autoProgress.value >= lastSavedProgress.value + 10) {
      console.log(`Auto milestone reached: ${autoProgress.value}%`)
      emitProgress(autoProgress.value)
      lastSavedProgress.value = autoProgress.value
    }
    
    // إكمال تلقائي عند الوصول لـ 100%
    if (autoProgress.value >= 100) {
      console.log('Auto progress completed!')
      emitProgress(100)
      stopWatching()
      emit('ended')
    }
    
  }, 5000) // كل 5 ثوانٍ
}

const stopProgressTimer = () => {
  if (progressTimer.value) {
    clearInterval(progressTimer.value)
    progressTimer.value = null
  }
}

const emitProgress = (percent) => {
  // محاكاة مدة الفيديو للـ emit
  const fakeDuration = estimatedDuration.value
  const fakeCurrentTime = (percent / 100) * fakeDuration
  
  emit('progress', fakeCurrentTime, fakeDuration)
  emit('milestone', percent)
}

// Utility functions
const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

// Regular Video Handlers (للفيديوهات العادية)
const handleMetadataLoaded = () => {
  if (videoElement.value) {
    duration.value = videoElement.value.duration
    console.log('Video loaded - Duration:', duration.value)
  }
}

const handleTimeUpdate = () => {
  if (videoElement.value && duration.value > 0) {
    currentTime.value = videoElement.value.currentTime
    const progressPercent = Math.round((currentTime.value / duration.value) * 100)
    
    emit('progress', currentTime.value, duration.value)
    
    // حفظ النقاط المهمة
    const milestones = [10, 25, 50, 75, 90, 100]
    milestones.forEach(milestone => {
      if (progressPercent >= milestone && lastSavedProgress.value < milestone) {
        emit('milestone', milestone)
        lastSavedProgress.value = milestone
      }
    })
  }
}

const handlePlay = () => {
  emit('play')
}

const handlePause = () => {
  emit('pause')
}

const handleEnded = () => {
  emit('ended')
  emit('milestone', 100)
}

const onYouTubeLoad = () => {
  console.log('YouTube video loaded')
  // إعادة تعيين القيم
  autoProgress.value = 0
  currentWatchTime.value = 0
  isWatching.value = false
  lastSavedProgress.value = 0
}

// Reset on src change
watch(() => props.src, () => {
  stopWatching()
  autoProgress.value = 0
  currentWatchTime.value = 0
  lastSavedProgress.value = 0
})

// Cleanup
onUnmounted(() => {
  stopProgressTimer()
})

// Expose methods
defineExpose({
  startWatching,
  pauseWatching,
  finishWatching,
  getCurrentProgress: () => autoProgress.value
})
</script>