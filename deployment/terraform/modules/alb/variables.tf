variable "vpc_id" {
  description = "VPC id where the ALB and its security group are created"
  type        = string
}

variable "vpc_cidr" {
  description = "CIDR block of the VPC, used to scope the ALB ingress rule"
  type        = string
}

variable "subnet_ids" {
  description = "Subnet ids for the ALB"
  type        = list(string)
}
