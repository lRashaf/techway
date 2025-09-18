<template>
  <footer class="bg-slate-900 dark:bg-black text-white mt-auto">
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
        <!-- Logo & Description -->
        <div class="space-y-4">
          <div class="flex items-center space-x-2 rtl:space-x-reverse">
            <div class="w-8 h-8 bg-gradient-to-br from-purple-600 to-purple-700 rounded-lg flex items-center justify-center">
              <BookOpen class="w-5 h-5 text-white" />
            </div>
            <span class="text-xl font-bold">TechWay</span>
          </div>
          <p class="text-slate-400 text-sm leading-relaxed">
            {{ $t('hero_subtitle') }}
          </p>
        </div>

        <!-- Quick Links -->
        <div class="space-y-4">
          <h3 class="font-semibold text-lg">{{ $t('courses') }}</h3>
          <div class="space-y-2">
            <router-link 
              to="/courses" 
              class="block text-slate-400 hover:text-white transition-colors text-sm"
            >
              {{ $t('courses_title') }}
            </router-link>
            <router-link 
              to="/courses?category=البرمجة" 
              class="block text-slate-400 hover:text-white transition-colors text-sm"
            >
              البرمجة
            </router-link>
            <router-link 
              to="/courses?category=التصميم" 
              class="block text-slate-400 hover:text-white transition-colors text-sm"
            >
              التصميم
            </router-link>
            <router-link 
              to="/courses?category=علوم البيانات" 
              class="block text-slate-400 hover:text-white transition-colors text-sm"
            >
              علوم البيانات
            </router-link>
          </div>
        </div>

        <!-- Company -->
        <div class="space-y-4">
          <h3 class="font-semibold text-lg">الشركة</h3>
          <div class="space-y-2">
            <router-link 
              to="/about" 
              class="block text-slate-400 hover:text-white transition-colors text-sm"
            >
              {{ $t('about') }}
            </router-link>
            <router-link 
              to="/contact" 
              class="block text-slate-400 hover:text-white transition-colors text-sm"
            >
              {{ $t('contact') }}
            </router-link>
            <a 
              href="#" 
              class="block text-slate-400 hover:text-white transition-colors text-sm"
            >
              سياسة الخصوصية
            </a>
            <a 
              href="#" 
              class="block text-slate-400 hover:text-white transition-colors text-sm"
            >
              شروط الاستخدام
            </a>
          </div>
        </div>

        <!-- Contact Info -->
        <div class="space-y-4">
          <h3 class="font-semibold text-lg">{{ $t('contact') }}</h3>
          <div class="space-y-3">
            <div class="flex items-center space-x-3 rtl:space-x-reverse">
              <Mail class="w-4 h-4 text-purple-400 flex-shrink-0" />
              <span class="text-slate-400 text-sm">info@TechWay.com</span>
            </div>
            <div class="flex items-center space-x-3 rtl:space-x-reverse">
              <Phone class="w-4 h-4 text-purple-400 flex-shrink-0" />
              <span class="text-slate-400 text-sm">+966 50 123 4567</span>
            </div>
            <div class="flex items-center space-x-3 rtl:space-x-reverse">
              <MapPin class="w-4 h-4 text-purple-400 flex-shrink-0" />
              <span class="text-slate-400 text-sm">الرياض، المملكة العربية السعودية</span>
            </div>
          </div>
        </div>
      </div>

      <div class="border-t border-slate-800 dark:border-slate-700 mt-8 pt-8">
        <div class="flex flex-col sm:flex-row justify-between items-center space-y-4 sm:space-y-0">
          <p class="text-slate-400 text-sm">
            © 2024 TechWay. جميع الحقوق محفوظة.
          </p>
          <div class="flex space-x-6 rtl:space-x-reverse">
            <a 
              href="#" 
              class="text-slate-400 hover:text-white transition-colors"
              aria-label="Twitter"
            >
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path d="M6.29 18.251c7.547 0 11.675-6.253 11.675-11.675 0-.178 0-.355-.012-.53A8.348 8.348 0 0020 3.92a8.19 8.19 0 01-2.357.646 4.118 4.118 0 001.804-2.27 8.224 8.224 0 01-2.605.996 4.107 4.107 0 00-6.993 3.743 11.65 11.65 0 01-8.457-4.287 4.106 4.106 0 001.27 5.477A4.073 4.073 0 01.8 7.713v.052a4.105 4.105 0 003.292 4.022 4.095 4.095 0 01-1.853.07 4.108 4.108 0 003.834 2.85A8.233 8.233 0 010 16.407a11.616 11.616 0 006.29 1.84" />
              </svg>
            </a>
            <a 
              href="#" 
              class="text-slate-400 hover:text-white transition-colors"
              aria-label="LinkedIn"
            >
              <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path d="M16.338 16.338H13.67V12.16c0-.995-.017-2.277-1.387-2.277-1.39 0-1.601 1.086-1.601 2.207v4.248H8.014v-8.59h2.559v1.174h.037c.356-.675 1.227-1.387 2.526-1.387 2.703 0 3.203 1.778 3.203 4.092v4.711zM5.005 6.575a1.548 1.548 0 11-.003-3.096 1.548 1.548 0 01.003 3.096zm-1.337 9.763H6.34v-8.59H3.667v8.59zM17.668 1H2.328C1.595 1 1 1.581 1 2.298v15.403C1 18.418 1.595 19 2.328 19h15.34c.734 0 1.332-.582 1.332-1.299V2.298C19 1.581 18.402 1 17.668 1z" />
              </svg>
            </a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script>
import { BookOpen, Mail, Phone, MapPin } from 'lucide-vue-next'

export default {
  name: 'Footer',
  components: {
    BookOpen,
    Mail,
    Phone,
    MapPin
  }
}
</script>