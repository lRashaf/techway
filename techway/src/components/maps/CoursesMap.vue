<template>
  <Layout>
    <div class="min-h-screen">
      <!-- شريط أدوات أعلى المحتوى (تحت الهيدر) -->
      <div class="container mx-auto px-4 py-4 flex flex-wrap items-center gap-3">
        <div class="flex items-center gap-2">
          <button
            @click="goBack"
            class="px-3 py-2 rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-200 dark:hover:bg-slate-700"
            title="رجوع"
          >
            ← رجوع
          </button>

          <h1 class="text-2xl font-bold text-slate-900 dark:text-white">
            خريطة الكورسات الحضورية
          </h1>
        </div>

        <div class="ml-auto flex items-center gap-3">
          <label class="flex items-center gap-2 text-sm text-slate-700 dark:text-slate-200">
            <input type="checkbox" v-model="showPhysical" @change="renderMarkers" />
            حضوري (PHYSICAL)
          </label>
          <label class="flex items-center gap-2 text-sm text-slate-700 dark:text-slate-200">
            <input type="checkbox" v-model="showHybrid" @change="renderMarkers" />
            هجين (HYBRID)
          </label>

          <button
            @click="locateMe"
            class="px-3 py-2 rounded-lg bg-slate-900 text-white dark:bg-slate-100 dark:text-slate-900 hover:opacity-90"
          >
            موقعي الحالي
          </button>
          <button
            @click="fitAll"
            class="px-3 py-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-200 hover:bg-slate-200 dark:hover:bg-slate-700"
          >
            ملاءمة جميع النقاط
          </button>
        </div>
      </div>

      <!-- الماب تملأ ما تبقى من الشاشة بعد الهيدر وشريط الأدوات -->
      <div ref="mapEl" class="w-full h-[calc(100vh-160px)]"></div>
      <!-- ملاحظة: 160px تقريبية (ارتفاع الهيدر + التولبار). عدّلها إذا احتجت. -->
    </div>
  </Layout>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import L from 'leaflet'
import 'leaflet.markercluster'
import api from '@/services/api'
import Layout from '@/components/Layout.vue'

// ========= أيقونة بنفسجية مخصصة (SVG) =========
const purplePinSvg = `
<svg width="30" height="44" viewBox="0 0 30 44" fill="none" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <filter id="dropShadow" x="-50%" y="-50%" width="200%" height="200%">
      <feGaussianBlur in="SourceAlpha" stdDeviation="2"/>
      <feOffset dx="0" dy="2" result="offsetblur"/>
      <feComponentTransfer><feFuncA type="linear" slope="0.25"/></feComponentTransfer>
      <feMerge><feMergeNode/><feMergeNode in="SourceGraphic"/></feMerge>
    </filter>
  </defs>
  <path filter="url(#dropShadow)" d="M15 0C7.268 0 1 6.268 1 14c0 9.5 12.5 20 14 29.5C16.5 34 29 23.5 29 14 29 6.268 22.732 0 15 0Z" fill="#7C3AED"/>
  <circle cx="15" cy="14" r="5.5" fill="white"/>
</svg>
`
const PurpleIcon = L.divIcon({
  html: purplePinSvg,
  className: 'tw-pin',
  iconSize: [30, 44],
  iconAnchor: [15, 44],
  popupAnchor: [0, -44]
})

// ========= تنقل =========
const router = useRouter()
const goBack = () => router.back()

// ========= حالة الخريطة =========
const mapEl = ref(null)
let map, cluster, markers = []

const state = reactive({
  courses: [] // { id, title, imageUrl, latitude, longitude, locationType, fullAddress, price }
})

const showPhysical = ref(true)
const showHybrid   = ref(true)

