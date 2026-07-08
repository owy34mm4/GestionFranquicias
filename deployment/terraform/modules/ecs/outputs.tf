output "ecs_sg_id" {
  description = "Security group id of the ECS service tasks"
  value       = module.ecs_service.security_group_id
}

output "cluster_name" {
  description = "ECS cluster name"
  value       = module.ecs_cluster.name
}

output "service_name" {
  description = "ECS service name"
  value       = module.ecs_service.name
}
