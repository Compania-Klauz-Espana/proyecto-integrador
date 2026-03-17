output "resource_group_name"     { value = azurerm_resource_group.main.name }
output "resource_group_location" { value = azurerm_resource_group.main.location }
output "vnet_id"                 { value = azurerm_virtual_network.main.id }
output "aks_subnet_id"           { value = azurerm_subnet.aks.id }
output "db_subnet_id"            { value = azurerm_subnet.database.id }
output "postgres_dns_zone_id"    { value = azurerm_private_dns_zone.postgres.id }
output "tags"                    { value = local.tags }
