provider "aws" {
  region = "us-east-1"
}

# Deploy Storage Resource
module "storage" {
  source       = "./storage"
  security_group  = "${module.network.rds_security_group_id}"
  subnet = "${module.network.main_subnet}"
  subnet_secondary = "${module.network.secondary_subnet}"
}

# Deploy Compute Resource
module "compute" {
  source       = "./compute"
  security_group  = "${module.network.ec2_security_group_id}"
  subnet = "${module.network.main_subnet}"
  config_s3 = "${module.storage.config_bucket}"
}

# Deploy Network Resource
module "network" {
  source       = "./network"
}

output "db" {
  value = "${module.storage.db}"
}

output "config" {
  value = "${module.storage.config_bucket}"
}