/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: {
        'inter': ['Inter', 'system-ui', 'sans-serif'],
        'arabic': ['Noto Sans Arabic', 'system-ui', 'sans-serif']
      },
      colors: {
        primary: {
          50: '#f5f3ff',     // بنفسجي فاتح جداً
          100: '#ede9fe',    // بنفسجي فاتح
          200: '#ddd6fe',    // بنفسجي فاتح متوسط
          300: '#c4b5fd',    // بنفسجي متوسط
          400: '#a78bfa',    // بنفسجي متوسط داكن
          500: '#8b5cf6',    // بنفسجي أساسي
          600: '#7c3aed',    // بنفسجي داكن
          700: '#6d28d9',    // بنفسجي داكن جداً
          800: '#5b21b6',    // بنفسجي غامق
          900: '#4c1d95',    // بنفسجي غامق جداً
        }
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out'
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' }
        },
        slideUp: {
          '0%': { transform: 'translateY(10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' }
        }
      }
    },
  },
  plugins: [],
}