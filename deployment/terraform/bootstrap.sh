#!/usr/bin/env bash
# Creates the remote state backend: an S3 bucket for the state file and a
# DynamoDB table for state locking. Run this ONCE before "terraform init".
# Safe to re-run: it ignores resources that already exist.
set -e

REGION="us-east-1"
BUCKET="franquicia-tfstate-owy34mm4"
TABLE="franquicia-tf-lock"

echo ">> Creating S3 bucket: ${BUCKET}"
if aws s3api head-bucket --bucket "${BUCKET}" 2>/dev/null; then
  echo "   Bucket already exists, skipping."
else
  # us-east-1 does NOT take a LocationConstraint.
  aws s3api create-bucket --bucket "${BUCKET}" --region "${REGION}"
  echo "   Bucket created."
fi

echo ">> Enabling versioning on the bucket"
aws s3api put-bucket-versioning \
  --bucket "${BUCKET}" \
  --versioning-configuration Status=Enabled
echo "   Versioning enabled."

echo ">> Creating DynamoDB lock table: ${TABLE}"
if aws dynamodb describe-table --table-name "${TABLE}" --region "${REGION}" >/dev/null 2>&1; then
  echo "   Table already exists, skipping."
else
  aws dynamodb create-table \
    --table-name "${TABLE}" \
    --attribute-definitions AttributeName=LockID,AttributeType=S \
    --key-schema AttributeName=LockID,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region "${REGION}"
  echo "   Table created."
fi

echo ">> Bootstrap done. You can now run: terraform init"
