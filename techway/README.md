# Frontend (Vue 3 + Vite) – techway

Vue 3 SPA for TechWay. Talks to the Spring Boot API.

## Stack
- Vue 3 + Vite
- Pinia / Vue Router (if present)
- Tailwind CSS
- Axios
- Leaflet (maps), ApexCharts (charts)

## Scripts
```bash
npm i
npm run dev      # http://localhost:5173
npm run build    # outputs dist/
npm run preview
```

## Env
Create `.env`:
```
VITE_API_BASE=http://localhost:8080
```
Use it in Axios:
```ts
const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE || "/api" });
```

## Folder Hints
```
src/
  assets/     components/     pages/     router/     store/     utils/
```

## Connect to Backend
- All API calls should go to `${VITE_API_BASE}/api/...`
- For production behind Nginx, proxy `/api` to the backend container.

## Build for Prod
```bash
npm run build
# Serve dist/ via Nginx (root /usr/share/nginx/html)
```

## Troubleshooting
- **CORS/401**: Make sure JWT is sent: `Authorization: Bearer <token>`
- **Paths**: If 404 on refresh, ensure Nginx `try_files $uri /index.html;`
- **Map tiles**: Leaflet requires CSS & correct container height.
