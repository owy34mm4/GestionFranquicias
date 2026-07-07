# Container registry for the app image. Uses the official ECR module.
# Tag mutability is MUTABLE on purpose: the CD pipeline overwrites ":latest".
module "ecr" {
  source = "terraform-aws-modules/ecr/aws"
  # Pinned to 2.x: the 3.x line requires AWS provider v6, but we use v5.
  version = "~> 2.4"

  repository_name                 = var.repository_name
  repository_image_tag_mutability = "MUTABLE"
  repository_image_scan_on_push   = true

  # Keep the registry tidy: drop untagged images after 1 day.
  repository_lifecycle_policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Expire untagged images"
        selection = {
          tagStatus   = "untagged"
          countType   = "sinceImagePushed"
          countUnit   = "days"
          countNumber = 1
        }
        action = {
          type = "expire"
        }
      }
    ]
  })
}
