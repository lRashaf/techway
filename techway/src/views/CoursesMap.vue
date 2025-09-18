<template>
  <Layout>
    <div class="container mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <h1 class="text-2xl font-bold mb-4">الخريطة</h1>
      <CoursesMap />
    </div>
  </Layout>
</template>

<script setup>
import Layout from '@/components/Layout.vue'
// هذا هو الكمبوننت الموجود عندك مسبقًا
import CoursesMap from '@/components/maps/CoursesMap.vue'
</script>
