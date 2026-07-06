output "ecr_repository_url" {
  description = "ECR repository URL to push the app image to"
  value       = module.ecr.repository_url
}

output "rds_endpoint" {
  description = "RDS instance address (hostname)"
  value       = module.rds.db_instance_address
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = aws_ecs_cluster.this.name
}

output "ecs_service_name" {
  description = "ECS service name"
  value       = aws_ecs_service.this.name
}

# The Fargate task's public IP is EPHEMERAL: there is no ALB or Elastic IP,
# so it changes on every deploy. That is why we do not output a static value
# here. Instead we output the commands to look up the current IP at runtime.
output "public_ip_command" {
  description = "AWS CLI commands to fetch the running task's public IP"
  value       = <<-EOT
    TASK_ARN=$(aws ecs list-tasks --cluster franquicia-cluster --service-name franquicia-service --query 'taskArns[0]' --output text)
    ENI_ID=$(aws ecs describe-tasks --cluster franquicia-cluster --tasks $TASK_ARN --query 'tasks[0].attachments[0].details[?name==`networkInterfaceId`].value' --output text)
    aws ec2 describe-network-interfaces --network-interface-ids $ENI_ID --query 'NetworkInterfaces[0].Association.PublicIp' --output text
  EOT
}
