# Internal ALB in front of the ECS service. Nothing public reaches it
# directly: the only public door is the API Gateway, which talks to this ALB
# through a VPC Link.
module "alb" {
  source  = "terraform-aws-modules/alb/aws"
  version = "~> 9.17"

  name    = "franquicia-alb"
  vpc_id  = var.vpc_id
  subnets = var.subnet_ids

  internal = true

  create_security_group = true

  # Ingress from the VPC CIDR (not from the VPC Link security group id) to
  # break the ecs<->alb<->vpc-link cycle. The VPC Link SG has the matching
  # egress rule pointed at this ALB's SG id.
  security_group_ingress_rules = {
    http_from_vpc = {
      from_port   = 80
      to_port     = 80
      ip_protocol = "tcp"
      cidr_ipv4   = var.vpc_cidr
      description = "HTTP from inside the VPC (API Gateway VPC Link)"
    }
  }

  security_group_egress_rules = {
    all_outbound = {
      ip_protocol = "-1"
      cidr_ipv4   = "0.0.0.0/0"
      description = "Allow all outbound"
    }
  }

  listeners = {
    http = {
      port     = 80
      protocol = "HTTP"
      forward = {
        target_group_key = "app"
      }
    }
  }

  target_groups = {
    app = {
      name_prefix       = "app-"
      protocol          = "HTTP"
      port              = 8080
      target_type       = "ip"
      create_attachment = false

      health_check = {
        enabled             = true
        path                = "/actuator/health"
        matcher             = "200"
        healthy_threshold   = 3
        unhealthy_threshold = 3
        interval            = 30
        timeout             = 5
      }
    }
  }
}
