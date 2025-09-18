<template>
  <Layout>
    <div class="py-8">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Header -->
        <div class="mb-8">
          <h1 class="text-3xl lg:text-4xl font-bold text-slate-900 dark:text-white mb-4">
            {{ $t('courses_title') }}
          </h1>
          <p class="text-lg text-slate-600 dark:text-slate-400">
            اكتشف مجموعة واسعة من الكورسات التقنية المتخصصة
          </p>
        </div>

        <!-- Search and Filters -->
        <div class="mb-8 space-y-4">
          <!-- Search Bar -->
          <SearchBar
            v-model="filters.search"
            placeholder="ابحث عن الكورسات، المدربين، أو المواضيع..."
          />

          <!-- Filter Controls -->
          <div class="flex flex-col sm:flex-row items-start sm:items-center justify-between space-y-4 sm:space-y-0 sm:space-x-4 rtl:space-x-reverse">
            <div class="flex items-center space-x-4 rtl:space-x-reverse">
              <button
                @click="showFilters = !showFilters"
                class="flex items-center space-x-2 rtl:space-x-reverse px-4 py-2 border border-slate-300 dark:border-slate-600 rounded-lg hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
              >
                <Filter class="w-4 h-4" />
                <span>{{ $t('filter') }}</span>
                <span v-if="activeFiltersCount > 0" class="bg-purple-600 text-white text-xs px-2 py-1 rounded-full">
                  {{ activeFiltersCount }}
                </span>
              </button>

              <button
                v-if="activeFiltersCount > 0"
                @click="clearFilters"
                class="text-sm text-purple-600 dark:text-purple-400 hover:underline"
              >
                مسح الفلاتر
              </button>
            </div>

            <!-- Sort -->
            <div class="flex items-center space-x-2 rtl:space-x-reverse">
              <SortDesc class="w-4 h-4 text-slate-500" />
              <select
                v-model="sortValue"
                @change="handleSortChange"
                class="border border-slate-300 dark:border-slate-600 rounded-lg px-3 py-2 bg-white dark:bg-slate-800 text-slate-900 dark:text-white"
              >
                <option v-for="option in sortOptions" :key="option.value" :value="option.value">
                  {{ option.label }}
                </option>
              </select>
            </div>
          </div>

          <!-- Filter Panel -->
          <FilterPanel
            v-if="showFilters"
            :filters="filters"
            @update-filter="handleFilterChange"
            @clear-filters="clearFilters"
            @close="showFilters = false"
          />
        </div>

        <!-- Results -->
        <div class="mb-6">
          <p class="text-slate-600 dark:text-slate-400">
            <span v-if="loading">{{ $t('loading') }}</span>
            <span v-else>تم العثور على {{ courses.length }} كورس</span>
          </p> 
        </div>

        <!-- Courses Grid -->
        <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-for="i in 6" :key="i" class="card p-6 animate-pulse">
            <div class="bg-slate-200 dark:bg-slate-700 h-48 rounded-lg mb-4"></div>
            <div class="space-y-3">
              <div class="bg-slate-200 dark:bg-slate-700 h-4 rounded w-3/4"></div>
              <div class="bg-slate-200 dark:bg-slate-700 h-4 rounded w-1/2"></div>
              <div class="bg-slate-200 dark:bg-slate-700 h-4 rounded w-1/4"></div>
            </div>
          </div>
        </div>

        <div v-else-if="courses.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <CourseCard
            v-for="course in courses"
            :key="course.id"
            :course="course"
          />
        </div>

        <!-- Empty State -->
        <div v-else class="text-center py-16">
          <div class="w-24 h-24 bg-slate-100 dark:bg-slate-800 rounded-full flex items-center justify-center mx-auto mb-4">
            <Search class="w-12 h-12 text-slate-400" />
          </div>
          <h3 class="text-xl font-semibold text-slate-900 dark:text-white mb-2">
            لم يتم العثور على كورسات
          </h3>
          <p class="text-slate-600 dark:text-slate-400 mb-6">
            جرب تغيير معايير البحث أو الفلاتر
          </p>
          <button
            @click="clearFilters"
            class="bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded-lg font-medium transition-colors"
          >
            مسح جميع الفلاتر
          </button>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { courseService } from '@/services/api'
