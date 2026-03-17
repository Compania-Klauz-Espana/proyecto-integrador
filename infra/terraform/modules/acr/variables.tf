variable "prefix"               { type = string }
variable "environment"          { type = string }
variable "location"             { type = string }
variable "resource_group_name"  { type = string }
variable "acr_sku"              { type = string default = "Basic" }
variable "aks_kubelet_identity" { type = string }
variable "tags"                 { type = map(string) default = {} }
