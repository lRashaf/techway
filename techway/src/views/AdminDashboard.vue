<template>
  <Layout>
    <div class="py-8">
      <div class="container mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Access Check -->
        <div v-if="!authStore.isAdmin" class="min-h-screen flex items-center justify-center">
          <div class="text-center space-y-4">
            <Shield class="w-16 h-16 text-red-500 mx-auto" />
            <h2 class="text-2xl font-bold text-slate-900 dark:text-white">
              غير مخول للوصول
            </h2>
            <p class="text-slate-600 dark:text-slate-400">
              هذه الصفحة خاصة بالمديرين فقط
            </p>
            <router-link
              to="/"
              class="inline-block bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded-lg font-medium transition-colors"
            >
              العودة للرئيسية
            </router-link>
          </div>
        </div>

        <!-- Admin Dashboard -->
        <div v-else>
          <!-- Header -->
          <div class="mb-8">
            <h1 class="text-3xl lg:text-4xl font-bold text-slate-900 dark:text-white mb-4">
              لوحة تحكم المدير
            </h1>
            <p class="text-lg text-slate-600 dark:text-slate-400">
              إدارة المنصة والمحتوى والمستخدمين
            </p>
          </div>

          <!-- Tabs -->
          <div class="mb-8">
            <div class="border-b border-slate-200 dark:border-slate-700">
              <nav class="flex space-x-8 rtl:space-x-reverse">
                <button
                  v-for="tab in tabs"
                  :key="tab.id"
                  @click="handleTabChange(tab.id)"
                  :class="[
                    'flex items-center space-x-2 rtl:space-x-reverse py-4 px-1 border-b-2 font-medium text-sm transition-colors',
                    activeTab === tab.id
                      ? 'border-purple-500 text-purple-600 dark:text-purple-400'
                      : 'border-transparent text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-300'
                  ]"
                >
                  <component :is="tab.icon" class="w-4 h-4" />
                  <span>{{ tab.label }}</span>
                </button>
              </nav>
            </div>
          </div>

          <!-- Tab Content -->
          <div class="space-y-8">
            <!-- Dashboard Overview -->
            <div v-if="activeTab === 'dashboard'" class="space-y-8">
              <!-- Stats Cards -->
              <div v-if="loading" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <div v-for="i in 4" :key="i" class="card p-6 animate-pulse">
                  <div class="bg-slate-200 dark:bg-slate-700 h-8 w-16 rounded mb-2"></div>
                  <div class="bg-slate-200 dark:bg-slate-700 h-4 w-24 rounded"></div>
                </div>
              </div>

              <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <div class="card p-6">
                  <div class="flex items-center space-x-3 rtl:space-x-reverse">
                    <div class="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                      <BookOpen class="w-6 h-6 text-blue-600 dark:text-blue-400" />
                    </div>
                    <div>
                      <p class="text-2xl font-bold text-slate-900 dark:text-white">
                        {{ dashboardStats.totalCourses || 0 }}
                      </p>
                      <p class="text-sm text-slate-600 dark:text-slate-400">
                        إجمالي الكورسات
                      </p>
                    </div>
                  </div>
                </div>

                <div class="card p-6">
                  <div class="flex items-center space-x-3 rtl:space-x-reverse">
                    <div class="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-lg flex items-center justify-center">
                      <Users class="w-6 h-6 text-green-600 dark:text-green-400" />
                    </div>
                    <div>
                      <p class="text-2xl font-bold text-slate-900 dark:text-white">
                        {{ dashboardStats.totalUsers || 0 }}
                      </p>
                      <p class="text-sm text-slate-600 dark:text-slate-400">
                        إجمالي المستخدمين
                      </p>
                    </div>
                  </div>
                </div>

                <div class="card p-6">
                  <div class="flex items-center space-x-3 rtl:space-x-reverse">
                    <div class="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-lg flex items-center justify-center">
                      <Gift class="w-6 h-6 text-purple-600 dark:text-purple-400" />
                    </div>
                    <div>
                      <p class="text-2xl font-bold text-slate-900 dark:text-white">
                        {{ dashboardStats.freeCourses || 0 }}
                      </p>
                      <p class="text-sm text-slate-600 dark:text-slate-400">
                        كورسات مجانية
                      </p>
                    </div>
                  </div>
                </div>

                <div class="card p-6">
                  <div class="flex items-center space-x-3 rtl:space-x-reverse">
                    <div class="w-12 h-12 bg-yellow-100 dark:bg-yellow-900/30 rounded-lg flex items-center justify-center">
                      <DollarSign class="w-6 h-6 text-yellow-600 dark:text-yellow-400" />
                    </div>
                    <div>
                      <p class="text-2xl font-bold text-slate-900 dark:text-white">
                        {{ dashboardStats.paidCourses || 0 }}
                      </p>
                      <p class="text-sm text-slate-600 dark:text-slate-400">
                        كورسات مدفوعة
                      </p>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Analytics Row (Line + Donut) -->
              <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <!-- Performance Overview (Line) -->
                <div class="card p-6 lg:col-span-2">
                  <div class="flex items-center justify-between mb-4">
                    <div>
                      <h3 class="text-lg font-semibold text-slate-900 dark:text-white">Performance Overview</h3>
                      <p class="text-sm text-slate-500 dark:text-slate-400">تسجيلات {{ rangeLabel }}</p>
                    </div>
                    <div class="flex items-center gap-2">
                      <select v-model="range"
                        @change="fetchTrend"
                        class="px-3 py-2 rounded-lg border border-slate-300 dark:border-slate-600 bg-white dark:bg-slate-800 text-slate-700 dark:text-slate-200">
                        <option value="7d">آخر 7 أيام</option>
                        <option value="30d">آخر 30 يوم</option>
                        <option value="90d">آخر 90 يوم</option>
                      </select>
                      <button @click="refreshAll"
                        class="px-3 py-2 rounded-lg bg-slate-900 text-white dark:bg-slate-100 dark:text-slate-900 hover:opacity-90 transition">
                        تحديث
                      </button>
                    </div>
                  </div>

                  <div v-if="trendLoading" class="h-56 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse"></div>
                  <div v-else-if="trend.data.length > 0" class="h-56">
                    <LineApex :labels="trend.labels" :series="trend.data" />
                  </div>
                  <div v-else class="h-56 flex items-center justify-center text-slate-500 dark:text-slate-400">
                    لا توجد بيانات
                  </div>
                </div>

                <!-- Customers Activity (Donut) -->
                <div class="card p-6">
                  <div class="flex items-center justify-between mb-4">
                    <h3 class="text-lg font-semibold text-slate-900 dark:text-white">Customers Activity</h3>
                    <button @click="fetchDistribution" class="text-sm text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200">تحديث</button>
                  </div>

                  <div v-if="distributionLoading" class="h-56 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse"></div>
                  <div v-else-if="distribution.data.length > 0" class="h-56">
                    <DonutApex :labels="distribution.labels" :series="distribution.data" :colors="donutColors" />
                    <div class="mt-4 grid grid-cols-2 gap-2">
                      <div v-for="(label, i) in distribution.labels" :key="label" class="flex items-center justify-between text-sm">
                        <span class="flex items-center gap-2">
                          <span class="inline-block w-3 h-3 rounded" :style="{ backgroundColor: donutColors[i % donutColors.length] }"></span>
                          {{ label }}
                        </span>
                        <span class="font-medium">{{ distribution.data[i] }}</span>
                      </div>
                    </div>
                  </div>
                  <div v-else class="h-56 flex items-center justify-center text-slate-500 dark:text-slate-400">
                    لا توجد بيانات
                  </div>
                </div>
              </div>

              <!-- Top Courses + Recent Activity -->
              <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <!-- Top Courses -->
                <div class="card p-6 lg:col-span-2">
                  <div class="flex items-center justify-between mb-4">
                    <h3 class="text-lg font-semibold text-slate-900 dark:text-white">Top Courses</h3>
                    <button @click="fetchTopCourses" class="text-sm text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200">تحديث</button>
                  </div>

                  <div v-if="topLoading" class="h-40 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse"></div>
                  <div v-else-if="topCourses.length > 0" class="overflow-x-auto">
                    <table class="w-full">
                      <thead class="bg-slate-50 dark:bg-slate-800/50">
                        <tr>
                          <th class="px-4 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase">الكورس</th>
                          <th class="px-4 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase">الطلاب</th>
                          <th class="px-4 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase">الإيراد</th>
                          <th class="px-4 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase">الحالة</th>
                        </tr>
                      </thead>
                      <tbody class="divide-y divide-slate-200 dark:divide-slate-700">
                        <tr v-for="row in topCourses" :key="row.courseId" class="hover:bg-slate-50 dark:hover:bg-slate-800/50">
                          <td class="px-4 py-3">
                            <div class="font-medium text-slate-900 dark:text-white">{{ row.title }}</div>
                            <div class="text-xs text-slate-500">{{ row.category || '—' }}</div>
                          </td>
                          <td class="px-4 py-3">{{ row.students ?? 0 }}</td>
                          <td class="px-4 py-3">{{ formatRevenue(row.revenue) }}</td>
                          <td class="px-4 py-3">
                            <span :class="['px-2 py-1 rounded-full text-xs font-medium', row.status === 'Active' ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-slate-100 text-slate-700 dark:bg-slate-800/50 dark:text-slate-300']">
                              {{ row.status || '—' }}
                            </span>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                  <div v-else class="h-40 flex items-center justify-center text-slate-500 dark:text-slate-400">
                    لا توجد بيانات
                  </div>
                </div>

                <!-- Recent Activity -->
                <div class="card p-6">
                  <div class="flex items-center justify-between mb-4">
                    <h3 class="text-lg font-semibold text-slate-900 dark:text-white">Recent Activity</h3>
                    <button @click="fetchRecentActivity" class="text-sm text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200">تحديث</button>
                  </div>

                  <div v-if="activityLoading" class="h-40 rounded-xl bg-slate-100 dark:bg-slate-800 animate-pulse"></div>
                  <div v-else-if="recentActivity.length > 0" class="space-y-3">
                    <div v-for="item in recentActivity" :key="item.id || (item.type + item.at)" class="p-3 rounded-lg bg-slate-50 dark:bg-slate-800/50">
                      <div class="flex items-center justify-between">
                        <div class="text-sm">
                          <span v-if="item.type === 'ENROLL'">سجّل <b>{{ item.user }}</b> في <b>{{ item.course }}</b></span>
                          <span v-else-if="item.type === 'COURSE_CREATE'">أُضيف كورس <b>{{ item.course }}</b></span>
                          <span v-else>{{ item.message || 'نشاط' }}</span>
                        </div>
                        <div class="text-xs text-slate-500">{{ formatRelative(item.at) }}</div>
                      </div>
                    </div>
                  </div>
                  <div v-else class="h-40 flex items-center justify-center text-slate-500 dark:text-slate-400">
                    لا توجد بيانات
                  </div>
                </div>
              </div>

              <!-- Recent Courses -->
              <div class="card p-6">
                <h3 class="text-lg font-semibold text-slate-900 dark:text-white mb-4">
                  آخر الكورسات المضافة
                </h3>
                <div v-if="dashboardStats.recentCourses?.length > 0" class="space-y-3">
                  <div
                    v-for="course in dashboardStats.recentCourses"
                    :key="course.id"
                    class="flex items-center justify-between p-3 bg-slate-50 dark:bg-slate-800/50 rounded-lg"
                  >
                    <div>
                      <h4 class="font-medium text-slate-900 dark:text-white">
                        {{ course.title }}
                      </h4>
                      <p class="text-sm text-slate-600 dark:text-slate-400">
                        {{ course.instructor }} • {{ formatDate(course.createdAt) }}
                      </p>
                    </div>
                    <span :class="[
                      'px-2 py-1 rounded-full text-xs font-medium',
                      course.isFree
                        ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400'
                        : 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400'
                    ]">
                      {{ course.isFree ? 'مجاني' : 'مدفوع' }}
                    </span>
                  </div>
                </div>
                <div v-else class="text-center py-8 text-slate-500 dark:text-slate-400">
                  لا توجد كورسات
                </div>
              </div>
            </div>

            <!-- Courses Management -->
            <div v-else-if="activeTab === 'courses'" class="space-y-6">
              <div class="flex justify-between items-center">
                <h2 class="text-2xl font-bold text-slate-900 dark:text-white">
                  إدارة الكورسات
                </h2>
                <button
                  @click="showCourseModal = true; editingCourse = null"
                  class="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-lg font-medium transition-colors flex items-center space-x-2 rtl:space-x-reverse"
                >
                  <Plus class="w-4 h-4" />
                  <span>إضافة كورس جديد</span>
                </button>
              </div>

              <div class="card overflow-hidden">
                <div v-if="coursesLoading" class="p-6">
                  <div class="animate-pulse space-y-4">
                    <div v-for="i in 5" :key="i" class="flex items-center space-x-4 rtl:space-x-reverse">
                      <div class="bg-slate-200 dark:bg-slate-700 h-4 w-1/4 rounded"></div>
                      <div class="bg-slate-200 dark:bg-slate-700 h-4 w-1/3 rounded"></div>
                      <div class="bg-slate-200 dark:bg-slate-700 h-4 w-1/6 rounded"></div>
                    </div>
                  </div>
                </div>
                
                <div v-else class="overflow-x-auto">
                  <table class="w-full">
                    <thead class="bg-slate-50 dark:bg-slate-800/50">
                      <tr>
                        <th class="px-6 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                          العنوان
                        </th>
                        <th class="px-6 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                          المدرب
                        </th>
                        <th class="px-6 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                          السعر
                        </th>
                        <th class="px-6 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                          الموقع
                        </th>
                        <th class="px-6 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                          العمليات
                        </th>
                      </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-200 dark:divide-slate-700">
                      <tr v-for="course in adminCourses" :key="course.id" class="hover:bg-slate-50 dark:hover:bg-slate-800/50">
                        <td class="px-6 py-4 whitespace-nowrap">
                          <div>
                            <div class="text-sm font-medium text-slate-900 dark:text-white">
                              {{ course.title }}
                            </div>
                            <div class="text-sm text-slate-500 dark:text-slate-400">
                              {{ course.duration }} ساعة
                            </div>
                          </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900 dark:text-white">
                          {{ course.instructor }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                          <span v-if="course.isFree" class="text-green-600 dark:text-green-400 font-medium">
                            مجاني
                          </span>
                          <span v-else class="text-slate-900 dark:text-white">
                            {{ course.price }} ريال
                          </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900 dark:text-white">
                          {{ course.location }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2 rtl:space-x-reverse">
                          <button
                            @click="editCourse(course)"
                            class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300"
                          >
                            <Edit class="w-4 h-4" />
                          </button>
                          <button
                            @click="deleteCourse(course.id)"
                            class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                          >
                            <Trash class="w-4 h-4" />
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>

            <!-- Users Management -->
            <div v-else-if="activeTab === 'users'" class="space-y-6">
              <h2 class="text-2xl font-bold text-slate-900 dark:text-white">
                إدارة المستخدمين
              </h2>

              <div class="card overflow-hidden">
                <div v-if="usersLoading" class="p-6">
                  <div class="animate-pulse space-y-4">
                    <div v-for="i in 5" :key="i" class="flex items-center space-x-4 rtl:space-x-reverse">
                      <div class="bg-slate-200 dark:bg-slate-700 h-4 w-1/4 rounded"></div>
                      <div class="bg-slate-200 dark:bg-slate-700 h-4 w-1/3 rounded"></div>
                      <div class="bg-slate-200 dark:bg-slate-700 h-4 w-1/6 rounded"></div>
                    </div>
                  </div>
                </div>
                
                <div v-else class="overflow-x-auto">
                  <table class="w-full">
                    <thead class="bg-slate-50 dark:bg-slate-800/50">
                      <tr>
                        <th class="px-6 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                          الاسم
                        </th>
                        <th class="px-6 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                          البريد الإلكتروني
                        </th>
                        <th class="px-6 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                          الدور
                        </th>
                        <th class="px-6 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                          تاريخ التسجيل
                        </th>
                        <th class="px-6 py-3 text-right rtl:text-left text-xs font-medium text-slate-500 dark:text-slate-400 uppercase tracking-wider">
                          العمليات
                        </th>
                      </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-200 dark:divide-slate-700">
                      <tr v-for="user in adminUsers" :key="user.id" class="hover:bg-slate-50 dark:hover:bg-slate-800/50">
                        <td class="px-6 py-4 whitespace-nowrap">
                          <div class="flex items-center space-x-3 rtl:space-x-reverse">
                            <div class="w-8 h-8 bg-gradient-to-br from-blue-500 to-blue-600 rounded-full flex items-center justify-center text-white text-sm font-semibold">
                              {{ (user.name?.[0] || 'U').toUpperCase() }}
                            </div>
                            <div class="text-sm font-medium text-slate-900 dark:text-white">
                              {{ user.name || '—' }}
                            </div>
                          </div>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900 dark:text-white">
                          {{ user.email || '—' }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap">
                          <span :class="[
                            'px-2 py-1 rounded-full text-xs font-medium',
                            (user.role === 'ADMIN' || user.role === 'ROLE_ADMIN')
                              ? 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
                              : 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400'
                          ]">
                            {{ (user.role === 'ADMIN' || user.role === 'ROLE_ADMIN') ? 'مدير' : 'مستخدم' }}
                          </span>
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-900 dark:text-white">
                          {{ formatDate(user.createdAt) }}
                        </td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2 rtl:space-x-reverse">
                          <button
                            v-if="!(user.role === 'ADMIN' || user.role === 'ROLE_ADMIN')"
                            @click="promoteToAdmin(user.id)"
                            class="text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300"
                            title="ترقية لمدير"
                          >
                            <UserPlus class="w-4 h-4" />
                          </button>
                          <button
                            v-if="(user.role === 'ADMIN' || user.role === 'ROLE_ADMIN') && user.id !== authStore.user?.id"
                            @click="demoteFromAdmin(user.id)"
                            class="text-orange-600 hover:text-orange-900 dark:text-orange-400 dark:hover:text-orange-300"
                            title="إلغاء الإدارة"
                          >
                            <UserMinus class="w-4 h-4" />
                          </button>
                          <button
                            v-if="user.id !== authStore.user?.id"
                            @click="deleteUser(user.id)"
                            class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                          >
                            <Trash class="w-4 h-4" />
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div> <!-- /Tab Content -->
        </div>
      </div>
    </div>

    <!-- Course Modal -->
    <div v-if="showCourseModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white dark:bg-slate-800 rounded-lg p-6 w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-slate-900 dark:text-white">
            {{ editingCourse ? 'تعديل الكورس' : 'إضافة كورس جديد' }}
          </h3>
          <button @click="showCourseModal = false" class="text-slate-400 hover:text-slate-600">
            <X class="w-6 h-6" />
          </button>
        </div>

        <form @submit.prevent="saveCourse" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
              عنوان الكورس *
            </label>
            <input
              v-model="courseForm.title"
              type="text"
              required
              class="input-field"
              placeholder="عنوان الكورس"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
              الوصف *
            </label>
            <textarea
              v-model="courseForm.description"
              required
              rows="3"
              class="input-field resize-none"
              placeholder="وصف الكورس"
            ></textarea>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                المدرب *
              </label>
              <input
                v-model="courseForm.instructor"
                type="text"
                required
                class="input-field"
                placeholder="اسم المدرب"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                المدة (ساعة) *
              </label>
              <input
                v-model="courseForm.duration"
                type="number"
                required
                min="1"
                class="input-field"
                placeholder="المدة بالساعات"
              />
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                الموقع *
              </label>
              <input
                v-model="courseForm.location"
                type="text"
                required
                class="input-field"
                placeholder="موقع الكورس"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
                رابط الصورة
              </label>
              <input
                v-model="courseForm.imageUrl"
                type="url"
                class="input-field"
                placeholder="https://example.com/image.jpg"
              />
            </div>
          </div>

          <div>
            <label class="flex items-center space-x-2 rtl:space-x-reverse">
              <input
                v-model="courseForm.isFree"
                type="checkbox"
                class="rounded border-slate-300 text-purple-600 focus:ring-purple-500"
              />
              <span class="text-sm font-medium text-slate-700 dark:text-slate-300">
                كورس مجاني
              </span>
            </label>
          </div>

          <div v-if="!courseForm.isFree">
            <label class="block text-sm font-medium text-slate-700 dark:text-slate-300 mb-1">
              السعر (ريال) *
            </label>
            <input
              v-model="courseForm.price"
              type="number"
              :required="!courseForm.isFree"
              min="0"
              step="0.01"
              class="input-field"
              placeholder="السعر بالريال"
            />
          </div>

          <div class="flex space-x-4 rtl:space-x-reverse pt-4">
            <button
              type="submit"
              :disabled="courseSubmitting"
              class="flex-1 bg-purple-600 hover:bg-purple-700 disabled:bg-purple-400 text-white px-4 py-2 rounded-lg font-medium transition-colors"
            >
              {{ courseSubmitting ? 'جاري الحفظ...' : (editingCourse ? 'تحديث' : 'إضافة') }}
            </button>
            <button
              type="button"
              @click="showCourseModal = false"
              class="flex-1 btn-secondary"
            >
              إلغاء
            </button>
          </div>
        </form>
      </div>
    </div>
  </Layout>
</template>

<script>
import { ref, reactive, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { adminService } from '@/services/api'
import { useToast } from 'vue-toastification'
import { 
  Shield, 
  BarChart, 
  BookOpen, 
  Users, 
  Settings,
  Gift,
  DollarSign,
  Plus,
  Edit,
  Trash,
  UserPlus,
  UserMinus,
  X
} from 'lucide-vue-next'
import Layout from '@/components/Layout.vue'
import { normalizeTrend, normalizeDistribution } from '@/utils/normalize'
import LineApex from '@/components/charts/LineApex.vue'
import DonutApex from '@/components/charts/DonutApex.vue'




export default {
  name: 'AdminDashboard',
  components: {
    Layout,
    Shield, 
    BarChart, 
    BookOpen, 
    Users, 
    Settings,
    Gift,
    DollarSign,
    Plus,
    Edit,
    Trash,
    UserPlus,
    UserMinus,
    X,
    LineApex,
    DonutApex
  },
  setup() {
    const authStore = useAuthStore()
    const toast = useToast()
    
    const activeTab = ref('dashboard')
    const loading = ref(true)
    const coursesLoading = ref(false)
    const usersLoading = ref(false)
    const courseSubmitting = ref(false)
    
    const dashboardStats = ref({})
    const adminCourses = ref([])
    const adminUsers = ref([])
    
    const showCourseModal = ref(false)
    const editingCourse = ref(null)
    
    const courseForm = reactive({
      title: '',
      description: '',
      instructor: '',
      duration: '',
      location: '',
      price: '',
      isFree: false,
      imageUrl: ''
    })
    
    const tabs = [
      { id: 'dashboard', label: 'نظرة عامة', icon: BarChart },
      { id: 'courses', label: 'إدارة الكورسات', icon: BookOpen },
      { id: 'users', label: 'إدارة المستخدمين', icon: Users },
      { id: 'settings', label: 'إعدادات', icon: Settings }
    ]

    // ======= Analytics State =======
    const range = ref('30d')
    const trendLoading = ref(false)
    const distributionLoading = ref(false)
    const topLoading = ref(false)
    const activityLoading = ref(false)

    const trend = ref({ labels: [], data: [] })
    const distribution = ref({ labels: [], data: [] })
    const topCourses = ref([])
    const recentActivity = ref([])

    const donutColors = ['#6366f1','#f59e0b','#10b981','#8b5cf6','#ef4444','#14b8a6']
    const rangeLabel = computed(()=> range.value === '7d' ? 'آخر 7 أيام' : range.value === '90d' ? 'آخر 90 يوم' : 'آخر 30 يوم')

    // ======= Existing fetchers =======
    const fetchDashboardStats = async () => {
      try {
        const response = await adminService.getDashboardStats()
        dashboardStats.value = response.data
      } catch (error) {
        console.error('Error fetching dashboard stats:', error)
        toast.error('حدث خطأ في جلب إحصائيات اللوحة')
      } finally {
        loading.value = false
      }
    }
    
    const fetchAdminCourses = async () => {
      coursesLoading.value = true
      try {
        const response = await adminService.getAllCoursesAdmin()
        adminCourses.value = response.data
      } catch (error) {
        console.error('Error fetching admin courses:', error)
        toast.error('حدث خطأ في جلب الكورسات')
      } finally {
        coursesLoading.value = false
      }
    }
    
    const fetchAdminUsers = async () => {
      usersLoading.value = true
      try {
        const response = await adminService.getAllUsersAdmin()
        adminUsers.value = response.data
      } catch (error) {
        console.error('Error fetching admin users:', error)
        toast.error('حدث خطأ في جلب المستخدمين')
      } finally {
        usersLoading.value = false
      }
    }

    // ======= Analytics Fetchers =======
    const fetchTrend = async () => {
      trendLoading.value = true
      try {
        if (typeof adminService.getEnrollmentsTrend === 'function') {
          const res = await adminService.getEnrollmentsTrend(range.value) // <-- رقم/سترنج بسيط
          trend.value = normalizeTrend(res.data)
        } else {
          trend.value = { labels: [], data: [] }
        }
      } catch (e) {
        console.error(e)
        trend.value = { labels: [], data: [] }
      } finally {
        trendLoading.value = false
      }
    }

    const fetchDistribution = async () => {
      distributionLoading.value = true
      try {
        if (typeof adminService.getCourseDistribution === 'function') {
          const res = await adminService.getCourseDistribution()
          distribution.value = normalizeDistribution(res.data)
        } else {
          distribution.value = { labels: [], data: [] }
        }
      } catch (e) {
        console.error(e)
        distribution.value = { labels: [], data: [] }
      } finally {
        distributionLoading.value = false
      }
    }

    const fetchTopCourses = async () => {
      topLoading.value = true
      try {
        if (typeof adminService.getTopCourses === 'function') {
          const res = await adminService.getTopCourses(5) // <-- مهم: رقم، مو object
          topCourses.value = res.data || []
        } else {
          topCourses.value = []
        }
      } catch (e) {
        console.error(e)
        topCourses.value = []
      } finally {
        topLoading.value = false
      }
    }

    const fetchRecentActivity = async () => {
      activityLoading.value = true
      try {
        if (typeof adminService.getRecentActivity === 'function') {
          const res = await adminService.getRecentActivity(8) // <-- مهم: رقم، مو object
          recentActivity.value = res.data || []
        } else {
          recentActivity.value = []
        }
      } catch (e) {
        console.error(e)
        recentActivity.value = []
      } finally {
        activityLoading.value = false
      }
    }

    const refreshAll = async () => {
      await Promise.all([fetchTrend(), fetchDistribution(), fetchTopCourses(), fetchRecentActivity()])
    }
    
    const editCourse = (course) => {
      editingCourse.value = course
      Object.assign(courseForm, {
        title: course.title || '',
        description: course.description || '',
        instructor: course.instructor || '',
        duration: course.duration || '',
        location: course.location || '',
        price: course.price || '',
        isFree: !!course.isFree,
        imageUrl: course.imageUrl || ''
      })
      showCourseModal.value = true
    }
    
    const saveCourse = async () => {
      courseSubmitting.value = true
      try {
        const courseData = {
          ...courseForm,
          duration: parseInt(courseForm.duration, 10),
          price: courseForm.isFree ? 0 : parseFloat(courseForm.price || 0)
        }
        
        if (editingCourse.value) {
          await adminService.updateCourseAdmin(editingCourse.value.id, courseData)
          toast.success('تم تحديث الكورس بنجاح')
        } else {
          await adminService.createCourseAdmin(courseData)
          toast.success('تم إضافة الكورس بنجاح')
        }
        
        showCourseModal.value = false
        resetCourseForm()
        await fetchAdminCourses()
        await fetchDashboardStats()
        await refreshAll()
      } catch (error) {
        console.error('Error saving course:', error)
        toast.error('حدث خطأ في حفظ الكورس')
      } finally {
        courseSubmitting.value = false
      }
    }
    
    const deleteCourse = async (courseId) => {
      if (!confirm('هل أنت متأكد من حذف هذا الكورس؟')) return
      
      try {
        await adminService.deleteCourseAdmin(courseId)
        toast.success('تم حذف الكورس بنجاح')
        await fetchAdminCourses()
        await fetchDashboardStats()
        await refreshAll()
      } catch (error) {
        console.error('Error deleting course:', error)
        toast.error('حدث خطأ في حذف الكورس')
      }
    }
    
    const promoteToAdmin = async (userId) => {
      if (!confirm('هل أنت متأكد من ترقية هذا المستخدم لمدير؟')) return
      
      try {
        await adminService.updateUserRole(userId, 'ADMIN')
        toast.success('تم ترقية المستخدم لمدير بنجاح')
        await fetchAdminUsers()
        await fetchDashboardStats()
      } catch (error) {
        console.error('Error promoting user:', error)
        toast.error('حدث خطأ في ترقية المستخدم')
      }
    }
    
    const demoteFromAdmin = async (userId) => {
      if (!confirm('هل أنت متأكد من إلغاء صلاحيات المدير لهذا المستخدم؟')) return
      
      try {
        await adminService.updateUserRole(userId, 'USER')
        toast.success('تم إلغاء صلاحيات المدير بنجاح')
        await fetchAdminUsers()
        await fetchDashboardStats()
      } catch (error) {
        console.error('Error demoting user:', error)
        toast.error('حدث خطأ في إلغاء صلاحيات المدير')
      }
    }
    
    const deleteUser = async (userId) => {
      if (!confirm('هل أنت متأكد من حذف هذا المستخدم؟ هذا الإجراء لا يمكن التراجع عنه.')) return
      
      try {
        await adminService.deleteUserAdmin(userId)
        toast.success('تم حذف المستخدم بنجاح')
        await fetchAdminUsers()
        await fetchDashboardStats()
      } catch (error) {
        console.error('Error deleting user:', error)
        toast.error('حدث خطأ في حذف المستخدم')
      }
    }
    
    const resetCourseForm = () => {
      Object.assign(courseForm, {
        title: '',
        description: '',
        instructor: '',
        duration: '',
        location: '',
        price: '',
        isFree: false,
        imageUrl: ''
      })
      editingCourse.value = null
    }
    
    const formatDate = (dateString) => {
      if (!dateString) return '—'
      const d = new Date(dateString)
      if (isNaN(d.getTime())) return '—'
      return d.toLocaleDateString('ar-SA', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    const formatRevenue = (rev) => {
      if (rev == null) return '—'
      const n = typeof rev === 'number' ? rev : Number(rev)
      if (!Number.isFinite(n)) return '—'
      return n.toLocaleString('ar-SA')
    }

    const formatRelative = (iso) => {
      if (!iso) return '—'
      const now = new Date()
      const t = new Date(iso)
      const diff = Math.floor((now - t) / 1000)
      if (diff < 60) return `${diff}s`
      const m = Math.floor(diff/60)
      if (m < 60) return `${m}m`
      const h = Math.floor(m/60)
      if (h < 24) return `${h}h`
      const d = Math.floor(h/24)
      return `${d}d`
    }
    
    // تحميل حسب التاب
    const handleTabChange = async (newTab) => {
      activeTab.value = newTab
      if (newTab === 'courses' && adminCourses.value.length === 0) {
        await fetchAdminCourses()
      } else if (newTab === 'users' && adminUsers.value.length === 0) {
        await fetchAdminUsers()
      } else if (newTab === 'dashboard' && Object.keys(dashboardStats.value || {}).length === 0) {
        await fetchDashboardStats()
        await refreshAll()
      }
    }
    
    onMounted(async () => {
      if (authStore.isAdmin) {
        await fetchDashboardStats()
        await refreshAll()
      }
    })
    
    return {
      authStore,
      activeTab,
      loading,
      coursesLoading,
      usersLoading,
      courseSubmitting,
      dashboardStats,
      adminCourses,
      adminUsers,
      showCourseModal,
      editingCourse,
      courseForm,
      tabs,
      editCourse,
      saveCourse,
      deleteCourse,
      promoteToAdmin,
      demoteFromAdmin,
      deleteUser,
      resetCourseForm,
      formatDate,
      formatRevenue,
      formatRelative,
      handleTabChange,

      // Analytics bindings
      range,
      rangeLabel,
      trendLoading,
      distributionLoading,
      topLoading,
      activityLoading,
      trend,
      distribution,
      topCourses,
      recentActivity,
      refreshAll,
      fetchTrend,
      fetchDistribution,
      fetchTopCourses,
      fetchRecentActivity,
      donutColors
    }
  }
}
</script>

<style scoped>
.input-field {
  border: 1px solid #d1d5db;
  border-radius: 0.375rem;
  padding: 0.5rem 0.75rem;
  width: 100%;
  font-size: 1rem;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.input-field:focus {
  outline: none;
  border-color: #7c3aed;
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.4);
}
.dark .input-field {
  background-color: #1e293b;
  border-color: #475569;
  color: white;
}
.btn-secondary {
  background-color: #f1f5f9;
  color: #475569;
  border: 1px solid #d1d5db;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  font-weight: 500;
  transition: all 0.2s;
}
.btn-secondary:hover {
  background-color: #e2e8f0;
}
.dark .btn-secondary {
  background-color: #334155;
  color: #e2e8f0;
  border-color: #475569;
}
.dark .btn-secondary:hover {
  background-color: #475569;
}
.card { @apply bg-white dark:bg-slate-800 rounded-xl shadow-sm; }
</style>
