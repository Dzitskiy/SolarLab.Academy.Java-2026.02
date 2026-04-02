#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

kubectl delete -f "$ROOT_DIR/k8s/04-services.yml" --ignore-not-found
kubectl delete -f "$ROOT_DIR/k8s/03-deployments.yml" --ignore-not-found
kubectl delete -f "$ROOT_DIR/k8s/02-secrets.yml" --ignore-not-found
kubectl delete -f "$ROOT_DIR/k8s/01-configmap.yml" --ignore-not-found
kubectl delete -f "$ROOT_DIR/k8s/00-namespace.yml" --ignore-not-found

echo "Kubernetes resources removed."
