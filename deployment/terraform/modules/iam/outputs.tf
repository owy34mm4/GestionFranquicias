output "execution_role_arn" {
  description = "ARN of the ECS task execution role"
  value       = module.ecs_task_execution_role.iam_role_arn
}
