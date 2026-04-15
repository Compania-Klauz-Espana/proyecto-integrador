variable "prefix"               { type = string }
variable "environment"          { type = string }
variable "location"             { type = string }
variable "resource_group_name"  { type = string }
variable "db_subnet_id"         { type = string }
variable "postgres_dns_zone_id" { type = string }
variable "tenant_id"            { type = string }

variable "db_admin_user" {
  type    = string
  default = "klauzadmin"
}

variable "db_sku" {
  type    = string
  default = "B_Standard_B1ms"
}

variable "db_storage_mb" {
  type    = number
  default = 32768
}

variable "tags" {
  type    = map(string)
  default = {}
}
