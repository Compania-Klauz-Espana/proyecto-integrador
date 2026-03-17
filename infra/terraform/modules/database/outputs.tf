output "db_fqdn"       { value = azurerm_postgresql_flexible_server.main.fqdn }
output "db_name"       { value = azurerm_postgresql_flexible_server_database.app.name }
output "db_admin_user" { value = azurerm_postgresql_flexible_server.main.administrator_login }
output "key_vault_id"  { value = azurerm_key_vault.main.id }
output "key_vault_uri" { value = azurerm_key_vault.main.vault_uri }
