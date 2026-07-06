# MySQL database using the official RDS module.
#
# Pinned to the 6.x line on purpose: from v7 the module removed the plain
# "password" input in favor of a write-only "password_wo" (ephemeral value).
# We want to pass the password as a normal sensitive variable from a GitHub
# secret, so 6.13.x is the right fit.
#
# Engine note: if MySQL 8.4 is not available in the target account/region,
# fall back to engine_version = "8.0", family = "mysql8.0",
# major_engine_version = "8.0".
module "rds" {
  source  = "terraform-aws-modules/rds/aws"
  version = "~> 6.13"

  identifier = "franquicia"

  engine               = "mysql"
  engine_version       = "8.4"
  family               = "mysql8.4"
  major_engine_version = "8.4"
  instance_class       = "db.t3.micro"

  allocated_storage = 20
  storage_type      = "gp3"
  storage_encrypted = false

  db_name  = local.db_name
  username = local.db_username
  password = var.db_password
  port     = 3306

  # We manage the password ourselves via var.db_password, so this stays false.
  manage_master_user_password = false

  publicly_accessible     = false
  multi_az                = false
  backup_retention_period = 0
  skip_final_snapshot     = true

  create_db_subnet_group = true
  subnet_ids             = data.aws_subnets.default.ids
  vpc_security_group_ids = [aws_security_group.rds.id]
}
