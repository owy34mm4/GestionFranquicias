# Database master password. Provided at runtime via the DB_PASSWORD GitHub
# secret (exported as TF_VAR_db_password). Never hardcode a value here.
variable "db_password" {
  type      = string
  sensitive = true
}

# Fixed database settings. Kept as locals because they never change per env.
locals {
  db_username = "admin"
  db_name     = "franchise"
}
