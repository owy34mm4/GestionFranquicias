variable "vpc_id" {
  description = "VPC id where the VPC Link security group is created"
  type        = string
}

variable "subnet_ids" {
  description = "Subnet ids for the VPC Link"
  type        = list(string)
}

variable "alb_sg_id" {
  description = "Security group id of the internal ALB, the VPC Link is allowed to reach it"
  type        = string
}

variable "alb_listener_arn" {
  description = "ARN of the ALB HTTP listener the API Gateway routes into"
  type        = string
}
