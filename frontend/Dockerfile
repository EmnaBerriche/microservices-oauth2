# Multi-stage build for Angular frontend
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
# Install dependencies
RUN npm ci
COPY . .
# Build Angular app
RUN npm run build

# Runtime stage with Nginx
FROM nginx:alpine
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
# Optional: custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
