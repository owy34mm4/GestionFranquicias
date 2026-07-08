output "api_url" {
  description = "Invoke URL of the HTTP API"
  value       = module.api_gateway.api_endpoint
}
