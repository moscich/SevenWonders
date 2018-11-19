resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  enable_dns_hostnames = true
}

data "aws_availability_zones" "available" {}

resource "aws_internet_gateway" "gw" {
  vpc_id = "${aws_vpc.main.id}"

  tags {
    Name = "main"
  }
}

resource "aws_subnet" "main" {
  vpc_id     = "${aws_vpc.main.id}"
  cidr_block = "10.0.1.0/24"
  availability_zone = "${data.aws_availability_zones.available.names[0]}"

  tags {
    Name = "Main"
  }
}

resource "aws_subnet" "secondary" {
  vpc_id     = "${aws_vpc.main.id}"
  cidr_block = "10.0.2.0/24"
  availability_zone = "${data.aws_availability_zones.available.names[1]}"

  tags {
    Name = "Main"
  }
}

resource "aws_route_table" "r" {
  vpc_id = "${aws_vpc.main.id}"

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = "${aws_internet_gateway.gw.id}"
  }

  tags {
    Name = "main"
  }
}

resource "aws_route_table_association" "primary" {
  subnet_id      = "${aws_subnet.main.id}"
  route_table_id = "${aws_route_table.r.id}"
}

resource "aws_route_table_association" "secondary" {
  subnet_id      = "${aws_subnet.secondary.id}"
  route_table_id = "${aws_route_table.r.id}"
}

resource "aws_security_group" "allow_all" {
  name        = "allow_all"
  description = "Allow all inbound traffic"
  vpc_id      = "${aws_vpc.main.id}"

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    cidr_blocks     = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "allow_ssh" {
  name        = "allow_ssh"
  description = "Allow ssh inbound traffic"
  vpc_id      = "${aws_vpc.main.id}"

  ingress {
    from_port       = 22
    to_port         = 22
    protocol        = "TCP"
    cidr_blocks = ["185.233.24.22/32"]
  }

  egress {
    from_port       = 0
    to_port         = 0
    protocol        = "-1"
    cidr_blocks     = ["0.0.0.0/0"]
  }

}

output "rds_security_group_id" {
  value = "${aws_security_group.allow_all.id}"
}

output "ec2_security_group_id" {
  value = "${aws_security_group.allow_ssh.id}"
}

output "main_subnet" {
  value = "${aws_subnet.main.id}"
}

output "secondary_subnet" {
  value = "${aws_subnet.secondary.id}"
}

