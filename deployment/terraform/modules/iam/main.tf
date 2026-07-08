# Execution role that ECS uses to pull the image from ECR and push logs to
# CloudWatch. This is the ONLY IAM role in this stack.
module "ecs_task_execution_role" {
  source  = "terraform-aws-modules/iam/aws//modules/iam-assumable-role"
  version = "~> 5.0"

  create_role = true
  role_name   = var.role_name

  # Not a human role, so no MFA requirement.
  role_requires_mfa = false

  trusted_role_services = [
    "ecs-tasks.amazonaws.com"
  ]

  custom_role_policy_arns = [
    "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
  ]

  number_of_custom_role_policy_arns = 1
}
