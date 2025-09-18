<template>
  <transition name="fade">
    <div
      v-if="show"
      dir="rtl"
      class="relative h-screen w-screen overflow-hidden bg-[#0B0B10] text-white grid place-items-center"
    >
      <!-- خلفية تدرّج وهوية -->
      <div class="absolute inset-0">
        <div class="absolute inset-0 bg-gradient-to-br from-[#6D28D9] via-[#7C3AED] to-[#A855F7] opacity-90"></div>
        <div class="absolute inset-0 radial" />
        <div class="absolute inset-0 circuit-lines" />
        <div class="pointer-events-none absolute -bottom-24 -left-24 h-80 w-80 rounded-full bg-[#A855F7] blur-3xl opacity-25"></div>
        <div class="pointer-events-none absolute -top-24 -right-24 h-80 w-80 rounded-full bg-[#6D28D9] blur-3xl opacity-25"></div>
      </div>

      <!-- محتوى -->
      <div class="relative z-10 flex flex-col items-center gap-6 px-6">
        <!-- روبوت + تروس + ماسح -->
        <div class="relative w-[320px] h-[320px]">
          <!-- تروس خلفية -->
          <svg class="absolute inset-0" viewBox="0 0 320 320" aria-hidden="true">
            <g class="gear gear-lg" transform="translate(160,160)">
              <circle r="64" fill="none" stroke="rgba(255,255,255,.18)" stroke-width="6"/>
              <g fill="rgba(255,255,255,.18)">
                <template v-for="i in 12" :key="'lg-'+i">
                  <rect :transform="`rotate(${(i-1)*30}) translate(64,-8)`" x="-10" y="-10" width="28" height="20" rx="3" />
                </template>
              </g>
            </g>
            <g class="gear gear-sm" transform="translate(100,210)">
              <circle r="34" fill="none" stroke="rgba(255,255,255,.22)" stroke-width="5"/>
              <g fill="rgba(255,255,255,.22)">
                <template v-for="i in 10" :key="'sm-'+i">
                  <rect :transform="`rotate(${(i-1)*36}) translate(34,-6)`" x="-8" y="-8" width="20" height="16" rx="3" />
                </template>
              </g>
            </g>
            <g class="gear gear-sm reverse" transform="translate(230,90)">
              <circle r="34" fill="none" stroke="rgba(255,255,255,.22)" stroke-width="5"/>
              <g fill="rgba(255,255,255,.22)">
                <template v-for="i in 10" :key="'sm2-'+i">
                  <rect :transform="`rotate(${(i-1)*36}) translate(34,-6)`" x="-8" y="-8" width="20" height="16" rx="3" />
                </template>
              </g>
            </g>
          </svg>

          <!-- الروبوت -->
          <svg class="absolute inset-0" viewBox="0 0 320 320" role="img" aria-label="TechWay bot loading">
            <!-- ظل ناعم -->
            <ellipse cx="160" cy="250" rx="90" ry="14" fill="rgba(0,0,0,.28)" />
            <!-- جسم -->
            <g class="bot">
              <!-- رأس -->
              <rect x="95" y="80" width="130" height="110" rx="24" fill="white" opacity=".96"/>
              <!-- شريط علوي -->
              <rect x="95" y="80" width="130" height="18" rx="8" fill="#6D28D9" opacity=".95"/>
              <!-- أنتينـا -->
              <g class="antenna">
                <line x1="160" y1="65" x2="160" y2="80" stroke="#F4C84A" stroke-width="4" stroke-linecap="round"/>
                <circle cx="160" cy="60" r="8" fill="#F4C84A"/>
                <g class="spark">
                  <circle cx="160" cy="60" r="16" fill="none" stroke="#F4C84A" stroke-width="2" opacity=".55"/>
                  <circle cx="160" cy="60" r="24" fill="none" stroke="#F4C84A" stroke-width="2" opacity=".25"/>
                </g>
              </g>
              <!-- شاشة -->
              <rect x="110" y="105" width="100" height="60" rx="10" fill="#0B0B10" />
              <!-- ماسح -->
              <rect class="scanner" x="110" y="125" width="100" height="4" fill="#7C3AED"/>
              <!-- عيون -->
              <g class="eyes">
                <circle class="eye" cx="135" cy="135" r="7" fill="#F4C84A"/>
                <circle class="eye" cx="185" cy="135" r="7" fill="#F4C84A"/>
              </g>
              <!-- فم -->
              <rect x="140" y="152" width="40" height="6" rx="3" fill="#7C3AED" opacity=".8"/>
              <!-- جسم سفلي -->
              <rect x="120" y="190" width="80" height="18" rx="9" fill="white" opacity=".95"/>
              <!-- ذراعين -->
              <rect x="85" y="145" width="22" height="10" rx="5" fill="white" opacity=".9"/>
              <rect x="213" y="145" width="22" height="10" rx="5" fill="white" opacity=".9"/>
            </g>

            <!-- ماسح ليزر خارجي -->
            <rect class="scan-line" x="60" y="0" width="200" height="320" fill="url(#scanGrad)" opacity=".12"/>
            <defs>
              <linearGradient id="scanGrad" x1="0" x2="1">
                <stop offset="0" stop-color="#F4C84A" stop-opacity="0"/>
                <stop offset="0.5" stop-color="#F4C84A" stop-opacity="1"/>
                <stop offset="1" stop-color="#F4C84A" stop-opacity="0"/>
              </linearGradient>
            </defs>
          </svg>

          <!-- جزيئات -->
          <div class="particles pointer-events-none">
            <span v-for="n in 28" :key="n" class="p"></span>
          </div>
        </div>

        <!-- لوجو ونص -->
        <div class="text-center">
          <h1 class="text-4xl md:text-5xl font-extrabold tracking-tight">
            <span class="text-white">Tech</span><span class="text-[#F4C84A]">Way</span>
          </h1>
          <p class="mt-3 text-white/85 text-base md:text-lg">جاهزين نطلق التجربة التعليمية ✦</p>
        </div>

        <!-- لودر نسبة -->
        <div class="w-[280px]" aria-live="polite">
          <div class="h-2 w-full rounded-full bg-white/20 overflow-hidden">
            <div
              class="h-full rounded-full bg-gradient-to-r from-[#F4C84A] via-[#FFD362] to-[#F4C84A] glow"
              :style="{ width: progress + '%' }"
            />
          </div>
          <div class="mt-2 text-center text-sm font-semibold text-white/85">{{ Math.round(progress) }}%</div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const show = ref(false)
