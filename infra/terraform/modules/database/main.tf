resource "random_password" "postgres" {
  length           = 20
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

resource "azurerm_postgresql_flexible_server" "main" {
  name                          = "${var.prefix}-${var.environment}-db"
  resource_group_name           = var.resource_group_name
  location                      = var.location
  version                       = "16"
  delegated_subnet_id           = var.db_subnet_id
  private_dns_zone_id           = var.postgres_dns_zone_id
  public_network_access_enabled = false

  administrator_login    = var.db_admin_user
  administrator_password = random_password.postgres.result

  sku_name   = var.db_sku
  storage_mb = var.db_storage_mb

  backup_retention_days        = var.environment == "prod" ? 35 : 7
  geo_redundant_backup_enabled = false

  high_availability {
    mode = var.environment == "prod" ? "ZoneRedundant" : "Disabled"
  }

  maintenance_window {
    day_of_week  = 0
    start_hour   = 2
    start_minute = 0
  }

  tags = var.tags
}

resource "azurerm_postgresql_flexible_server_database" "app" {
  name      = "appdb"
  server_id = azurerm_postgresql_flexible_server.main.id
  collation = "en_US.utf8"
  charset   = "utf8"
}

resource "azurerm_postgresql_flexible_server_configuration" "max_connections" {
  name      = "max_connections"
  server_id = azurerm_postgresql_flexible_server.main.id
  value     = "100"
}

# Guardar password en Azure Key Vault (buena práctica)
resource "azurerm_key_vault" "main" {
  name                = "${var.prefix}${var.environment}kv"
  location            = var.location
  resource_group_name = var.resource_group_name
  tenant_id           = var.tenant_id
  sku_name            = "standard"
  tags                = var.tags

  purge_protection_enabled   = var.environment == "prod" ? true : false
  soft_delete_retention_days = 7
}

resource "azurerm_key_vault_secret" "db_password" {
  name         = "db-password"
  value        = random_password.postgres.result
  key_vault_id = azurerm_key_vault.main.id
}

resource "azurerm_key_vault_secret" "db_url" {
  name         = "db-connection-string"
  value        = "jdbc:postgresql://${azurerm_postgresql_flexible_server.main.fqdn}:5432/appdb"
  key_vault_id = azurerm_key_vault.main.id
}
