<template>
  <div class="card p-6 shadow-sm">
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-lg font-semibold text-slate-900 dark:text-white">
        {{ $t('filter') }}
      </h3>
      <button
        @click="$emit('close')"
        class="p-1 hover:bg-slate-100 dark:hover:bg-slate-700 rounded-lg transition-colors"
      >
        <X class="w-5 h-5 text-slate-500" />
      </button>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <!-- Location Filter -->
      <div>
        <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
          {{ $t('location') }}
        </label>
        <select
          :value="filters.location"
          @change="handleFilterChange('location', $event.target.value)"
          class="input-field"
        >
          <option value="">{{ $t('all_locations') }}</option>
          <option v-for="location in LOCATIONS" :key="location" :value="location">
            {{ location }}
          </option>
        </select>
      </div>

      <!-- Category Filter -->
      <div>
        <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
          {{ $t('category') }}
        </label>
        <select
          :value="filters.category"
          @change="handleFilterChange('category', $event.target.value)"
          class="input-field"
        >
          <option value="">{{ $t('all_categories') }}</option>
          <option v-for="category in CATEGORIES" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
      </div>

      <!-- Price Filter -->
      <div>
        <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-2">
          {{ $t('price') }}
        </label>
        <div class="space-y-2">
          <label class="flex items-center">
            <input
              type="radio"
              name="price"
              :checked="filters.isFree === undefined"
              @change="handleFilterChange('isFree', undefined)"
              class="mr-2 rtl:mr-0 rtl:ml-2"
            />
            <span class="text-sm text-slate-700 dark:text-slate-300">
              جميع الكورسات
            </span>
          </label>
          <label class="flex items-center">
            <input
              type="radio"
              name="price"
              :checked="filters.isFree === true"
              @change="handleFilterChange('isFree', true)"
              class="mr-2 rtl:mr-0 rtl:ml-2"
            />
            <span class="text-sm text-slate-700 dark:text-slate-300">
              {{ $t('free') }}
            </span>
          </label>
          <label class="flex items-center">
            <input
              type="radio"
              name="price"
              :checked="filters.isFree === false"
              @change="handleFilterChange('isFree', false)"
              class="mr-2 rtl:mr-0 rtl:ml-2"
            />
            <span class="text-sm text-slate-700 dark:text-slate-300">
              {{ $t('paid') }}
            </span>
          </label>
        </div>
      </div>
    </div>

    <!-- Clear Filters -->
    <div class="mt-6 pt-6 border-t border-slate-200 dark:border-slate-700">
      <button
        @click="handleClearFilters"
        class="btn-secondary w-full"
      >
        مسح جميع الفلاتر
      </button>
    </div>
  </div>
</template>

<script>
import { X } from 'lucide-vue-next'

// ثوابت الفلترة
const LOCATIONS = ['Riyadh', 'Jeddah', 'Dammam', 'Mecca', 'Medina'];
const CATEGORIES = ['Programming', 'Design', 'Data Science', 'Mobile Development', 'Marketing', 'Business Management'];

export default {
  name: 'FilterPanel',
  components: {
    X
  },
  props: {
    filters: {
      type: Object,
      required: true
    }
  },
  emits: ['update-filter', 'clear-filters', 'close'],
  setup(props, { emit }) {
    const handleFilterChange = (key, value) => {
      emit('update-filter', { [key]: value })
    }
    
    const handleClearFilters = () => {
      emit('clear-filters')
    }
    
    return {
      LOCATIONS,
      CATEGORIES,
      handleFilterChange,
      handleClearFilters
    }
  }
}
</script>