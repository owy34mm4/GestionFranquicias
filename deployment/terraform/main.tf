# Root module: wires every local module together.
#
# Topology: API Gateway (HTTP) -> VPC Link -> internal ALB (:80) ->
# ECS Fargate (:8080) -> RDS MySQL. The only public door is the API Gateway.

module "network" {
  source = "./modules/network"
}

module "ecr" {
  source = "./modules/ecr"
}

module "iam" {
  source = "./modules/iam"
}

module "logs" {
  source = "./modules/logs"
}

module "rds" {
  source = "./modules/rds"

  vpc_id      = module.network.vpc_id
  subnet_ids  = module.network.subnet_ids
  db_password = var.db_password
}

module "alb" {
  source = "./modules/alb"

  vpc_id     = module.network.vpc_id
  vpc_cidr   = module.network.vpc_cidr
  subnet_ids = module.network.subnet_ids
}

module "apigateway" {
  source = "./modules/apigateway"

  vpc_id           = module.network.vpc_id
  subnet_ids       = module.network.subnet_ids
  alb_sg_id        = module.alb.alb_sg_id
  alb_listener_arn = module.alb.listener_arn
}

module "ecs" {
  source = "./modules/ecs"

  subnet_ids           = module.network.subnet_ids
  image_url            = module.ecr.repository_url
  execution_role_arn   = module.iam.execution_role_arn
  log_group_name       = module.logs.log_group_name
  db_address           = module.rds.db_address
  db_password          = var.db_password
  alb_target_group_arn = module.alb.target_group_arn
  alb_sg_id            = module.alb.alb_sg_id
}

# Cycle break: ecs needs rds.db_address (to build the R2DBC URL) and rds
# would need ecs's security group id for its MySQL ingress rule. Breaking
# that cycle by keeping the rule out of both modules and creating it here,
# once both security group ids exist.
resource "aws_vpc_security_group_ingress_rule" "rds_from_ecs" {
  security_group_id            = module.rds.rds_sg_id
  referenced_security_group_id = module.ecs.ecs_sg_id
  from_port                    = 3306
  to_port                      = 3306
  ip_protocol                  = "tcp"
  description                  = "MySQL from the ECS task"
}
