<template>
  <div id="app" :dir="$i18n.locale === 'ar' ? 'rtl' : 'ltr'" :lang="$i18n.locale">
    <router-view />
  </div>
</template>

<script>
import { useThemeStore } from '@/stores/theme'
import { useI18n } from 'vue-i18n'
import { onMounted, watch } from 'vue'

export default {
  name: 'App',
  setup() {
    const themeStore = useThemeStore()
    const { locale } = useI18n()

    // تطبيق الثيم عند تحميل التطبيق
    onMounted(() => {
      themeStore.initTheme()
      // تطبيق اللغة المحفوظة
      const savedLanguage = localStorage.getItem('language') || 'ar'
      locale.value = savedLanguage
    })

    // مراقبة تغيير اللغة وتطبيقها على الـ DOM
    watch(locale, (newLocale) => {
      document.dir = newLocale === 'ar' ? 'rtl' : 'ltr'
      document.documentElement.lang = newLocale
      localStorage.setItem('language', newLocale)
    }, { immediate: true })

    return {
      themeStore
    }
  }
}
</script>

<style>
/* Global styles will be in assets/style.css */
</style>