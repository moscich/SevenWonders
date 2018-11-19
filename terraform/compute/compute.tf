variable "security_group" {}
variable "subnet" {}
variable "config_s3" {}

resource "aws_instance" "wonders_api" {
  ami           = "ami-05b85382051785818"
  instance_type = "t2.micro"
  subnet_id = "${var.subnet}"
  vpc_security_group_ids  = ["${var.security_group}"]
  key_name = "marekec2"
  user_data = <<-EOF
                #!/bin/bash
  				echo "CONFIG_S3=${var.config_s3}" >> /etc/environment
  	EOF
  tags {
    Name = "wonders_api"
  }
}

resource "aws_eip" "eip" {
  instance = "${aws_instance.wonders_api.id}"
  vpc      = true
}