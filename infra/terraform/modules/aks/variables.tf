variable "prefix"               { type = string }
variable "environment"          { type = string }
variable "location"             { type = string }
variable "resource_group_name"  { type = string }
variable "aks_subnet_id"        { type = string }

variable "kubernetes_version" {
  type    = string
  default = "1.29"
}

variable "node_count" {
  type    = number
  default = 1
}

variable "node_vm_size" {
  type    = string
  default = "Standard_B2s"
}

variable "enable_app_node_pool" {
  type    = bool
  default = false
}

variable "app_node_vm_size" {
  type    = string
  default = "Standard_B2s"
}

variable "app_node_count" {
  type    = number
  default = 1
}

variable "tags" {
  type    = map(string)
  default = {}
}
