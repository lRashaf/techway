// في services/api/progressService.js

import apiClient from './index' // أو حسب هيكلة مشروعك

const progressService = {
  
  // جلب تقدم الكورس للمستخدم
  async getCourseProgress(userId, courseId) {
    try {
      const response = await apiClient.get(`/progress/course/${courseId}`)
      console.log('Progress service response:', response.data)
      return response.data
    } catch (error) {
      console.error('Error fetching course progress:', error)
      // إرجاع بيانات فارغة بدلاً من خطأ
      return {
        courseId: courseId,
        percent: 0,
        modules: [],
        lessons: {}
      }
    }
  },

  // تحديث تقدم الدرس
  async updateLessonProgress(userId, payload) {
    try {
      console.log('Updating lesson progress:', payload)
      const response = await apiClient.post('/progress', payload)
      console.log('Progress update response:', response.data)
      return response.data
    } catch (error) {
      console.error('Error updating lesson progress:', error)
      throw error
    }
  },

  // حساب النسبة المئوية للتقدم
  calculateModuleProgress(lessons, progressData) {
    if (!lessons.length) return 0
    
    let totalProgress = 0
    lessons.forEach(lesson => {
      const lessonProgress = progressData[lesson.id] || 0
      totalProgress += lessonProgress
    })
    
    return Math.round(totalProgress / lessons.length)
  },

  // تحديد ما إذا كان الدرس مكتمل
  isLessonCompleted(lessonId, progressData) {
    return (progressData[lessonId] || 0) >= 100
  },

  // تحديد ما إذا كانت الوحدة مكتملة
  isModuleCompleted(moduleId, modules, progressData) {
    const module = modules.find(m => m.id === moduleId)
    if (!module || !module.lessons) return false
    
    return module.lessons.every(lesson => 
      this.isLessonCompleted(lesson.id, progressData)
    )
  },

  // حساب إجمالي التقدم في الكورس
  calculateCourseProgress(modules, progressData) {
    if (!modules.length) return 0
    
    let totalLessons = 0
    let completedLessons = 0
    
    modules.forEach(module => {
      if (module.lessons) {
        totalLessons += module.lessons.length
        module.lessons.forEach(lesson => {
          if (this.isLessonCompleted(lesson.id, progressData)) {
            completedLessons++
          }
        })
      }
    })
    
    return totalLessons > 0 ? Math.round((completedLessons / totalLessons) * 100) : 0
  }
}

export { progressService }

// في services/api/index.js - تأكد من إضافة التصدير
export { progressService } from './progressService'