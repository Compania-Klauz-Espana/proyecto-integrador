# GUIA DE DEMOSTRACION — Proyecto Integrador DevOps

Esta guia esta organizada en el orden exacto para grabar el video de demostracion.
Abre VS Code con el proyecto y usa la terminal integrada.

---

## PREREQUISITOS — Instalar herramientas

Abre la terminal de VS Code y ejecuta:

```bash
# Instalar Azure CLI
brew install azure-cli

# Instalar Helm (para monitoring en AKS)
brew install helm

# Instalar GitHub CLI (para ver workflows)
brew install gh
```

Verifica que todo quedo instalado:

```bash
az version
helm version --short
gh --version
```

---

## PARTE 1 — INFRAESTRUCTURA COMO CODIGO (IaC)

> En el video: "Voy a mostrar como definimos la infraestructura con Terraform
> y como se despliega en Azure"

### 1.1 Mostrar los archivos en VS Code

Abre estos archivos en VS Code y explicalos brevemente:

1. `infra/terraform/backend.tf` — Backend remoto en Azure Storage
2. `infra/terraform/modules/` — Expandir la carpeta y mostrar los 4 modulos:
   - `networking/main.tf` — VNet, subnets, NSG, DNS zone
   - `aks/main.tf` — Cluster Kubernetes
   - `acr/main.tf` — Container Registry
   - `database/main.tf` — PostgreSQL Flexible Server + Key Vault
3. `infra/terraform/environments/dev/main.tf` — Como se ensamblan los modulos
4. `infra/terraform/environments/dev/terraform.tfvars` — Variables del ambiente dev

**Puntos clave para mencionar:**
- "Tenemos 4 modulos reutilizables"
- "3 ambientes: dev, qa y prod, cada uno con sus propias variables"
- "El state se guarda en Azure Storage, no localmente"
- "Buenas practicas: modularizacion, variables, sin credenciales hardcodeadas"

### 1.2 Login en Azure

```bash
# Login (abre el navegador)
az login

# Seleccionar la suscripcion
az account set --subscription "82afe158-defc-4c91-9593-e4f9a60cb4f4"

# Verificar
az account show --query "{name:name, id:id}" -o table
```

### 1.3 Crear el backend de Terraform (solo la primera vez)

```bash
# Crear resource group para el state
az group create \
  --name klauz-tfstate-rg \
  --location eastus

# Crear storage account
az storage account create \
  --name klauztfstate \
  --resource-group klauz-tfstate-rg \
  --location eastus \
  --sku Standard_LRS

# Crear container
az storage container create \
  --name tfstate \
  --account-name klauztfstate
```

### 1.4 Ejecutar Terraform — Ambiente DEV

```bash
cd infra/terraform/environments/dev

# Inicializar (descarga providers y configura backend)
terraform init

# Ver que se va a crear (plan)
terraform plan -out=dev.tfplan

# APLICAR (esto tarda ~10-15 minutos)
terraform apply dev.tfplan
```

**Mientras espera terraform apply, puedes mostrar en el portal de Azure:**
- Ir a portal.azure.com
- Buscar "Resource groups"
- Mostrar que se esta creando `klauz-dev-rg`

### 1.5 Verificar en Azure Portal (para el video)

Despues de que terraform apply termine:

```bash
# Ver los outputs
terraform output
```

**En el portal de Azure, navegar y mostrar:**
1. Resource Group `klauz-dev-rg` — mostrar todos los recursos creados
2. AKS cluster `klauz-dev-aks` — mostrar nodos, version de K8s
3. Container Registry `klauzdevacr` — mostrar que esta vacio (aun no hay imagenes)
4. PostgreSQL `klauz-dev-db` — mostrar configuracion, networking
5. Key Vault — mostrar que tiene el secreto de la password de DB

```bash
# Volver al root del proyecto
cd ~/ProPer/DMC/proyecto-integrador
```

### 1.6 Conectar kubectl al cluster

```bash
# Obtener credenciales del cluster
az aks get-credentials \
  --resource-group klauz-dev-rg \
  --name klauz-dev-aks \
  --overwrite-existing

# Verificar conexion
kubectl get nodes
kubectl cluster-info
```

---

## PARTE 2 — CI/CD Y APLICACION CORRIENDO

> En el video: "Ahora voy a mostrar los pipelines de CI/CD y el despliegue
> de la aplicacion"

### 2.1 Mostrar archivos CI/CD en VS Code

Abrir y explicar brevemente:

1. `.github/workflows/ci-backend.yml` — "CI del backend: compila, corre tests, construye imagen Docker y la sube a GHCR"
2. `.github/workflows/ci-frontend.yml` — "CI del frontend: lint, tests, build production, imagen Docker"
3. `.github/workflows/cd-dev.yml` — "CD automatico a dev cuando CI pasa"
4. `.github/workflows/cd-qa.yml` — "CD manual a QA con 1 aprobador"
5. `.github/workflows/cd-prod.yml` — "CD manual a PROD con 2 aprobadores, auto-rollback si falla"

