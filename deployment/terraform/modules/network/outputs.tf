output "vpc_id" {
  description = "Default VPC id"
  value       = data.aws_vpc.default.id
}

output "vpc_cidr" {
  description = "Default VPC CIDR block"
  value       = data.aws_vpc.default.cidr_block
}

output "subnet_ids" {
  description = "Subnet ids in the default VPC"
  value       = data.aws_subnets.default.ids
}

output "region" {
  description = "Current AWS region"
  value       = data.aws_region.current.name
}

output "account_id" {
  description = "Current AWS account id"
  value       = data.aws_caller_identity.current.account_id
}
