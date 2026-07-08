variable "log_group_name" {
  description = "Name of the CloudWatch log group"
  type        = string
  default     = "/ecs/franquicia"
}

variable "retention_in_days" {
  description = "How many days to keep app logs"
  type        = number
  default     = 7
}
