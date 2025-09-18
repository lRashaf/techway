<template>
  <div :class="['relative', className]">
    <div class="absolute inset-y-0 left-0 rtl:left-auto rtl:right-0 pl-3 rtl:pl-0 rtl:pr-3 flex items-center pointer-events-none">
      <Search class="w-5 h-5 text-slate-400" />
    </div>
    
    <input
      v-model="localValue"
      type="text"
      :placeholder="placeholder"
      class="block w-full pl-10 rtl:pl-4 rtl:pr-10 pr-4 py-3 border border-slate-300 dark:border-slate-600 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-slate-800 text-slate-900 dark:text-white placeholder-slate-500 dark:placeholder-slate-400"
    />
    
    <button
      v-if="localValue"
      @click="handleClear"
      class="absolute inset-y-0 right-0 rtl:right-auto rtl:left-0 pr-3 rtl:pr-0 rtl:pl-3 flex items-center hover:text-slate-600 dark:hover:text-slate-300"
    >
      <X class="w-5 h-5 text-slate-400" />
    </button>
  </div>
</template>

<script>
import { ref, watch, onMounted } from 'vue'
import { Search, X } from 'lucide-vue-next'

export default {
  name: 'SearchBar',
  components: {
    Search,
    X
  },
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: 'البحث...'
    },
    className: {
      type: String,
      default: ''
    },
    debounce: {
      type: Number,
      default: 300
    }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const localValue = ref(props.modelValue)
    
    // مزامنة القيمة المحلية مع القيمة الخارجية
    watch(() => props.modelValue, (newValue) => {
      localValue.value = newValue
    })
    
    // إرسال القيمة مع التأخير (debounce)
    let timeoutId = null
    watch(localValue, (newValue) => {
      if (timeoutId) {
        clearTimeout(timeoutId)
      }
      
      timeoutId = setTimeout(() => {
        emit('update:modelValue', newValue)
      }, props.debounce)
    })
    
    const handleClear = () => {
      localValue.value = ''
      emit('update:modelValue', '')
    }
    
    onMounted(() => {
      localValue.value = props.modelValue
    })
    
    return {
      localValue,
      handleClear
    }
  }
}
</script>