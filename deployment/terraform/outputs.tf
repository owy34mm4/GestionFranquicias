output "ecr_repository_url" {
  description = "ECR repository URL to push the app image to"
  value       = module.ecr.repository_url
}

output "rds_endpoint" {
  description = "RDS instance address (hostname)"
  value       = module.rds.db_address
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = module.ecs.cluster_name
}

output "ecs_service_name" {
  description = "ECS service name"
  value       = module.ecs.service_name
}

output "alb_dns_name" {
  description = "DNS name of the internal ALB (only reachable from inside the VPC)"
  value       = module.alb.dns_name
}

output "api_gateway_url" {
  description = "Invoke URL of the API Gateway, the app's public entry point"
  value       = module.apigateway.api_url
}
