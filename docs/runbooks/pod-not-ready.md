# Runbook — PodNotReady

## Descripcion
Un pod lleva mas de 2 minutos sin estar en estado Ready.

## Impacto
El pod no recibe trafico. Si todos los pods del deployment estan afectados, hay downtime del servicio.

## Diagnostico

```bash
# Ver estado de pods
kubectl get pods -n <namespace>

# Ver detalle del pod
kubectl describe pod <pod-name> -n <namespace>

# Ver logs del pod
kubectl logs <pod-name> -n <namespace> --previous

# Ver eventos del namespace
kubectl get events -n <namespace> --sort-by='.lastTimestamp'
```

## Causas comunes y resolucion

1. **Readiness probe falla** — Revisar logs de inicio, verificar que DB este accesible
2. **OOMKilled** — Aumentar memory limit en el overlay correspondiente
3. **ImagePullBackOff** — Verificar que la imagen existe en ACR y el secreto de pull esta configurado
4. **CrashLoopBackOff** — Ver runbook pod-crashloop.md

## Rollback de emergencia

```bash
kubectl rollout undo deployment/<name> -n <namespace>
kubectl rollout status deployment/<name> -n <namespace>
```

## Escalacion
Si no se resuelve en 10 minutos, notificar al equipo de backend.
