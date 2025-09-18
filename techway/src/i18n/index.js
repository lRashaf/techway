import { createI18n } from 'vue-i18n'

const messages = {
  en: {
    // Navigation
    home: 'Home',
    courses: 'Courses',
    about: 'About',
    contact: 'Contact',
    profile: 'Profile',
    login: 'Login',
    logout: 'Logout',
    
    // Common
    search: 'Search',
    filter: 'Filter',
    sort: 'Sort',
    loading: 'Loading...',
    error: 'Error',
    success: 'Success',
    save: 'Save',
    cancel: 'Cancel',
    delete: 'Delete',
    edit: 'Edit',
    view: 'View',
    share: 'Share',
    
    // Course
    course: 'Course',
    courses_title: 'Technical Courses',
    course_details: 'Course Details',
    instructor: 'Instructor',
    duration: 'Duration',
    price: 'Price',
    location: 'Location',
    category: 'Category',
    level: 'Level',
    rating: 'Rating',
    reviews: 'Reviews',
    free: 'Free',
    paid: 'Paid',
    hours: 'hours',
    
    // Filters
    all_locations: 'All Locations',
    all_categories: 'All Categories',
    sort_by_newest: 'Newest',
    sort_by_oldest: 'Oldest',
    sort_by_rating: 'Highest Rated',
    sort_by_price_low: 'Price: Low to High',
    sort_by_price_high: 'Price: High to Low',
    
    // Actions
    add_to_favorites: 'Add to Favorites',
    remove_from_favorites: 'Remove from Favorites',
    write_review: 'Write Review',
    enroll_course: 'Enroll in Course',
    
    // Auth
    login_with_email: 'Login with Email',
    welcome_back: 'Welcome Back',
    my_favorites: 'My Favorites',
    email: 'Email',
    password: 'Password',
    name: 'Name',
    register: 'Register',
    
    // Messages
    course_added_to_favorites: 'Course added to favorites',
    course_removed_from_favorites: 'Course removed from favorites',
    review_submitted: 'Review submitted successfully',
    
    // Levels
    beginner: 'Beginner',
    intermediate: 'Intermediate',
    advanced: 'Advanced',
    
    // Theme
    light_mode: 'Light Mode',
    dark_mode: 'Dark Mode',
    
    // Hero Section
    hero_title: 'Master Technical Skills',
    hero_subtitle: 'Discover premium courses from top instructors and advance your career',
    get_started: 'Get Started',
    
    // Features
    expert_instructors: 'Expert Instructors',
    flexible_learning: 'Flexible Learning',
    certification: 'Certification',
    community: 'Community Support',
  },
  ar: {
    // Navigation
    home: 'الرئيسية',
    courses: 'الكورسات',
    about: 'من نحن',
    contact: 'اتصل بنا',
    profile: 'الملف الشخصي',
    login: 'تسجيل الدخول',
    logout: 'تسجيل الخروج',
    
    // Common
    search: 'البحث',
    filter: 'تصفية',
    sort: 'ترتيب',
    loading: 'جاري التحميل...',
    error: 'خطأ',
    success: 'نجح',
    save: 'حفظ',
    cancel: 'إلغاء',
    delete: 'حذف',
    edit: 'تعديل',
    view: 'عرض',
    share: 'مشاركة',
    
    // Course
    course: 'كورس',
    courses_title: 'الكورسات التقنية',
    course_details: 'تفاصيل الكورس',
    instructor: 'المدرب',
    duration: 'المدة',
    price: 'السعر',
    location: 'الموقع',
    category: 'الفئة',
    level: 'المستوى',
    rating: 'التقييم',
    reviews: 'المراجعات',
    free: 'مجاني',
    paid: 'مدفوع',
    hours: 'ساعة',
    
    // Filters
    all_locations: 'جميع المواقع',
    all_categories: 'جميع الفئات',
    sort_by_newest: 'الأحدث',
    sort_by_oldest: 'الأقدم',
    sort_by_rating: 'الأعلى تقييماً',
    sort_by_price_low: 'السعر: من الأقل للأعلى',
    sort_by_price_high: 'السعر: من الأعلى للأقل',
    
    // Actions
    add_to_favorites: 'إضافة للمفضلة',
    remove_from_favorites: 'إزالة من المفضلة',
    write_review: 'كتابة مراجعة',
    enroll_course: 'التسجيل في الكورس',
    
    // Auth
    login_with_email: 'تسجيل الدخول بالإيميل',
    welcome_back: 'مرحباً بعودتك',
    my_favorites: 'مفضلتي',
    email: 'البريد الإلكتروني',
    password: 'كلمة المرور',
    name: 'الاسم',
    register: 'إنشاء حساب',
    
    // Messages
    course_added_to_favorites: 'تم إضافة الكورس للمفضلة',
    course_removed_from_favorites: 'تم إزالة الكورس من المفضلة',
    review_submitted: 'تم إرسال المراجعة بنجاح',
    
    // Levels
    beginner: 'مبتدئ',
    intermediate: 'متوسط',
    advanced: 'متقدم',
    
    // Theme
    light_mode: 'الوضع النهاري',
    dark_mode: 'الوضع الليلي',
    
    // Hero Section
    hero_title: 'إتقان المهارات التقنية',
    hero_subtitle: 'اكتشف كورسات متميزة من أفضل المدربين وطور مسيرتك المهنية',
    get_started: 'ابدأ الآن',
    
    // Features
    expert_instructors: 'مدربون خبراء',
    flexible_learning: 'تعلم مرن',
    certification: 'شهادة معتمدة',
    community: 'دعم المجتمع',
  }
}

const i18n = createI18n({
  locale: localStorage.getItem('language') || 'ar',
  fallbackLocale: 'en',
  messages,
  legacy: false,
  globalInjection: true
})

export default i18n