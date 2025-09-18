<template>
  <nav class="navbar">
    <div class="nav-container">
      <!-- Brand/Logo -->
      <div class="nav-brand">
        <router-link to="/" class="brand-link">
          <span class="brand-icon">🎓</span>
          <span class="brand-text">EduHub</span>
        </router-link>
      </div>
      
      <!-- Navigation Links -->
      <div class="nav-links">
        <router-link to="/" class="nav-link">Home</router-link>
        <router-link to="/courses" class="nav-link">Courses</router-link>
        <router-link to="/about" class="nav-link">About</router-link>
        <!-- زر الخريطة -->
        <router-link to="/map" class="nav-link">عرض على الخريطة 🗺️</router-link>
      </div>
      
      <!-- Actions -->
      <div class="nav-actions">
        <!-- Theme Toggle -->
        <button @click="toggleTheme" class="theme-btn" title="Toggle theme">
          {{ isDark ? '☀️' : '🌙' }}
        </button>
        
        <!-- Auth Buttons -->
        <div class="auth-buttons">
          <router-link to="/login" class="btn-outline">Login</router-link>
          <router-link to="/register" class="btn-primary">Sign Up</router-link>
        </div>
      </div>
      
      <!-- Mobile Menu Button -->
      <button class="mobile-menu-btn" @click="toggleMobileMenu">
        <span></span>
        <span></span>
        <span></span>
      </button>
    </div>
    
    <!-- Mobile Menu -->
    <div class="mobile-menu" :class="{ active: mobileMenuOpen }">
      <router-link to="/" class="mobile-link" @click="closeMobileMenu">Home</router-link>
      <router-link to="/courses" class="mobile-link" @click="closeMobileMenu">Courses</router-link>
      <router-link to="/about" class="mobile-link" @click="closeMobileMenu">About</router-link>
      <!-- زر الخريطة للموبايل -->
      <router-link to="/map" class="mobile-link" @click="closeMobileMenu">عرض على الخريطة 🗺️</router-link>
      <div class="mobile-auth">
        <router-link to="/login" class="btn-outline" @click="closeMobileMenu">Login</router-link>
        <router-link to="/register" class="btn-primary" @click="closeMobileMenu">Sign Up</router-link>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue'

const isDark = ref(false)
const mobileMenuOpen = ref(false)

const toggleTheme = () => {
  isDark.value = !isDark.value
  document.documentElement.setAttribute('data-theme', isDark.value ? 'dark' : 'light')
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const closeMobileMenu = () => {
  mobileMenuOpen.value = false
}
</script>

<style scoped>
.navbar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

/* Brand */
.nav-brand .brand-link {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #1f2937;
  font-weight: 700;
  font-size: 1.5rem;
}

.brand-icon {
  margin-right: 0.5rem;
  font-size: 1.8rem;
}

.brand-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* Navigation Links */
.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-link {
  text-decoration: none;
  color: #6b7280;
  font-weight: 500;
  transition: color 0.2s;
  position: relative;
}

.nav-link:hover {
  color: #4f46e5;
}

.nav-link.router-link-active {
  color: #4f46e5;
}

.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  right: 0;
  height: 2px;
  background: #4f46e5;
}

/* Actions */
.nav-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.theme-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 8px;
  transition: background 0.2s;
}

.theme-btn:hover {
  background: #f3f4f6;
}

.auth-buttons {
  display: flex;
  gap: 0.75rem;
}

.btn-outline {
  border: 2px solid #4f46e5;
  color: #4f46e5;
  padding: 8px 16px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.btn-outline:hover {
  background: #4f46e5;
  color: white;
}

.btn-primary {
  background: #4f46e5;
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: background 0.2s;
}

.btn-primary:hover {
  background: #3730a3;
}

/* Mobile Menu Button */
.mobile-menu-btn {
  display: none;
  flex-direction: column;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  gap: 3px;
}

.mobile-menu-btn span {
  width: 20px;
  height: 2px;
  background: #4f46e5;
  transition: all 0.3s;
}

/* Mobile Menu */
.mobile-menu {
  display: none;
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  padding: 1rem;
  transform: translateY(-100%);
  opacity: 0;
  transition: all 0.3s;
}

.mobile-menu.active {
  transform: translateY(0);
  opacity: 1;
}

.mobile-link {
  display: block;
  padding: 0.75rem 0;
  text-decoration: none;
  color: #6b7280;
  font-weight: 500;
  border-bottom: 1px solid #f3f4f6;
}

.mobile-link:hover {
  color: #4f46e5;
}

.mobile-auth {
  display: flex;
  gap: 0.75rem;
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #f3f4f6;
}

/* Responsive */
@media (max-width: 768px) {
  .nav-links,
  .auth-buttons {
    display: none;
  }
  
  .mobile-menu-btn {
    display: flex;
  }
  
  .mobile-menu {
    display: block;
  }
}
</style>
