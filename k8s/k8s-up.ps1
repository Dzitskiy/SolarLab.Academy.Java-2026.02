$ErrorActionPreference = "Stop"

$rootDir = Resolve-Path (Join-Path $PSScriptRoot "..")

kubectl apply -f "$rootDir\k8s\00-namespace.yml"
kubectl apply -f "$rootDir\k8s\01-configmap.yml"
kubectl apply -f "$rootDir\k8s\02-secrets.yml"
kubectl apply -f "$rootDir\k8s\03-deployments.yml"
kubectl apply -f "$rootDir\k8s\04-services.yml"

Write-Host "Kubernetes resources applied."
kubectl get pods -n solarlab
