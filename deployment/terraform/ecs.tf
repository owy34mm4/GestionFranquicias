resource "aws_ecs_cluster" "this" {
  name = "franquicia-cluster"
}

# Task definition for the Spring Boot app. Runs on Fargate, logs to
# CloudWatch, and connects to RDS through R2DBC environment variables.
resource "aws_ecs_task_definition" "this" {
  family                   = "franquicia-task"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 256
  memory                   = 512
  execution_role_arn       = aws_iam_role.ecs_task_execution.arn

  container_definitions = jsonencode([
    {
      name      = "franquicia-app"
      image     = "${module.ecr.repository_url}:latest"
      essential = true

      portMappings = [
        {
          containerPort = 8080
          protocol      = "tcp"
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = "/ecs/franquicia"
          "awslogs-region"        = "us-east-1"
          "awslogs-stream-prefix" = "ecs"
        }
      }

      environment = [
        {
          name  = "SPRING_R2DBC_URL"
          value = "r2dbc:mysql://${module.rds.db_instance_address}:3306/franchise"
        },
        {
          name  = "SPRING_R2DBC_USERNAME"
          value = "admin"
        },
        {
          name  = "SPRING_R2DBC_PASSWORD"
          value = var.db_password
        },
        {
          name  = "SPRING_SQL_INIT_MODE"
          value = "always"
        }
      ]
    }
  ])
}

# Service that keeps one task running. The task gets a public IP so the app
# is reachable directly (no ALB).
resource "aws_ecs_service" "this" {
  name            = "franquicia-service"
  cluster         = aws_ecs_cluster.this.id
  task_definition = aws_ecs_task_definition.this.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = data.aws_subnets.default.ids
    security_groups  = [aws_security_group.ecs.id]
    assign_public_ip = true
  }

  deployment_minimum_healthy_percent = 0
  deployment_maximum_percent         = 200

  # The database must exist before the app starts trying to connect.
  depends_on = [module.rds]
}
