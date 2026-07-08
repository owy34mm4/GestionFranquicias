# CloudWatch log group for the app container. The ECS task writes here via
# the awslogs log driver.
module "log_group" {
  source  = "terraform-aws-modules/cloudwatch/aws//modules/log-group"
  version = "~> 5.0"

  name              = var.log_group_name
  retention_in_days = var.retention_in_days
}
