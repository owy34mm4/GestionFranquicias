output "repository_url" {
  description = "ECR repository URL to push the app image to"
  value       = module.ecr.repository_url
}
