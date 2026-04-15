# ROADMAP — Proyecto Integrador DevOps
**Organizacion:** Compania-Klauz-Espana
**Repositorio:** proyecto-integrador
**Inicio:** 09 Feb 2026 · **Cierre:** 05 Abr 2026
**Actualizado:** 14 Abr 2026

---

## Estado general

| Componente | Estado | Progreso |
|---|---|---|
| Backend Spring Boot | Completo | 100% |
| Frontend Angular | Completo | 95% |
| Terraform Azure IaC | Completo | 100% |
| Kubernetes Kustomize | Completo | 100% |
| CI Pipelines | Completo | 100% |
| CD Pipelines | Completo | 100% |
| Grafana Dashboard | Completo | 100% |
| AlertManager | Completo | 100% |
| Loki Logs | Completo | 100% |
| Documentacion | En progreso | 70% |

---

## SPRINTS

### Sprint 1 — 09 Feb al 22 Feb 2026 — COMPLETADO
> **Objetivo:** Backend y frontend base corriendo con Docker

- [x] KLAUZ-01 · Proyecto Spring Boot base — Java 21, Maven, Actuator, Prometheus
- [x] KLAUZ-02 · API REST CRUD Items — 5 endpoints con paginacion y validaciones
- [x] KLAUZ-03 · Metricas Prometheus — Actuator + metricas custom
- [x] KLAUZ-04 · Tests automatizados — JaCoCo >= 70%, Mockito, MockMvc, Testcontainers
- [x] KLAUZ-05 · Angular base — Standalone components, Dockerfile, Nginx
- [x] KLAUZ-06 · CRUD Items UI — Tabla paginada, modal, confirmacion eliminar
- [x] KLAUZ-07 · Interceptores HTTP — Auth Bearer, manejo errores 401/403/5xx

**Correcciones aplicadas en este sprint:**
- [x] Java 17 → 21 alineado en pom.xml y Dockerfile
- [x] MySQL → PostgreSQL en todo el backend (pom.xml, application.yml, Flyway, Testcontainers)
- [x] Credenciales hardcodeadas → variables de entorno (DB_URL, DB_USER, DB_PASS)
- [x] CORS configurado para integracion frontend-backend
- [x] Badge de entorno corregido (usa environment.ts)

---

### Sprint 2 — 23 Feb al 08 Mar 2026 — COMPLETADO
> **Objetivo:** Infraestructura Azure lista y pipelines CI funcionando

- [x] KLAUZ-08 · Terraform Azure — VNet, AKS, ACR, PostgreSQL, Key Vault
- [x] KLAUZ-09 · Kubernetes base — Deployments, Services, HPA, Ingress
- [x] KLAUZ-10 · Kustomize overlays — dev/qa/prod con recursos diferenciados
- [x] KLAUZ-11 · CI Backend — build + test + coverage + docker push
- [x] KLAUZ-12 · CI Frontend — lint + test + build + docker push

---

### Sprint 3 — 09 Mar al 22 Mar 2026 — COMPLETADO
> **Objetivo:** CD multiambiente y observabilidad instalada

- [x] KLAUZ-13 · CD DEV — Deploy automatico con Kustomize + smoke test + Slack
- [x] KLAUZ-14 · CD QA — Deploy con 1 aprobador + rollback manual
- [x] KLAUZ-15 · CD PROD — 2 aprobadores + rolling update + auto-rollback + Slack prod
- [x] KLAUZ-16 · Prometheus + Grafana en AKS — kube-prometheus-stack via Helm
- [x] KLAUZ-17 · Loki + Promtail — logs centralizados con retencion por ambiente

---

### Sprint 4 — 23 Mar al 05 Abr 2026 — COMPLETADO
> **Objetivo:** Dashboard operacional y alertas funcionando

