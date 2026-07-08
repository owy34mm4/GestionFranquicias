output "db_address" {
  description = "RDS instance address (hostname)"
  value       = module.rds.db_instance_address
}

output "rds_sg_id" {
  description = "Security group id attached to the RDS instance"
  value       = aws_security_group.rds.id
}