**Puntos clave:**
- "CI se dispara automaticamente con push a main o develop"
- "CD usa Kustomize para configurar cada ambiente"
- "Prod tiene health check con reintentos y rollback automatico"

### 2.2 Mostrar archivos de Kubernetes en VS Code

1. `infra/k8s/base/backend/deployment.yaml` — "Deployment base con probes, resources, env vars"
2. `infra/k8s/base/backend/hpa.yaml` — "Auto-scaling basado en CPU y memoria"
3. `infra/k8s/base/ingress.yaml` — "Ingress con rutas /api y /"
4. `infra/k8s/overlays/dev/kustomization.yaml` — "Overlay dev: namespace, images, patches"
5. `infra/k8s/overlays/prod/kustomization.yaml` — "Overlay prod: mas replicas, mas recursos, PDB, TLS"

### 2.3 Construir imagenes Docker y subirlas a ACR

```bash
# Login en el container registry de Azure
az acr login --name klauzdevacr

# Construir backend
docker build -t klauzdevacr.azurecr.io/backend:v1 ./backend

# Construir frontend
docker build -t klauzdevacr.azurecr.io/frontend:v1 ./frontend

# Push al registry
docker push klauzdevacr.azurecr.io/backend:v1
docker push klauzdevacr.azurecr.io/frontend:v1

# Verificar que estan en ACR
az acr repository list --name klauzdevacr -o table
az acr repository show-tags --name klauzdevacr --repository backend -o table
```

**En Azure Portal:** Navegar a ACR > Repositories y mostrar las imagenes.

### 2.4 Crear el secret de base de datos en Kubernetes

```bash
# Obtener la password de Key Vault
DB_PASS=$(az keyvault secret show \
  --vault-name klauz-dev-kv \
  --name db-password \
  --query value -o tsv)

# Crear namespace
kubectl create namespace klauz-dev

# Crear el secret en Kubernetes
kubectl create secret generic backend-secrets \
  --from-literal=db-password="$DB_PASS" \
  -n klauz-dev
```

### 2.5 Actualizar image tags y desplegar a DEV

```bash
# Actualizar las imagenes en el overlay dev
cd infra/k8s/overlays/dev
kustomize edit set image \
  klauzdevacr.azurecr.io/backend=klauzdevacr.azurecr.io/backend:v1 \
  klauzdevacr.azurecr.io/frontend=klauzdevacr.azurecr.io/frontend:v1

cd ~/ProPer/DMC/proyecto-integrador

# Aplicar manifiestos
kubectl apply -k infra/k8s/overlays/dev

# Ver que se esta desplegando
kubectl get pods -n klauz-dev -w
```

Esperar hasta que todos los pods esten Running. Luego:

```bash
# Ver todos los recursos
kubectl get all -n klauz-dev

# Ver logs del backend
kubectl logs -n klauz-dev deployment/backend --tail=20

# Health check
kubectl exec -n klauz-dev deployment/backend -- \
  curl -s http://localhost:8080/actuator/health
```

### 2.6 Acceder a la aplicacion

```bash
# Port-forward para acceder localmente
# Backend
kubectl port-forward -n klauz-dev svc/backend 8080:8080 &

# Frontend
kubectl port-forward -n klauz-dev svc/frontend 4200:80 &

# Probar backend
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/items

# Abrir frontend en el navegador
open http://localhost:4200
```

**En el video, mostrar en el navegador:**
- La app Angular cargando
- Crear un item nuevo
- Editar un item
- Eliminar un item
- Ver la paginacion

### 2.7 Mostrar CI en GitHub Actions (opcional pero suma)

Si tienes `gh` instalado:

```bash
gh auth login

# Ver runs recientes
gh run list --limit 5

# O abrir en el navegador
open https://github.com/Compania-Klauz-Espana/proyecto-integrador/actions
```

Mostrar en el navegador los workflows ejecutados.

---

## PARTE 3 — MONITOREO

> En el video: "Finalmente voy a mostrar el stack de observabilidad:
> Prometheus, Grafana, Loki y las alertas"

### 3.1 Mostrar archivos de monitoring en VS Code

1. `monitoring/grafana/dashboards/system-health.json` — "Dashboard con 10 paneles"
2. `monitoring/alerts/prometheus-rules.yaml` — "4 alertas: CrashLoop, PodNotReady, HighCPU, HighErrorRate"
3. `monitoring/grafana/provisioning/datasources.yaml` — "Datasources: Prometheus y Loki"
4. `docs/runbooks/pod-crashloop.md` — "Runbooks vinculados a las alertas"

