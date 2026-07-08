variable "subnet_ids" {
  description = "Subnet ids for the ECS service"
  type        = list(string)
}

variable "image_url" {
  description = "ECR repository URL (without tag) to run in the task"
  type        = string
}

variable "execution_role_arn" {
  description = "ARN of the ECS task execution role"
  type        = string
}

variable "log_group_name" {
  description = "CloudWatch log group name for the app container"
  type        = string
}

variable "db_address" {
  description = "RDS instance address (hostname)"
  type        = string
}

variable "db_password" {
  description = "Database master password"
  type        = string
  sensitive   = true
}

variable "alb_target_group_arn" {
  description = "ARN of the ALB target group the service registers with"
  type        = string
}

variable "alb_sg_id" {
  description = "Security group id of the ALB, the only source allowed to reach the task on 8080"
  type        = string
}
