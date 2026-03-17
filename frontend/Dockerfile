# ── Stage 1: Build ──────────────────────────
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --quiet
COPY . .
ARG BUILD_ENV=production
RUN npm run build -- --configuration=$BUILD_ENV --output-path=dist/app

# ── Stage 2: Runtime ────────────────────────
FROM nginx:1.25-alpine AS runtime
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/dist/app /usr/share/nginx/html
COPY nginx/nginx.conf /etc/nginx/nginx.conf
RUN chown -R appuser:appgroup /usr/share/nginx/html \
    && chown -R appuser:appgroup /var/cache/nginx \
    && chown -R appuser:appgroup /var/log/nginx \
    && touch /var/run/nginx.pid \
    && chown appuser:appgroup /var/run/nginx.pid
USER appuser
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=5s \
  CMD wget -qO- http://localhost/health || exit 1
CMD ["nginx", "-g", "daemon off;"]
