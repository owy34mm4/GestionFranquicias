output "log_group_name" {
  description = "Name of the CloudWatch log group"
  value       = module.log_group.cloudwatch_log_group_name
}
