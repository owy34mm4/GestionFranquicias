# Security group for the VPC Link. It only needs to reach the ALB on port 80;
# the ALB's own ingress rule (VPC CIDR based) is what actually lets the
# traffic through, so this SG only needs the matching egress rule.
module "vpc_link_sg" {
  source  = "terraform-aws-modules/security-group/aws"
  version = "~> 5.3"

  name        = "franquicia-vpc-link"
  description = "Security group for the API Gateway VPC Link"
  vpc_id      = var.vpc_id

  egress_with_source_security_group_id = [
    {
      from_port                = 80
      to_port                  = 80
      protocol                 = "tcp"
      description              = "HTTP to the internal ALB"
      source_security_group_id = var.alb_sg_id
    }
  ]
}

# HTTP API in front of the internal ALB. This is the only public door into
# the app: API Gateway -> VPC Link -> internal ALB -> ECS.
module "api_gateway" {
  source  = "terraform-aws-modules/apigateway-v2/aws"
  version = "~> 5.5"

  name          = "franquicia-api"
  description   = "HTTP API in front of the franquicia ALB"
  protocol_type = "HTTP"

  create_domain_name = false

  vpc_links = {
    franquicia = {
      name               = "franquicia-vpc-link"
      security_group_ids = [module.vpc_link_sg.security_group_id]
      subnet_ids         = var.subnet_ids
    }
  }

  routes = {
    "ANY /{proxy+}" = {
      integration = {
        type                   = "HTTP_PROXY"
        connection_type        = "VPC_LINK"
        vpc_link_key           = "franquicia"
        method                 = "ANY"
        payload_format_version = "1.0"
        uri                    = var.alb_listener_arn
      }
    }
  }
}
