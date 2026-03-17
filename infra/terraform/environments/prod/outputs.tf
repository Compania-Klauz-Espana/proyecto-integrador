output "resource_group"   { value = module.networking.resource_group_name }
output "aks_cluster_name" { value = module.aks.cluster_name }
output "acr_login_server" { value = module.acr.login_server }
output "db_fqdn"          { value = module.database.db_fqdn }
output "key_vault_uri"    { value = module.database.key_vault_uri }
