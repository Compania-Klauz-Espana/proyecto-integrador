# IMPORTANTE: Antes de hacer terraform init, crear manualmente:
# az group create --name klauz-tfstate-rg --location eastus
# az storage account create --name klauztfstate --resource-group klauz-tfstate-rg --location eastus --sku Standard_LRS
# az storage container create --name tfstate --account-name klauztfstate

terraform {
  required_version = ">= 1.7.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 3.95"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "~> 2.47"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.6"
    }
  }
}
