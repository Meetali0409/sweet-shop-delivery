#!/usr/bin/env bash
#
# Manual deploy script: builds the backend Docker image, pushes it to
# Google Artifact Registry, and deploys it to Cloud Run.
#
# Prerequisites:
#   - gcloud CLI installed and authenticated (gcloud auth login)
#   - Docker installed and running
#   - A GCP project with billing enabled and the Cloud Run + Artifact
#     Registry APIs enabled (this script will try to enable them)
#
# Required environment variables (see backend/.env.gcp.example):
#   GCP_PROJECT_ID   - target GCP project id
#   GCP_REGION       - e.g. us-central1 (default below)
#   ARTIFACT_REPO    - Artifact Registry repo name (default: sweetshop)
#   SERVICE_NAME     - Cloud Run service name (default: sweetshop-backend)
#   DATABASE_URL     - Neon pooled JDBC connection string
#   DB_USERNAME      - Neon database user
#   DB_PASSWORD      - Neon database password
#   JWT_SECRET       - at least 256 bits
#   ALLOWED_ORIGINS  - comma-separated list of allowed CORS origins, or "*"
#
# Optional:
#   SWAGGER_ENABLED, STORAGE_TYPE, STORAGE_S3_*, RAZORPAY_KEY_ID,
#   RAZORPAY_KEY_SECRET
#
# Usage:
#   cd backend
#   export GCP_PROJECT_ID=sweetshop-backend-dummy
#   export DATABASE_URL="jdbc:postgresql://ep-xxxx-pooler.us-east-2.aws.neon.tech/sweetshop?sslmode=require&prepareThreshold=0"
#   export DB_USERNAME=sweetshop_owner
#   export DB_PASSWORD=change-me
#   export JWT_SECRET=change-this-to-a-real-256-bit-secret
#   export ALLOWED_ORIGINS="*"
#   ./scripts/deploy-gcp.sh

set -euo pipefail

: "${GCP_PROJECT_ID:?Set GCP_PROJECT_ID to your GCP project id}"
: "${DATABASE_URL:?Set DATABASE_URL to the Neon pooled connection string}"
: "${DB_USERNAME:?Set DB_USERNAME to the Neon database user}"
: "${DB_PASSWORD:?Set DB_PASSWORD to the Neon database password}"
: "${JWT_SECRET:?Set JWT_SECRET to a 256-bit+ secret}"

GCP_REGION="${GCP_REGION:-us-central1}"
ARTIFACT_REPO="${ARTIFACT_REPO:-sweetshop}"
SERVICE_NAME="${SERVICE_NAME:-sweetshop-backend}"
ALLOWED_ORIGINS="${ALLOWED_ORIGINS:-*}"
SWAGGER_ENABLED="${SWAGGER_ENABLED:-false}"
STORAGE_TYPE="${STORAGE_TYPE:-local}"
RAZORPAY_KEY_ID="${RAZORPAY_KEY_ID:-}"
RAZORPAY_KEY_SECRET="${RAZORPAY_KEY_SECRET:-}"

IMAGE="${GCP_REGION}-docker.pkg.dev/${GCP_PROJECT_ID}/${ARTIFACT_REPO}/${SERVICE_NAME}"
TAG="$(git rev-parse --short HEAD 2>/dev/null || date +%s)"

echo "==> Using project:  ${GCP_PROJECT_ID}"
echo "==> Using region:   ${GCP_REGION}"
echo "==> Image:          ${IMAGE}:${TAG}"

gcloud config set project "${GCP_PROJECT_ID}" >/dev/null

echo "==> Enabling required APIs (no-op if already enabled)"
gcloud services enable run.googleapis.com artifactregistry.googleapis.com >/dev/null

echo "==> Ensuring Artifact Registry repo exists"
if ! gcloud artifacts repositories describe "${ARTIFACT_REPO}" --location="${GCP_REGION}" >/dev/null 2>&1; then
  gcloud artifacts repositories create "${ARTIFACT_REPO}" \
    --repository-format=docker \
    --location="${GCP_REGION}" \
    --description="Sweet Shop backend images"
fi

echo "==> Configuring Docker auth for Artifact Registry"
gcloud auth configure-docker "${GCP_REGION}-docker.pkg.dev" --quiet

echo "==> Building image"
docker build -t "${IMAGE}:${TAG}" -t "${IMAGE}:latest" .

echo "==> Pushing image"
docker push "${IMAGE}:${TAG}"
docker push "${IMAGE}:latest"

# NOTE: for a quick/dummy setup this passes secrets as plain Cloud Run env
# vars. Before going to production, move DB_PASSWORD, JWT_SECRET and
# RAZORPAY_KEY_SECRET into Secret Manager and reference them with
# `--set-secrets` instead of `--set-env-vars`.
#
# We use ^||^ as the env-var separator (instead of the default comma)
# because DATABASE_URL may contain commas or special characters.
echo "==> Deploying to Cloud Run"
gcloud run deploy "${SERVICE_NAME}" \
  --image="${IMAGE}:${TAG}" \
  --region="${GCP_REGION}" \
  --platform=managed \
  --allow-unauthenticated \
  --port=8080 \
  --min-instances=0 \
  --max-instances=3 \
  --memory=512Mi \
  --cpu=1 \
  --set-env-vars="^||^SPRING_PROFILES_ACTIVE=gcp||DATABASE_URL=${DATABASE_URL}||DB_USERNAME=${DB_USERNAME}||DB_PASSWORD=${DB_PASSWORD}||JWT_SECRET=${JWT_SECRET}||ALLOWED_ORIGINS=${ALLOWED_ORIGINS}||SWAGGER_ENABLED=${SWAGGER_ENABLED}||STORAGE_TYPE=${STORAGE_TYPE}||RAZORPAY_KEY_ID=${RAZORPAY_KEY_ID}||RAZORPAY_KEY_SECRET=${RAZORPAY_KEY_SECRET}"

echo "==> Done. Service URL:"
gcloud run services describe "${SERVICE_NAME}" --region="${GCP_REGION}" --format="value(status.url)"