// ========= أدوات مساعدة للعرض =========
function badge(type) {
  const t = (type || '').toUpperCase()
  if (t === 'PHYSICAL') return `<span style="background:#10b9811a;color:#059669;padding:.25rem .5rem;border-radius:999px;font-size:.75rem">حضوري</span>`
  if (t === 'HYBRID')   return `<span style="background:#6366f11a;color:#4f46e5;padding:.25rem .5rem;border-radius:999px;font-size:.75rem">هجين</span>`
  return ''
}
function priceFmt(p) {
  if (p == null) return '—'
  const n = Number(p)
  return Number.isFinite(n) ? n.toLocaleString('ar-SA') + ' ريال' : '—'
}
function popupTpl(c) {
  return `
  <div style="width:280px;direction:rtl;font-family:inherit">
    <div style="overflow:hidden;border-radius:14px;border:1px solid #e5e7eb;box-shadow:0 6px 16px rgba(15,23,42,.08)">
      ${c.imageUrl ? `<img src="${c.imageUrl}" alt="" style="width:100%;height:140px;object-fit:cover">` : ''}
      <div style="padding:12px;background:white">
        <div style="display:flex;justify-content:space-between;align-items:center;gap:8px;margin-bottom:6px">
          <h3 style="font-weight:800;color:#0f172a;font-size:14px;line-height:1.35;margin:0">${c.title ?? ''}</h3>
          ${badge(c.locationType)}
        </div>
        <div style="color:#64748b;font-size:12px;margin-bottom:10px">${c.fullAddress ?? c.location ?? ''}</div>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <div style="font-weight:700;color:#334155">${priceFmt(c.price)}</div>
          <a href="/courses/${c.id}"
             style="text-decoration:none;background:#7c3aed;color:white;padding:.5rem .8rem;border-radius:10px;font-size:12px">
            التفاصيل
          </a>
        </div>
      </div>
    </div>
  </div>`
}

// ========= طبقات البلاطات (Light/Dark) =========
let lightTiles, darkTiles
let currentBase = 'light'

function addBasemapToggleControl() {
  const Toggle = L.Control.extend({
    options: { position: 'topright' },
    onAdd() {
      const btn = L.DomUtil.create('button', 'tw-btn')
      btn.style.cssText = 'margin:8px;padding:8px 10px;border-radius:10px;background:#fff;border:1px solid #e5e7eb;cursor:pointer'
      btn.innerText = currentBase === 'light' ? '🌙 وضع داكن' : '☀️ وضع فاتح'
      btn.title = 'تبديل طبقة الخريطة'
      L.DomEvent.on(btn, 'click', (e) => {
        L.DomEvent.stopPropagation(e)
        if (currentBase === 'light') {
          map.removeLayer(lightTiles); darkTiles.addTo(map); currentBase = 'dark'
          btn.innerText = '☀️ وضع فاتح'
        } else {
          map.removeLayer(darkTiles); lightTiles.addTo(map); currentBase = 'light'
          btn.innerText = '🌙 وضع داكن'
        }
      })
      return btn
    }
  })
  map.addControl(new Toggle())
}

// ========= عدّاد ظاهر (كم نقطة معروضة) =========
let counterControl
function updateCounter(n) {
  if (!counterControl) return
  counterControl.innerHTML = `النقاط الظاهرة: <b>${n}</b>`
}
function addCounterControl() {
  const C = L.Control.extend({
    options: { position: 'topleft' },
    onAdd() {
      const div = L.DomUtil.create('div', 'tw-counter')
      div.style.cssText = 'margin:8px;padding:8px 10px;border-radius:10px;background:#fff;border:1px solid #e5e7eb;font-size:12px;color:#334155'
      div.innerHTML = 'النقاط الظاهرة: <b>0</b>'
      counterControl = div
      return div
    }
  })
  map.addControl(new C())
}

