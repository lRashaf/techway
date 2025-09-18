<template>
  <Layout>
    <div class="py-16 space-y-16">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Hero Section -->
        <div class="text-center space-y-6 max-w-4xl mx-auto">
          <h1 class="text-4xl lg:text-5xl font-bold text-slate-900 dark:text-white">
            عن TechWay
          </h1>
          <p class="text-xl text-slate-600 dark:text-slate-400 leading-relaxed">
            نحن منصة تعليمية متخصصة في تقديم أفضل الكورسات التقنية 
            لتطوير المهارات وبناء مستقبل مهني متميز
          </p>
        </div>

        <!-- Mission & Vision -->
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 mt-16">
          <div class="space-y-6">
            <div class="flex items-center space-x-3 rtl:space-x-reverse">
              <div class="w-12 h-12 bg-gradient-to-br from-purple-500 to-blue-600 rounded-lg flex items-center justify-center">
                <Target class="w-6 h-6 text-white" />
              </div>
              <h2 class="text-2xl font-bold text-slate-900 dark:text-white">
                رؤيتنا
              </h2>
            </div>
            <p class="text-slate-600 dark:text-slate-400 leading-relaxed">
              أن نكون المنصة الرائدة في تعليم التقنية في المنطقة، 
              ونمكن كل شخص من اكتساب المهارات التقنية المطلوبة 
              لسوق العمل الحديث.
            </p>
          </div>

          <div class="space-y-6">
            <div class="flex items-center space-x-3 rtl:space-x-reverse">
              <div class="w-12 h-12 bg-gradient-to-br from-purple-500 to-green-600 rounded-lg flex items-center justify-center">
                <BookOpen class="w-6 h-6 text-white" />
              </div>
              <h2 class="text-2xl font-bold text-slate-900 dark:text-white">
                مهمتنا
              </h2>
            </div>
            <p class="text-slate-600 dark:text-slate-400 leading-relaxed">
              تقديم تعليم تقني عالي الجودة من خلال كورسات متخصصة 
              يقدمها خبراء في المجال، مع توفير بيئة تعليمية 
              تفاعلية ومرنة تناسب احتياجات جميع المتعلمين.
            </p>
          </div>
        </div>

        <!-- Values -->
        <div class="mt-16">
          <h2 class="text-3xl font-bold text-center text-slate-900 dark:text-white mb-12">
            قيمنا
          </h2>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div v-for="(value, index) in values" :key="index" class="text-center space-y-4">
              <div class="w-16 h-16 bg-gradient-to-br from-purple-500 to-blue-600 rounded-full flex items-center justify-center mx-auto">
                <component :is="value.icon" class="w-8 h-8 text-white" />
              </div>
              <h3 class="text-xl font-semibold text-slate-900 dark:text-white">
                {{ value.title }}
              </h3>
              <p class="text-slate-600 dark:text-slate-400">
                {{ value.description }}
              </p>
            </div>
          </div>
        </div>

        <!-- Statistics -->
        <div class="bg-gradient-to-r from-purple-600 to-purple-700 rounded-2xl p-8 lg:p-12 text-white mt-16">
          <div class="text-center mb-8">
            <h2 class="text-3xl font-bold mb-4">إنجازاتنا بالأرقام</h2>
            <p class="text-blue-100">نفخر بالثقة التي وضعها طلابنا فينا</p>
          </div>
          
          <div class="grid grid-cols-2 lg:grid-cols-4 gap-8">
            <div v-for="(stat, index) in statistics" :key="index" class="text-center">
              <div class="text-3xl lg:text-4xl font-bold mb-2">{{ stat.value }}</div>
              <div class="text-blue-100">{{ stat.label }}</div>
            </div>
          </div>
        </div>

        <!-- Team Section -->
        <div class="mt-16">
          <h2 class="text-3xl font-bold text-center text-slate-900 dark:text-white mb-12">
            فريقنا
          </h2>
          <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div
              v-for="(member, index) in teamMembers"
              :key="index"
              class="card p-6 text-center course-card"
            >
              <img
                :src="member.image"
                :alt="member.name"
                class="w-24 h-24 rounded-full mx-auto mb-4 object-cover"
              />
              <h3 class="text-lg font-semibold text-slate-900 dark:text-white mb-1">
                {{ member.name }}
              </h3>
              <p class="text-purple-600 dark:text-purple-400 text-sm font-medium mb-3">
                {{ member.role }}
              </p>
              <p class="text-slate-600 dark:text-slate-400 text-sm">
                {{ member.description }}
              </p>
            </div>
          </div>
        </div>

        <!-- Why Choose Us -->
        <div class="mt-16">
          <h2 class="text-3xl font-bold text-center text-slate-900 dark:text-white mb-12">
            لماذا تختار TechWay
          </h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div v-for="(feature, index) in features" :key="index" class="flex items-start space-x-4 rtl:space-x-reverse">
              <div class="w-10 h-10 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center flex-shrink-0">
                <component :is="feature.icon" class="w-5 h-5 text-purple-600 dark:text-purple-400" />
              </div>
              <div>
                <h3 class="font-semibold text-slate-900 dark:text-white mb-2">
                  {{ feature.title }}
                </h3>
                <p class="text-slate-600 dark:text-slate-400 text-sm">
                  {{ feature.description }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- CTA Section -->
        <div class="bg-slate-50 dark:bg-slate-800/50 rounded-2xl p-8 lg:p-12 text-center mt-16">
          <h2 class="text-3xl font-bold text-slate-900 dark:text-white mb-4">
            انضم إلى رحلة التطوير معنا
          </h2>
          <p class="text-lg text-slate-600 dark:text-slate-400 mb-8 max-w-2xl mx-auto">
            ابدأ رحلتك في تعلم التقنية مع خبراء المجال واكتسب المهارات التي تحتاجها لتحقيق أهدافك المهنية
          </p>
          <div class="flex flex-col sm:flex-row gap-4 justify-center">
            <router-link
              to="/courses"
              class="bg-purple-600 hover:bg-purple-700 text-white px-8 py-3 rounded-lg font-semibold transition-colors"
            >
              استكشف الكورسات
            </router-link>
            <router-link
              to="/contact"
              class="btn-secondary"
            >
              تواصل معنا
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script>
import { BookOpen, Target, Users, Award, Shield, Zap, Clock, CheckCircle } from 'lucide-vue-next'
import Layout from '@/components/Layout.vue'

export default {
  name: 'About',
  components: {
    Layout,
    BookOpen,
    Target,
    Users,
    Award,
    Shield,
    Zap,
    Clock,
    CheckCircle
  },
  setup() {
    const values = [
      {
        icon: Users,
        title: 'التميز في التعليم',
        description: 'نسعى لتقديم أعلى معايير الجودة في المحتوى التعليمي'
      },
      {
        icon: Award,
        title: 'الابتكار والتطوير',
        description: 'نواكب أحدث التقنيات ونطور منهجياتنا باستمرار'
      },
      {
        icon: Target,
        title: 'التعلم المستمر',
        description: 'نؤمن بأهمية التطوير المستمر ومواكبة التغييرات'
      }
    ]
    
    const statistics = [
      { value: '500+', label: 'طالب نشط' },
      { value: '50+', label: 'كورس متاح' },
      { value: '20+', label: 'مدرب خبير' },
      { value: '95%', label: 'معدل الرضا' }
    ]
    
    const teamMembers = [
      {
        name: 'أحمد محمد',
        role: 'مؤسس ومدير تنفيذي',
        image: '',
        description: 'خبرة 15 عام في تطوير البرمجيات والتعليم التقني'
      },
      {
        name: 'سارة أحمد',
        role: 'مديرة المحتوى التعليمي',
        image: '',
        description: 'متخصصة في تصميم المناهج والتقييم التعليمي'
      },
      {
        name: 'محمد العلي',
        role: 'مدير التقنية',
        image: '',
        description: 'خبير في تطوير المنصات التعليمية وتحليل البيانات'
      }
    ]
    
    const features = [
      {
        icon: Shield,
        title: 'محتوى عالي الجودة',
        description: 'جميع الكورسات مراجعة من قبل خبراء في المجال'
      },
      {
        icon: Zap,
        title: 'تعلم تفاعلي',
        description: 'منصة تفاعلية مع تمارين عملية ومشاريع تطبيقية'
      },
      {
        icon: Clock,
        title: 'مرونة في التعلم',
        description: 'تعلم في الوقت والوتيرة التي تناسبك'
      },
      {
        icon: CheckCircle,
        title: 'شهادات معتمدة',
        description: 'احصل على شهادات معتمدة عند إكمال الكورسات'
      }
    ]
    
    return {
      values,
      statistics,
      teamMembers,
      features
    }
  }
}
</script>