const progress = ref(0)

onMounted(() => {
  show.value = true
  sessionStorage.setItem('skipNextSplash', '1')

  // تقدّم سريع لكن ناعم
  const iv = setInterval(() => {
    const step = Math.random() * 14 + 10
    progress.value = Math.min(progress.value + step, 100)
    if (progress.value >= 100) clearInterval(iv)
  }, 150)

  // خروج بعد حركة الروبوت والماسح
  setTimeout(() => {
    router.replace({ name: 'Home' })
  }, 3200)
})
</script>

<style scoped>
/* انتقال */
.fade-enter-active, .fade-leave-active { transition: opacity .5s ease, transform .5s ease }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: translateY(8px) scale(.98) }

/* وهج بسيط لشريط التقدم */
.glow { box-shadow: 0 0 18px rgba(244,200,74,.55) }

/* شعور هويّة */
.radial {
  background: radial-gradient(1200px 600px at 50% 60%, rgba(255,255,255,.12), transparent 60%);
}

/* خطوط دائرة إلكترونية */
.circuit-lines:before, .circuit-lines:after {
  content:"";
  position:absolute; inset:0;
  background:
    repeating-linear-gradient(90deg, rgba(255,255,255,.08) 0 1px, transparent 1px 80px),
    repeating-linear-gradient(0deg, rgba(255,255,255,.06) 0 1px, transparent 1px 80px);
  mask-image: radial-gradient(circle at 50% 50%, black 45%, transparent 70%);
  animation: circuit-move 22s linear infinite;
}
.circuit-lines:after { animation-duration: 30s; opacity:.5 }
@keyframes circuit-move {
  0% { transform: translate(0,0) }
  100% { transform: translate(80px,80px) }
}