// ========= زر ملء الشاشة (بدون مكتبة) =========
function addFullscreenControl() {
  const F = L.Control.extend({
    options: { position: 'topleft' },
    onAdd() {
      const btn = L.DomUtil.create('button', 'tw-btn')
      btn.style.cssText = 'margin:8px 8px 0 8px;padding:8px 10px;border-radius:10px;background:#fff;border:1px solid #e5e7eb;cursor:pointer'
      btn.innerText = '⛶ ملء الشاشة'
      btn.title = 'عرض الخريطة بملء الشاشة'
      L.DomEvent.on(btn, 'click', (e) => {
        L.DomEvent.stopPropagation(e)
        const el = mapEl.value
        if (!document.fullscreenElement) el.requestFullscreen?.()
        else document.exitFullscreen?.()
      })
      return btn
    }
  })
  map.addControl(new F())
}

// ========= رسم العلامات =========
function renderMarkers() {
  if (!cluster) return
  cluster.clearLayers()
  markers = []

  const wantPhysical = showPhysical.value
  const wantHybrid   = showHybrid.value

  const filtered = state.courses.filter(c => {
    const hasLatLng = c.latitude != null && c.longitude != null
    const type = (c.locationType || '').toUpperCase()
    const typeOk = (type === 'PHYSICAL' && wantPhysical) || (type === 'HYBRID' && wantHybrid)
    return hasLatLng && typeOk
  })

  filtered.forEach(c => {
    const m = L.marker([c.latitude, c.longitude], { title: c.title || '', icon: PurpleIcon })
    m.bindTooltip(c.title || '', { direction: 'top', opacity: 0.9, offset: [0, -46] })
    m.bindPopup(popupTpl(c), { maxWidth: 300, className: 'tw-popup' })
    cluster.addLayer(m)
    markers.push(m)
  })

  updateCounter(markers.length)

  if (markers.length) {
    fitAll()
  } else {
    // رجّع على السعودية كمنظر عام
    map.setView([23.8859, 45.0792], 5)
  }
}

function fitAll() {
  if (!markers.length) return
  const group = L.featureGroup(markers)
  map.fitBounds(group.getBounds().pad(0.2), { animate: true })
}

function locateMe() {
  map.locate({ setView: true, maxZoom: 14 })
}
function onLocationError(e) {
  console.warn('Location error:', e?.message || e)
  // تجنّب أي تنبيهات مزعجة – بس سجّل
}

// ========= جلب البيانات =========
async function loadCourses() {
  try {
    const res = await api.get('/courses/with-coords')
    state.courses = Array.isArray(res.data) ? res.data : []
    renderMarkers()
  } catch (e) {
    console.error('Failed to load courses for map', e)
    state.courses = []
    renderMarkers()
  }
}

// ========= التهيئة =========
onMounted(async () => {
  map = L.map(mapEl.value, {
    center: [24.7136, 46.6753], // الرياض
    zoom: 6,
    zoomControl: false
  })
  L.control.zoom({ position: 'topright' }).addTo(map)

  lightTiles = L.tileLayer('https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; OpenStreetMap & Carto',
    maxZoom: 19
  })
  darkTiles = L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
    attribution: '&copy; OpenStreetMap & Carto',
    maxZoom: 19
  })
  lightTiles.addTo(map)

  cluster = L.markerClusterGroup({
    showCoverageOnHover: false,
    spiderfyOnMaxZoom: true,
    disableClusteringAtZoom: 16
  })
  map.addLayer(cluster)

  // Controls الإضافية
  addCounterControl()
  addFullscreenControl()
  addBasemapToggleControl()

  map.on('locationerror', onLocationError)

  await loadCourses()
})

onBeforeUnmount(() => {
  if (map) {
    map.off('locationerror', onLocationError)
    map.remove()
  }
})
</script>

<style scoped>
/* المساحة محسوبة كـ (طول الشاشة - ارتفاع الهيدر + شريط الأدوات) */
/* غطّينا كل الستايلات داخل عناصر التحكم نفسها لتفادي تضارب الثيم */
</style>
