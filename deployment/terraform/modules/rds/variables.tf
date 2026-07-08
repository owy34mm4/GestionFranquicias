variable "vpc_id" {
  description = "VPC id where the RDS security group is created"
  type        = string
}

variable "subnet_ids" {
  description = "Subnet ids for the RDS subnet group"
  type        = list(string)
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "franchise"
}

variable "db_username" {
  description = "Database master username"
  type        = string
  default     = "admin"
}

variable "db_password" {
  description = "Database master password"
  type        = string
  sensitive   = true
}
