# ECS cluster using the official module (Fargate only, no EC2 capacity).
module "ecs_cluster" {
  source  = "terraform-aws-modules/ecs/aws//modules/cluster"
  version = "~> 5.0"

  cluster_name = "franquicia-cluster"

  # We already manage app logs through the logs module; skip the extra
  # cluster-level log group the module would create by default.
  create_cloudwatch_log_group = false

  fargate_capacity_providers = {
    FARGATE = {}
  }
}

# Service that keeps one task running behind the internal ALB.
module "ecs_service" {
  source  = "terraform-aws-modules/ecs/aws//modules/service"
  version = "~> 5.0"

  name        = "franquicia-service"
  cluster_arn = module.ecs_cluster.arn

  cpu    = 256
  memory = 512

  launch_type = "FARGATE"

  # We already have an execution role from the iam module, don't create
  # another one.
  create_task_exec_iam_role = false
  task_exec_iam_role_arn    = var.execution_role_arn

  container_definitions = {
    franquicia-app = {
      image     = "${var.image_url}:latest"
      essential = true

      port_mappings = [
        {
          containerPort = 8080
          protocol      = "tcp"
        }
      ]

      # We create the log group ourselves in the logs module.
      create_cloudwatch_log_group = false
      log_configuration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = var.log_group_name
          "awslogs-region"        = "us-east-1"
          "awslogs-stream-prefix" = "ecs"
        }
      }

      environment = [
        {
          name  = "SPRING_R2DBC_URL"
          value = "r2dbc:mysql://${var.db_address}:3306/franchise"
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
  }

  load_balancer = {
    service = {
      target_group_arn = var.alb_target_group_arn
      container_name   = "franquicia-app"
      container_port   = 8080
    }
  }

  subnet_ids       = var.subnet_ids
  assign_public_ip = true
  desired_count    = 1

  enable_autoscaling = false

  # Task security group: only the ALB may reach the app on 8080.
  security_group_rules = {
    alb_ingress = {
      type                     = "ingress"
      from_port                = 8080
      to_port                  = 8080
      protocol                 = "tcp"
      source_security_group_id = var.alb_sg_id
    }
    egress_all = {
      type        = "egress"
      from_port   = 0
      to_port     = 0
      protocol    = "-1"
      cidr_blocks = ["0.0.0.0/0"]
    }
  }
}
