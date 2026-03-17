# Runbook — HighCPUUsage

## Descripción
CPU de un pod supera el 80% por más de 5 minutos.

## Diagnóstico
```bash
kubectl top pods -n <namespace>
kubectl top nodes
```

## Resolución
1. Revisar si hay aumento de tráfico legítimo → escalar réplicas
2. Si es anomalía → revisar logs por bucles o queries lentas
3. Escalar HPA manualmente si es urgente:
   `kubectl scale deployment <name> --replicas=<n> -n <namespace>`

## Escalación
Si CPU > 95% por más de 10 min → escalar al equipo DevOps
