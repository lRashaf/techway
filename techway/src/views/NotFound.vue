<template>
  <Layout>
    <div class="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-900 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full text-center space-y-8">
        <!-- 404 Animation -->
        <div class="relative">
          <div class="text-9xl font-bold text-slate-200 dark:text-slate-700 select-none">
            404
          </div>
          <div class="absolute inset-0 flex items-center justify-center">
            <Search class="w-24 h-24 text-slate-400 animate-pulse" />
          </div>
        </div>

        <!-- Content -->
        <div class="space-y-4">
          <h1 class="text-2xl font-bold text-slate-900 dark:text-white">
            الصفحة غير موجودة
          </h1>
          <p class="text-slate-600 dark:text-slate-400 leading-relaxed">
            عذراً، لم نتمكن من العثور على الصفحة التي تبحث عنها. 
            قد تكون تم نقلها أو حذفها أو ربما كتبت الرابط بشكل خاطئ.
          </p>
        </div>

        <!-- Actions -->
        <div class="flex flex-col sm:flex-row gap-4 justify-center">
          <router-link
            to="/"
            class="bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded-lg font-medium transition-colors flex items-center justify-center space-x-2 rtl:space-x-reverse"
          >
            <Home class="w-4 h-4" />
            <span>العودة للرئيسية</span>
          </router-link>
          
          <router-link
            to="/courses"
            class="btn-secondary flex items-center justify-center space-x-2 rtl:space-x-reverse"
          >
            <BookOpen class="w-4 h-4" />
            <span>تصفح الكورسات</span>
          </router-link>
        </div>

        <!-- Help Links -->
        <div class="pt-8 border-t border-slate-200 dark:border-slate-700">
          <p class="text-sm text-slate-500 dark:text-slate-400 mb-4">
            أو جرب هذه الروابط المفيدة:
          </p>
          <div class="flex flex-wrap gap-4 justify-center text-sm">
            <router-link to="/about" class="text-purple-600 hover:underline">
              من نحن
            </router-link>
            <router-link to="/contact" class="text-purple-600 hover:underline">
              اتصل بنا
            </router-link>
            <a href="#" class="text-purple-600 hover:underline">
              مركز المساعدة
            </a>
          </div>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script>
import { Search, Home, BookOpen } from 'lucide-vue-next'
import Layout from '@/components/Layout.vue'

export default {
  name: 'NotFound',
  components: {
    Layout,
    Search,
    Home,
    BookOpen
  }
}
</script>