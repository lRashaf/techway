import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  // State
  const isDark = ref(false)
  
  // Actions
  const initTheme = () => {
    // التحقق من الثيم المحفوظ أو إعداد النظام
    const savedTheme = localStorage.getItem('theme')
    if (savedTheme) {
      isDark.value = savedTheme === 'dark'
    } else {
      // استخدام إعداد النظام
      isDark.value = window.matchMedia('(prefers-color-scheme: dark)').matches
    }
    
    // تطبيق الثيم
    applyTheme()
  }
  
  const toggleTheme = () => {
    isDark.value = !isDark.value
  }
  
  const setTheme = (theme) => {
    isDark.value = theme === 'dark'
  }
  
  const applyTheme = () => {
    if (isDark.value) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }
  
  // مراقبة تغيير الثيم وحفظه
  watch(isDark, (newValue) => {
    localStorage.setItem('theme', newValue ? 'dark' : 'light')
    applyTheme()
  }, { immediate: true })
  
  return {
    // State
    isDark,
    
    // Actions
    initTheme,
    toggleTheme,
    setTheme
  }
})