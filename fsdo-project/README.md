# FSDO Project — Full Stack DevOps

Proyecto de referencia que implementa una arquitectura full stack con prácticas DevOps completas.

## Stack

| Componente | Tecnología |
|---|---|
| Backend | Java 21 · Spring Boot 3.2 · Maven |
| Frontend | Angular 17 · Nginx |
| Infraestructura | Terraform · AWS EKS · RDS PostgreSQL |
| Orquestación | Kubernetes · Kustomize · Helm |
| CI/CD | GitHub Actions |
| Observabilidad | Prometheus · Grafana · Loki · AlertManager |

## Estructura del repositorio

```
fsdo-project/
├── .github/workflows/   → Pipelines CI/CD
├── backend/             → API REST Spring Boot
├── frontend/            → SPA Angular
├── infra/               → IaC Terraform + K8s Kustomize
├── monitoring/          → Helm values + Grafana dashboards + AlertManager
└── docs/                → Runbooks y documentación
```

## Ambientes

| Ambiente | Branch | Deploy | Aprobaciones |
|---|---|---|---|
| dev | develop | Automático | 0 |
| qa | develop | Manual | 1 |
| prod | main | Manual | 2 |

## Inicio rápido (local)

```bash
# Clonar
git clone https://github.com/tu-org/fsdo-project.git
cd fsdo-project

# Levantar stack completo
docker compose up -d

# Verificar backend
curl http://localhost:8080/actuator/health

# Verificar frontend
open http://localhost:4200
```

## Pipelines

| Workflow | Trigger | Path filter |
|---|---|---|
| ci-backend | push / PR | `backend/**` |
| ci-frontend | push / PR | `frontend/**` |
| cd-dev | CI exitoso en develop | — |
| cd-qa | Aprobación manual | — |
| cd-prod | 2 aprobaciones | — |

## Equipo

| Rol | Responsabilidad |
|---|---|
| Dev A (Full-stack) | Backend · Frontend · Tests |
| Dev B (DevOps/SRE) | Infra · CI/CD · Observabilidad |

## Cronograma

| Sprint | Fechas | Objetivo |
|---|---|---|
| Sprint 1 | 01–14 Feb 2026 | Backend + Frontend base |
| Sprint 2 | 15–28 Feb 2026 | IaC + CI |
| Sprint 3 | 01–14 Mar 2026 | CD multiambiente + Observabilidad |
| Sprint 4 | 15–28 Mar 2026 | Dashboard Grafana + Alertas |
