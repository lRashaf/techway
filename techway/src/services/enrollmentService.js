import api from './api';

export const enrollmentService = {
  enroll({ userId, courseId }) {
    // backend expects body: { userId, courseId }
    return api.post('/enrollments', { userId, courseId });
  }
};
