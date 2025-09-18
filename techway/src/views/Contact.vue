<template>
  <Layout>
    <div class="py-16">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Header -->
        <div class="text-center space-y-4 mb-16">
          <h1 class="text-4xl lg:text-5xl font-bold text-slate-900 dark:text-white">
            {{ $t('contact') }}
          </h1>
          <p class="text-xl text-slate-600 dark:text-slate-400 max-w-2xl mx-auto">
            نحن هنا لمساعدتك. تواصل معنا في أي وقت وسنرد عليك في أقرب وقت ممكن
          </p>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-12">
          <!-- Contact Info -->
          <div class="space-y-8">
            <div>
              <h2 class="text-2xl font-bold text-slate-900 dark:text-white mb-6">
                معلومات التواصل
              </h2>
              <div class="space-y-6">
                <div v-for="(contact, index) in contactInfo" :key="index" class="flex items-start space-x-4 rtl:space-x-reverse">
                  <div :class="[
                    'w-12 h-12 rounded-lg flex items-center justify-center flex-shrink-0',
                    contact.bgColor
                  ]">
                    <component :is="contact.icon" :class="['w-6 h-6', contact.iconColor]" />
                  </div>
                  <div>
                    <h3 class="font-semibold text-slate-900 dark:text-white mb-1">
                      {{ contact.title }}
                    </h3>
                    <div v-for="(item, i) in contact.items" :key="i" class="text-slate-600 dark:text-slate-400">
                      {{ item }}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- FAQ -->
            <div class="bg-slate-50 dark:bg-slate-800/50 p-6 rounded-xl">
              <h3 class="font-semibold text-slate-900 dark:text-white mb-4">
                أسئلة شائعة
              </h3>
              <div class="space-y-3 text-sm">
                <div v-for="(faq, index) in faqs" :key="index">
                  <p class="font-medium text-slate-700 dark:text-slate-300">
                    {{ faq.question }}
                  </p>
                  <p class="text-slate-600 dark:text-slate-400">
                    {{ faq.answer }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Contact Form -->
          <div class="lg:col-span-2">
            <div class="card p-8">
              <h2 class="text-2xl font-bold text-slate-900 dark:text-white mb-6">
                أرسل لنا رسالة
              </h2>

              <form @submit.prevent="handleSubmit" class="space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label for="name" class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                      الاسم *
                    </label>
                    <input
                      id="name"
                      v-model="form.name"
                      type="text"
                      required
                      class="input-field"
                      placeholder="اسمك الكامل"
                    />
                  </div>

                  <div>
                    <label for="email" class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                      البريد الإلكتروني *
                    </label>
                    <input
                      id="email"
                      v-model="form.email"
                      type="email"
                      required
                      class="input-field"
                      placeholder="your@email.com"
                    />
                  </div>
                </div>

                <div>
                  <label for="subject" class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                    الموضوع *
                  </label>
                  <input
                    id="subject"
                    v-model="form.subject"
                    type="text"
                    required
                    class="input-field"
                    placeholder="موضوع رسالتك"
                  />
                </div>

                <div>
                  <label for="message" class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
                    الرسالة *
                  </label>
                  <textarea
                    id="message"
                    v-model="form.message"
                    required
                    rows="6"
                    class="input-field resize-none"
                    placeholder="اكتب رسالتك هنا..."
                  ></textarea>
                </div>

                <button
                  type="submit"
                  :disabled="isSubmitting"
                  class="w-full bg-purple-600 hover:bg-purple-700 disabled:bg-blue-400 text-white px-6 py-3 rounded-lg font-medium transition-colors flex items-center justify-center space-x-2 rtl:space-x-reverse"
                >
                  <div v-if="isSubmitting" class="loading-spinner w-5 h-5"></div>
                  <Send v-else class="w-5 h-5" />
                  <span>{{ isSubmitting ? 'جاري الإرسال...' : 'إرسال الرسالة' }}</span>
                </button>
              </form>
            </div>
          </div>
        </div>

        <!-- Map Section -->
        <div class="mt-16">
          <h2 class="text-2xl font-bold text-slate-900 dark:text-white mb-6 text-center">
            موقعنا
          </h2>
          <div class="rounded-xl overflow-hidden h-96 shadow-lg">
            <iframe
              src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3624.395623414567!2d46.67189597584839!3d24.71362957793845!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3e2f03890d489399%3A0xba974d1c98e79fd5!2z2K3Ziigg2KfZhNmF2YTZgyDZgdmH2K8g2IHYp9ix2LMg2KfZhNi12YXZiNivINmB2LHYudmKINii2YQg2LPYudmI2K8!5e0!3m2!1sar!2ssa!4v1694435273842!5m2!1sar!2ssa"
              width="100%"
              height="100%"
              style="border:0;"
              allowfullscreen=""
              loading="lazy"
              referrerpolicy="no-referrer-when-downgrade"
              class="w-full h-full"
            ></iframe>
          </div>
          <div class="text-center mt-4">
            <p class="text-slate-600 dark:text-slate-400">
              الرياض، حي الملك فهد - المملكة العربية السعودية
            </p>
          </div>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script>
import { ref, reactive } from 'vue'
import { useI18n } from 'vue-i18n'
import { useToast } from 'vue-toastification'
import { Mail, Phone, MapPin, Clock, Send } from 'lucide-vue-next'
import Layout from '@/components/Layout.vue'

export default {
  name: 'Contact',
  components: {
    Layout,
    Mail,
    Phone,
    MapPin,
    Clock,
    Send
  },
  setup() {
    const { t, locale } = useI18n()
    const toast = useToast()
    
    const isSubmitting = ref(false)
    const isRTL = locale.value === 'ar'
    
    const form = reactive({
      name: '',
      email: '',
      subject: '',
      message: ''
    })
    
    const contactInfo = [
      {
        icon: Mail,
        title: 'البريد الإلكتروني',
        items: ['info@TechWay.com', 'support@TechWay.com'],
        bgColor: 'bg-blue-100 dark:bg-blue-900/30',
        iconColor: 'text-purple-600 dark:text-purple-400'
      },
      {
        icon: Phone,
        title: 'الهاتف',
        items: ['+966 50 123 4567', '+966 11 456 7890'],
        bgColor: 'bg-green-100 dark:bg-green-900/30',
        iconColor: 'text-green-600 dark:text-green-400'
      },
      {
        icon: MapPin,
        title: 'العنوان',
        items: ['الرياض، حي الملك فهد', 'المملكة العربية السعودية'],
        bgColor: 'bg-purple-100 dark:bg-purple-900/30',
        iconColor: 'text-purple-600 dark:text-purple-400'
      },
      {
        icon: Clock,
        title: 'ساعات العمل',
        items: ['الأحد - الخميس: 9:00 - 18:00', 'الجمعة - السبت: مغلق'],
        bgColor: 'bg-orange-100 dark:bg-orange-900/30',
        iconColor: 'text-orange-600 dark:text-orange-400'
      }
    ]
    
    const faqs = [
      {
        question: 'كم يستغرق الرد على الاستفسارات؟',
        answer: 'نرد خلال 24 ساعة في أيام العمل'
      },
      {
        question: 'هل يمكنني الحصول على استشارة مجانية؟',
        answer: 'نعم، نقدم استشارات مجانية لاختيار المسار المناسب'
      }
    ]
    
    const handleSubmit = async () => {
      isSubmitting.value = true
      
      try {
        // محاكاة إرسال النموذج
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        toast.success(isRTL ? 'تم إرسال رسالتك بنجاح!' : 'Message sent successfully!')
        
        // مسح النموذج
        Object.keys(form).forEach(key => {
          form[key] = ''
        })
      } catch (error) {
        toast.error(isRTL ? 'حدث خطأ، حاول مرة أخرى' : 'Something went wrong')
      } finally {
        isSubmitting.value = false
      }
    }
    
    return {
      form,
      isSubmitting,
      contactInfo,
      faqs,
      handleSubmit
    }
  }
}
</script>