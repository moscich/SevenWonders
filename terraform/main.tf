provider "aws" {
  region = "us-east-1"
}

resource "random_id" "tf_bucket_id" {
  byte_length = 2
}

data "template_file" "config_template" {
  template = "${file("${path.module}/template.tpl")}"

  vars {
    db_password = "${random_string.password.result}"
  }
}

resource "aws_s3_bucket" "config_bucket" {
  bucket        = "wonders-config-bucket-${random_id.tf_bucket_id.dec}"
  acl           = "private"
  force_destroy = true

  tags {
    Name = "tf_bucket"
  }
}

resource "aws_s3_bucket_object" "object" {
  bucket  = "${aws_s3_bucket.config_bucket.bucket}"
  key     = "application.properties"
  content = "${data.template_file.config_template.rendered}"
}

resource "random_string" "password" {
  length = 16
  special = true
  override_special = "/@\" "
}

 