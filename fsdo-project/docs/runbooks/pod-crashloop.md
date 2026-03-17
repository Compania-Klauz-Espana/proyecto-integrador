# Runbook — PodCrashLooping

## Descripción
Un pod reinicia más de 3 veces en 5 minutos.

## Impacto
Degradación de servicio o downtime parcial.

## Diagnóstico
```bash
kubectl get pods -n <namespace>
kubectl describe pod <pod-name> -n <namespace>
kubectl logs <pod-name> -n <namespace> --previous
```

## Resolución
1. Revisar logs del pod por errores de inicio
2. Verificar ConfigMap y Secrets montados correctamente
3. Verificar conectividad a base de datos
4. Si persiste: `kubectl rollout undo deployment/<name> -n <namespace>`

## Escalación
Si no se resuelve en 15 min → notificar al equipo de backend