import { useToast } from 'vue-toastification'
import { Search, Filter, SortDesc } from 'lucide-vue-next'
import Layout from '@/components/Layout.vue'
import CourseCard from '@/components/CourseCard.vue'
import SearchBar from '@/components/SearchBar.vue'
import FilterPanel from '@/components/FilterPanel.vue'

export default {
  name: 'Courses',
  components: {
    Layout,
    CourseCard,
    SearchBar,
    FilterPanel,
    Search,
    Filter,
    SortDesc
  },
  setup() {
    const { t } = useI18n()
    const route = useRoute()
    const router = useRouter()
    const toast = useToast()
    
    const courses = ref([])
    const loading = ref(true)
    const showFilters = ref(false)
    
    const filters = reactive({
      search: route.query.search || '',
      location: route.query.location || '',
      category: route.query.category || '',
      isFree: route.query.isFree ? route.query.isFree === 'true' : undefined,
      sortBy: route.query.sortBy || 'createdAt',
      order: route.query.order || 'desc'
    })
    
    const sortValue = ref(`${filters.sortBy}:${filters.order}`)
    
    const sortOptions = [
      { value: 'createdAt:desc', label: t('sort_by_newest') },
      { value: 'createdAt:asc', label: t('sort_by_oldest') },
      { value: 'title:asc', label: 'الاسم: أ-ي' },
      { value: 'title:desc', label: 'الاسم: ي-أ' },
      { value: 'price:asc', label: t('sort_by_price_low') },
      { value: 'price:desc', label: t('sort_by_price_high') }
    ]
    
    const activeFiltersCount = computed(() => {
      return [
        filters.search,
        filters.location,
        filters.category,
        filters.isFree !== undefined ? 'price' : null
      ].filter(Boolean).length
    })
    
    const fetchCourses = async () => {
      loading.value = true
      try {
        let response
        
        // إذا كان هناك فلاتر، استخدم البحث المتقدم
        if (filters.search || filters.location || filters.category || filters.isFree !== undefined) {
          response = await courseService.searchCourses({
            keyword: filters.search,
            location: filters.location,
            category: filters.category,
            isFree: filters.isFree
          })
        } else {
          // جلب جميع الكورسات
          response = await courseService.getAllCourses()
        }
        
        courses.value = response.data || []
      } catch (error) {
        console.error('Error fetching courses:', error)
        toast.error('حدث خطأ في جلب الكورسات')
        courses.value = []
      } finally {
        loading.value = false
      }
    }
    
    const handleFilterChange = (newFilters) => {
      Object.assign(filters, newFilters)
    }
    
    const handleSortChange = () => {
      const [sortBy, order] = sortValue.value.split(':')
      filters.sortBy = sortBy
      filters.order = order
    }
    
    const clearFilters = () => {
      Object.assign(filters, {
        search: '',
        location: '',
        category: '',
        isFree: undefined,
        sortBy: 'createdAt',
        order: 'desc'
      })
      sortValue.value = 'createdAt:desc'
    }
    
    const updateURL = () => {
      const query = {}
      if (filters.search) query.search = filters.search
      if (filters.location) query.location = filters.location
      if (filters.category) query.category = filters.category
      if (filters.isFree !== undefined) query.isFree = filters.isFree.toString()
      if (filters.sortBy !== 'createdAt') query.sortBy = filters.sortBy
      if (filters.order !== 'desc') query.order = filters.order
      
      router.push({ query })
    }
    
    // مراقبة تغيير الفلاتر
    watch(filters, () => {
      fetchCourses()
      updateURL()
    }, { deep: true })
    
    onMounted(() => {
      fetchCourses()
    })
    
    return {
      courses,
      loading,
      showFilters,
      filters,
      sortValue,
      sortOptions,
      activeFiltersCount,
      handleFilterChange,
      handleSortChange,
      clearFilters
    }
  }
}
</script>