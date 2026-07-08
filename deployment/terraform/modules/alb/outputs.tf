output "target_group_arn" {
  description = "ARN of the target group the ECS service registers with"
  value       = module.alb.target_groups["app"].arn
}

output "listener_arn" {
  description = "ARN of the HTTP listener, used by the API Gateway VPC Link integration"
  value       = module.alb.listeners["http"].arn
}

output "alb_sg_id" {
  description = "Security group id attached to the ALB"
  value       = module.alb.security_group_id
}

output "dns_name" {
  description = "DNS name of the internal ALB"
  value       = module.alb.dns_name
}
