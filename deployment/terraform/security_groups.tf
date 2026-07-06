# Security group for the ECS task. Opens port 8080 to the world because we
# access the app directly through the task's public IP (there is no ALB).
resource "aws_security_group" "ecs" {
  name        = "franquicia-ecs"
  description = "Allow inbound HTTP to the app on 8080"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "App port"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Security group for RDS. Only the ECS task is allowed to reach MySQL.
resource "aws_security_group" "rds" {
  name        = "franquicia-rds"
  description = "Allow MySQL access from the ECS task only"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description     = "MySQL from ECS"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs.id]
  }

  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
