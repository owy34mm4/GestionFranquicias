# Use the account's default VPC and its subnets so we don't have to create
# networking ourselves. Good enough for a simple Fargate deployment behind
# an internal ALB + API Gateway.
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "all" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

data "aws_subnet" "each" {
  for_each = toset(data.aws_subnets.all.ids)
  id       = each.value
}

locals {
  # API Gateway VPC Link is not available in use1-az3, so drop any subnet there.
  usable_subnet_ids = [
    for id, s in data.aws_subnet.each : id
    if s.availability_zone_id != "use1-az3"
  ]
}

data "aws_caller_identity" "current" {}

data "aws_region" "current" {}
