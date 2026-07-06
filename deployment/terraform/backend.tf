terraform {
  backend "s3" {
    bucket         = "franquicia-tfstate-owy34mm4"
    key            = "franquicia/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "franquicia-tf-lock"
    encrypt        = true
  }
}
