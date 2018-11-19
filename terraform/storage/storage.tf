variable "security_group" {}
variable "subnet" {}
variable "subnet_secondary" {}

resource "aws_s3_bucket" "config_bucket" {
  bucket        = "wonders-config-bucket-${random_id.tf_bucket_id.dec}"
  acl           = "private"
  force_destroy = true

  tags {
    Name = "tf_bucket"
  }
}

data "template_file" "config_template" {
  template = "${file("${path.module}/template.tpl")}"

  vars {
    db_password = "${random_string.password.result}"
  }
}

resource "random_id" "tf_bucket_id" {
  byte_length = 2
}

resource "aws_s3_bucket_object" "object" {
  bucket  = "${aws_s3_bucket.config_bucket.bucket}"
  key     = "application.properties"
  content = "${data.template_file.config_template.rendered}"
}

resource "random_string" "password" {
  length = 16
  special = true
}

resource "aws_db_subnet_group" "default" {
  name       = "main"
  subnet_ids = ["${var.subnet}", "${var.subnet_secondary}"]

  tags {
    Name = "My DB subnet group"
  }
}

resource "aws_db_instance" "wonders_default" {
  allocated_storage    = 10
  storage_type         = "gp2"
  engine               = "postgres"
  instance_class       = "db.t2.micro"
  identifier           = "wonders"
  name                 = "wonders"
  username             = "wonders"
  password             = "${random_string.password.result}"
  vpc_security_group_ids = ["${var.security_group}"]
  db_subnet_group_name = "${aws_db_subnet_group.default.id}"
  publicly_accessible  = true
  parameter_group_name = "default.postgres10"
  final_snapshot_identifier = "wondersdbidentifier"
}

output "db" {
  value = "${aws_db_instance.wonders_default.endpoint}"
}

output "config_bucket" {
  value = "${aws_s3_bucket.config_bucket.id}"
}