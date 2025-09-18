// src/utils/normalize.js

// يحوّل أي قيمة رقمية (string/number/null) إلى رقم آمن للرسم
function toNumSafe(v) {
  if (v == null) return 0
  const n = typeof v === 'number' ? v : Number(v)
  return Number.isFinite(n) ? n : 0
}

// TrendDTO: { labels: string[], data: number[] } أو payload عام
export function normalizeTrend(payload) {
  // لو جتنا ApiResponse مفكوكة أصلاً من interceptor فـ payload = {labels, data}
  if (!payload || typeof payload !== 'object') {
    return { labels: [], data: [] }
  }

  // الشكل المتوقع مباشرة
  if (Array.isArray(payload.labels) && Array.isArray(payload.data)) {
    return {
      labels: payload.labels.map(String),
      data: payload.data.map(toNumSafe)
    }
  }

  // fallback: لو جاك {items:[{label, value}]}
  if (Array.isArray(payload.items)) {
    return {
      labels: payload.items.map(i => String(i.label ?? '')),
      data: payload.items.map(i => toNumSafe(i.value))
    }
  }

  return { labels: [], data: [] }
}

// DistributionDTO: { labels: string[], data: number[] } أو كائن {label:count}
export function normalizeDistribution(payload) {
  if (!payload || typeof payload !== 'object') {
    return { labels: [], data: [] }
  }

  // الشكل المتوقع مباشرة
  if (Array.isArray(payload.labels) && Array.isArray(payload.data)) {
    return {
      labels: payload.labels.map(String),
      data: payload.data.map(toNumSafe)
    }
  }

  // fallback: لو جاك object { "Design": 4, "IT": 2 }
  if (!Array.isArray(payload) && payload && typeof payload === 'object') {
    const entries = Object.entries(payload)
    // إذا كانت القيم أرقام كلها تقريبًا نعتبره توزيع
    if (entries.some(([k, v]) => typeof v === 'number' || typeof v === 'string')) {
      const labels = entries.map(([k]) => String(k))
      const data = entries.map(([, v]) => toNumSafe(v))
      return { labels, data }
    }
  }

  // fallback: لو جاك {items:[{label,count}]}
  if (Array.isArray(payload.items)) {
    return {
      labels: payload.items.map(i => String(i.label ?? '')),
      data: payload.items.map(i => toNumSafe(i.count))
    }
  }

  return { labels: [], data: [] }
}