### 3.2 Instalar kube-prometheus-stack en AKS

```bash
# Agregar repos de Helm
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

# Crear namespace de monitoring
kubectl create namespace monitoring

# Instalar Prometheus + Grafana + AlertManager
helm install monitoring prometheus-community/kube-prometheus-stack \
  --namespace monitoring \
  --set grafana.adminPassword=admin123 \
  --set prometheus.prometheusSpec.serviceMonitorSelectorNilUsesHelmValues=false \
  --wait

# Instalar Loki + Promtail
helm install loki grafana/loki-stack \
  --namespace monitoring \
  --set promtail.enabled=true \
  --set loki.persistence.enabled=false \
  --wait
```

Verificar que todo esta corriendo:

```bash
kubectl get pods -n monitoring
```

### 3.3 Agregar Loki como datasource en Grafana

```bash
# Port-forward a Grafana
kubectl port-forward -n monitoring svc/monitoring-grafana 3000:80 &

echo "Grafana disponible en http://localhost:3000"
echo "Usuario: admin / Password: admin123"
```

**En el navegador (http://localhost:3000):**
1. Login con admin / admin123
2. Ir a Configuration > Data Sources
3. Prometheus ya esta configurado automaticamente
4. Agregar Loki:
   - Click "Add data source"
   - Seleccionar "Loki"
   - URL: `http://loki:3100`
   - Click "Save & Test"

### 3.4 Importar el dashboard

**En Grafana (navegador):**
1. Ir al menu lateral > Dashboards > Import
2. Click "Upload JSON file"
3. Seleccionar: `monitoring/grafana/dashboards/system-health.json`
4. Click "Import"

**Mostrar el dashboard:**
- Los paneles de CPU, memoria, HTTP requests
- Los pods running
- Los logs de Loki (panel "Application Logs")
- Explicar: "Este dashboard refleja la salud del sistema en tiempo real"

### 3.5 Verificar Prometheus y alertas

```bash
# Port-forward a Prometheus
kubectl port-forward -n monitoring svc/monitoring-kube-prometheus-prometheus 9090:9090 &

echo "Prometheus disponible en http://localhost:9090"
```

**En el navegador (http://localhost:9090):**
1. Ir a Status > Targets — mostrar que hay targets activos
2. Ir a Alerts — mostrar las reglas configuradas
3. Ejecutar una query: `up` — muestra los servicios monitoreados

### 3.6 Aplicar las reglas de alerta custom

```bash
kubectl apply -f monitoring/alerts/prometheus-rules.yaml
```

Volver a Prometheus > Alerts y mostrar que las reglas custom aparecen.

### 3.7 Generar trafico para ver metricas (opcional)

```bash
# Generar un poco de trafico al backend para que las metricas se vean bonitas
for i in $(seq 1 50); do
  curl -s http://localhost:8080/api/items > /dev/null
  curl -s http://localhost:8080/actuator/prometheus > /dev/null
done

echo "Trafico generado, espera 30 segundos y revisa el dashboard"
```

Volver al dashboard de Grafana y mostrar que las metricas de HTTP Request Rate se actualizan.

---

## PARTE 4 — DEMO LOCAL CON DOCKER COMPOSE (alternativa rapida)

Si Azure tarda mucho o hay problemas, puedes demostrar todo localmente:

```bash
cd ~/ProPer/DMC/proyecto-integrador

# Levantar todo el stack
docker compose up -d --build

# Esperar a que todo este listo (~1-2 min)
docker compose ps

# Verificar backend
curl http://localhost:8080/actuator/health

# Verificar frontend
open http://localhost:4200

# Verificar Grafana
open http://localhost:3000
# Login: admin / admin

# Verificar Prometheus
open http://localhost:9090
```

---

## LIMPIEZA (despues del video)

```bash
# Matar port-forwards
pkill -f "port-forward"

# Destruir infra Azure (CUIDADO - borra todo)
cd infra/terraform/environments/dev
terraform destroy

# O simplemente borrar el resource group
az group delete --name klauz-dev-rg --yes --no-wait
az group delete --name klauz-tfstate-rg --yes --no-wait
```

---

## RESUMEN PARA EL VIDEO

| Seccion | Que mostrar | Duracion estimada |
|---|---|---|
| Intro | ROADMAP.md, estructura del repo | 2 min |
| IaC | Archivos Terraform → terraform apply → Azure Portal | 5 min |
| CI/CD | Archivos workflows → build images → deploy a AKS | 5 min |
| App | Frontend funcionando, CRUD completo | 3 min |
| Monitoreo | Grafana dashboard, Prometheus, Loki, alertas | 5 min |
| Cierre | Resumen de arquitectura, proximos pasos | 2 min |

**Total estimado: ~22 minutos**
