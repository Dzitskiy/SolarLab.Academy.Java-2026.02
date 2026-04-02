#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

kubectl apply -f "$ROOT_DIR/k8s/00-namespace.yml"
kubectl apply -f "$ROOT_DIR/k8s/01-configmap.yml"
kubectl apply -f "$ROOT_DIR/k8s/02-secrets.yml"
kubectl apply -f "$ROOT_DIR/k8s/03-deployments.yml"
kubectl apply -f "$ROOT_DIR/k8s/04-services.yml"

echo "Kubernetes resources applied."
kubectl get pods -n solarlab
