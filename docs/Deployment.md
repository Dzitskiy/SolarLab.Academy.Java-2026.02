# Deployment Guide

## Описание проекта

Это учебный backend-проект на Spring Boot 3 (Java 21) с PostgreSQL, Flyway и Keycloak.

Стек:
- Spring Boot Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Spring Security OAuth2 Resource Server
- Keycloak
- MapStruct
- JUnit + Testcontainers + ArchUnit + JaCoCo

Основные файлы:
- `docker-compose.yml` — запуск локальной инфраструктуры и приложения
- `.github/workflows/ci-cd.yml` — CI/CD в GitHub Actions
- `k8s/*.yml` — Kubernetes-манифесты
- `scripts/k8s-up.ps1`, `scripts/k8s-down.ps1` — запуск и удаление в Kubernetes

## Настройка CI/CD

Pipeline находится в `.github/workflows/ci-cd.yml`.

Триггеры:
- `push` в `main` и `master`
- `pull_request`

### CI (сборка и тесты)
Job `build-and-test`:
1. Checkout кода
2. Установка JDK 21
3. Установка Gradle
4. Запуск `gradle --no-daemon clean build`
5. Публикация тестовых отчетов (`build/reports/tests`, `build/reports/jacoco`)

### CD (Docker image)
Job `docker-image` выполняется только при `push` в `main/master`:
1. Сборка Docker-образа по `Dockerfile`
2. Логин в GHCR (`ghcr.io`)
3. Генерация тегов (`branch`, `sha`, `latest`)
4. Публикация образа в реестр

## Локальный запуск через Docker Compose

Запуск:
```bash
docker compose up -d --build
```

Остановка:
```bash
docker compose down
```

Сервисы:
- `postgres` — база данных
- `keycloak` — авторизация/JWT
- `app` — Spring Boot приложение

## Локальный запуск в Kubernetes

### Предварительные требования
- установлен `kubectl`
- поднят локальный кластер (Docker Desktop Kubernetes / Minikube / Kind)

Проверка:
```bash
kubectl cluster-info
```

### Сборка образа приложения
```bash
docker build -t solarlab-app:latest .
```

### Развертывание
PowerShell:
```powershell
.\scripts\k8s-up.ps1
```

Bash:
```bash
bash scripts/k8s-up.sh
```

### Проверка
```bash
kubectl get all -n solarlab
kubectl logs deploy/app -n solarlab
```

### Доступ
- Приложение: `http://localhost:30080`
- Keycloak: `http://localhost:30090`

### Удаление
PowerShell:
```powershell
.\scripts\k8s-down.ps1
```

Bash:
```bash
bash scripts/k8s-down.sh
```

## Важное замечание

Текущие пароли и секреты в `docker-compose.yml` и `k8s/02-secrets.yml` — учебные (dev-only).
Для production используйте внешний менеджер секретов и не храните реальные пароли в репозитории.