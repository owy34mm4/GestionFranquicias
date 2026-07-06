# CloudWatch log group for the app container. The ECS task writes here via
# the awslogs log driver.
resource "aws_cloudwatch_log_group" "app" {
  name              = "/ecs/franquicia"
  retention_in_days = 7
}