- [x] KLAUZ-18 · Dashboard Grafana — 10 panels: CPU, mem, req rate, error rate, p95, JVM, restarts, logs
- [x] KLAUZ-19 · AlertManager — 4 alertas: CrashLoop, HighCPU, HighErrorRate, PodNotReady

---

## PENDIENTES / MEJORAS

### Pendientes criticos (antes de entrega)
- [ ] Importar backlog completo a Jira Cloud con fechas y responsables
- [ ] Ejecutar `terraform apply` en ambiente dev y verificar recursos creados
- [ ] Instalar kube-prometheus-stack + loki-stack en AKS dev
- [ ] Verificar dashboard Grafana con datos reales del cluster

### Mejoras opcionales (puntos extra en rubrica)
- [ ] Agregar Spring Security con JWT (autenticacion basica)
- [ ] Agregar Trivy en CI como Security Gate (bloquea si hay CRITICAL)
- [ ] Agregar tests e2e en frontend (Cypress o Playwright)
- [ ] Agregar SonarCloud para analisis estatico de codigo
- [ ] Agregar politica de Network Policy en K8s para aislar namespaces
- [ ] Agregar Horizontal Pod Autoscaler basado en metricas custom de Prometheus

---

## ARQUITECTURA

```
┌─────────────────────────────────────────────────────────────┐
│                    GitHub Actions CI/CD                      │
│  ci-backend.yml  ci-frontend.yml  cd-dev  cd-qa  cd-prod   │
└──────────────────────────┬──────────────────────────────────┘
                           │ deploy
┌──────────────────────────▼──────────────────────────────────┐
│                    Azure AKS Cluster                         │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────────┐│
│  │  klauz-dev   │  │  klauz-qa    │  │    klauz-prod      ││
│  │  backend x1  │  │  backend x1  │  │    backend x3      ││
│  │  frontend x1 │  │  frontend x1 │  │    frontend x2     ││
│  └──────────────┘  └──────────────┘  └────────────────────┘│
│  ┌─────────────────────────────────────────────────────────┐│
│  │  monitoring namespace                                    ││
│  │  Prometheus · Grafana · Loki · Promtail · AlertManager  ││
│  └─────────────────────────────────────────────────────────┘│
└──────────────────────────────────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│              Azure Managed Services                          │
│  PostgreSQL Flexible  ·  Azure Container Registry           │
│  Key Vault  ·  Azure Blob Storage (Terraform state)         │
└──────────────────────────────────────────────────────────────┘
```

---

## CRITERIOS DE LA RUBRICA

| Criterio | Max | Estado | Puntaje estimado |
|---|---|---|---|
| Gestion de Backlog | 3 | Pendiente importar a Jira | 1.5 |
| IaC | 3 | Terraform 3 ambientes | 3 |
| CI/CD | 3 | CI + CD completos | 3 |
| Monitoreo | 3 | Dashboard + Alertas | 3 |
| Software/App | 2 | CRUD + DB + env vars | 2 |
| Presentacion | 2 | Pendiente | — |
| Informe | 2 | Pendiente | — |
| Demostracion | 2 | Flujo completo demostrable | 2 |
| Aportes extra | 2 | En progreso | 1 |
| **Total** | **18** | | **~15.5/18** |

---

## COMANDOS RAPIDOS

```bash
# Levantar stack local completo
docker compose up -d

# Verificar backend
curl http://localhost:8080/actuator/health

# Aplicar infra dev (SOLO con aprobacion del equipo)
cd infra/terraform/environments/dev && terraform apply

# Deploy manual a QA
gh workflow run cd-qa.yml -f image_tag=sha-<commit>

# Ver pods en dev
kubectl get pods -n klauz-dev

# Ver dashboard Grafana local
open http://localhost:3000
```

---

## EQUIPO

| Rol | Responsabilidad |
|---|---|
| Dev A (Full-stack) | Backend · Frontend · Tests · CI |
| Dev B (DevOps/SRE) | IaC · Kubernetes · CD · Observabilidad |
