$ErrorActionPreference = "Stop"

$rootDir = Resolve-Path (Join-Path $PSScriptRoot "..")

kubectl delete -f "$rootDir\k8s\04-services.yml" --ignore-not-found
kubectl delete -f "$rootDir\k8s\03-deployments.yml" --ignore-not-found
kubectl delete -f "$rootDir\k8s\02-secrets.yml" --ignore-not-found
kubectl delete -f "$rootDir\k8s\01-configmap.yml" --ignore-not-found
kubectl delete -f "$rootDir\k8s\00-namespace.yml" --ignore-not-found

Write-Host "Kubernetes resources removed."
