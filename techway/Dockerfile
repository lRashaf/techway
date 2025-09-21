# ===== Build (Vite) =====
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
# لو ci فشل بسبب peer deps، npm i يكفي
RUN npm ci || npm i
COPY . .
RUN npm run build

# ===== Serve (Nginx) =====
FROM nginx:alpine
# ننسخ ملفات build فقط
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
# الإعداد الافتراضي لـ nginx يشتغل مبدئيًا؛
# سنضيف nginx.conf و docker-compose في المهمة الجاية.
