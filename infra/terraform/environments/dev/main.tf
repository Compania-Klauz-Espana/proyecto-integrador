terraform {
  backend "local" {}
}

provider "azurerm" {
  subscription_id = var.subscription_id
  features {
    key_vault {
      purge_soft_delete_on_destroy    = true
      recover_soft_deleted_key_vaults = true
    }
    resource_group {
      prevent_deletion_if_contains_resources = false
    }
  }
}

module "networking" {
  source          = "../../modules/networking"
  prefix          = var.prefix
  environment     = var.environment
  location        = var.location
  vnet_cidr       = var.vnet_cidr
  aks_subnet_cidr = var.aks_subnet_cidr
  db_subnet_cidr  = var.db_subnet_cidr
}

module "aks" {
  source               = "../../modules/aks"
  prefix               = var.prefix
  environment          = var.environment
  location             = var.location
  resource_group_name  = module.networking.resource_group_name
  aks_subnet_id        = module.networking.aks_subnet_id
  kubernetes_version   = var.kubernetes_version
  node_count           = var.node_count
  node_vm_size         = var.node_vm_size
  enable_app_node_pool = false
  tags                 = module.networking.tags
}

module "acr" {
  source               = "../../modules/acr"
  prefix               = var.prefix
  environment          = var.environment
  location             = var.location
  resource_group_name  = module.networking.resource_group_name
  acr_sku              = var.acr_sku
  aks_kubelet_identity = module.aks.kubelet_identity
  tags                 = module.networking.tags
}

module "database" {
  source               = "../../modules/database"
  prefix               = var.prefix
  environment          = var.environment
  location             = var.location
  resource_group_name  = module.networking.resource_group_name
  db_subnet_id         = module.networking.db_subnet_id
  postgres_dns_zone_id = module.networking.postgres_dns_zone_id
  tenant_id            = var.tenant_id
  db_sku               = var.db_sku
  db_storage_mb        = var.db_storage_mb
  tags                 = module.networking.tags
}
