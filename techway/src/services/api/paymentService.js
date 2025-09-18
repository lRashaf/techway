// في services/api/paymentService.js

import { apiClient } from './index' // أو حسب هيكلة مشروعك

const paymentService = {
  
  // إنشاء جلسة دفع (للاستخدام مع مزودي الدفع الحقيقيين)
  async createPaymentSession(payload) {
    try {
      console.log('Creating payment session:', payload)
      
      const response = await apiClient.post('/payments/create', null, {
        params: {
          userId: payload.userId,
          courseId: payload.courseId
        }
      })
      
      return response.data
    } catch (error) {
      console.error('Error creating payment session:', error)
      throw error
    }
  },

  // تأكيد الدفع (للمحاكاة والدفع الحقيقي)
  async confirmPayment(payload) {
    try {
      console.log('Confirming payment:', payload)
      
      const response = await apiClient.get('/payments/success', {
        params: {
          userId: payload.userId,
          courseId: payload.courseId,
          method: payload.method || 'card'
        }
      })
      
      // إذا كانت الاستجابة redirect (302) فهذا يعني نجح
      return { success: true, data: response.data }
      
    } catch (error) {
      console.error('Error confirming payment:', error)
      
      // إذا كان 302 redirect، نعتبره نجاح
      if (error.response?.status === 302) {
        return { success: true, redirected: true }
      }
      
      return { success: false, error: error.message }
    }
  },

  // للتوافق مع الكود القديم
  async redirectToCheckout(payload) {
    console.log('Legacy redirectToCheckout called, using confirmPayment instead')
    return this.confirmPayment(payload)
  },

  // التحقق من حالة الدفع
  async getPaymentStatus(userId, courseId) {
    try {
      const response = await apiClient.get(`/enrollments/access`, {
        params: { userId, courseId }
      })
      
      return {
        enrolled: response.data.data.enrolled,
        paid: response.data.data.paid,
        status: response.data.data.status
      }
    } catch (error) {
      console.error('Error getting payment status:', error)
      return { enrolled: false, paid: false, status: 'ERROR' }
    }
  }
}

export { paymentService }