/* تروس */
.gear { animation: spin 9s linear infinite }
.gear-sm { animation-duration: 6s }
.reverse { animation-direction: reverse }
@keyframes spin { to { transform: translate(var(--tx,0), var(--ty,0)) rotate(360deg) } }

/* الروبوت */
.bot { animation: bot-bounce 2.2s ease-in-out infinite }
@keyframes bot-bounce {
  0%,100% { transform: translateY(0) }
  50% { transform: translateY(-8px) }
}

/* ماسح الشاشة */
.scanner { animation: scanner 1.6s ease-in-out infinite }
@keyframes scanner {
  0% { transform: translateY(0); opacity:.9 }
  50% { transform: translateY(20px); opacity:.6 }
  100% { transform: translateY(0); opacity:.9 }
}

/* عيون تومض */
.eye { transform-origin: center }
.eyes .eye {
  animation: blink 3.2s infinite;
}
.eyes .eye:nth-child(2) { animation-delay: .25s }
@keyframes blink {
  0%, 92%, 100% { transform: scaleY(1) }
  94% { transform: scaleY(.1) }
  96% { transform: scaleY(1) }
}

/* انتينا وشرار */
.antenna .spark { animation: spark 1.8s ease-in-out infinite }
@keyframes spark {
  0% { transform: scale(.6); opacity:.5 }
  50% { transform: scale(1); opacity:1 }
  100% { transform: scale(.6); opacity:.5 }
}

/* خط ماسح كبير */
.scan-line { animation: sweep 3.2s ease-in-out infinite }
@keyframes sweep {
  0% { transform: translateX(-260px) }
  50% { transform: translateX(0) }
  100% { transform: translateX(260px) }
}

/* جزيئات */
.particles { position:absolute; inset:0; }
.particles .p {
  position:absolute; width:6px; height:6px; border-radius:50%;
  background: rgba(255,255,255,.85);
  top: 50%; left: 50%;
  opacity:.0;
  animation: fly 2.8s ease-out infinite;
}
.particles .p:nth-child(n) { 
  --rx: calc((var(--i) - 14) * 10px);
  --ry: calc((var(--i) - 14) * 6px);
}
.particles .p { counter-reset: i var(--i); }
.particles .p { --i: 1 }
.particles .p:nth-child(1){--i:1}.particles .p:nth-child(2){--i:2}.particles .p:nth-child(3){--i:3}.particles .p:nth-child(4){--i:4}.particles .p:nth-child(5){--i:5}.particles .p:nth-child(6){--i:6}.particles .p:nth-child(7){--i:7}.particles .p:nth-child(8){--i:8}.particles .p:nth-child(9){--i:9}.particles .p:nth-child(10){--i:10}.particles .p:nth-child(11){--i:11}.particles .p:nth-child(12){--i:12}.particles .p:nth-child(13){--i:13}.particles .p:nth-child(14){--i:14}.particles .p:nth-child(15){--i:15}.particles .p:nth-child(16){--i:16}.particles .p:nth-child(17){--i:17}.particles .p:nth-child(18){--i:18}.particles .p:nth-child(19){--i:19}.particles .p:nth-child(20){--i:20}.particles .p:nth-child(21){--i:21}.particles .p:nth-child(22){--i:22}.particles .p:nth-child(23){--i:23}.particles .p:nth-child(24){--i:24}.particles .p:nth-child(25){--i:25}.particles .p:nth-child(26){--i:26}.particles .p:nth-child(27){--i:27}.particles .p:nth-child(28){--i:28}

@keyframes fly {
  0% { transform: translate(0,0) scale(.6); opacity:0 }
  10% { opacity:.9 }
  100% { transform: translate(calc(var(--rx) * 3), calc(var(--ry) * 3)) scale(0); opacity:0 }
}
</style>
