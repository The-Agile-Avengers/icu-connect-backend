#!/bin/bash

echo "Setup Localstack Env"

echo "# setup frontend S3 bucket"
awslocal s3api create-bucket --bucket icufrontend --create-bucket-configuration LocationConstraint=eu-west-1

echo "# setup RDS MySql database"
awslocal rds create-db-cluster --db-cluster-identifier dbcluster1 --engine mysql --database-name icudb1

echo "# build maven project (JAR)"
mvn package clean

echo "# register backend image in ECR"
awslocal ecr create-repository --repository-name icubackend
docker build -t localhost:4511/icubackend .
docker push localhost:4511/icubackend:latest

echo "# setup ECS"
awslocal ecs create-cluster --cluster-name icubackend-cluster

echo "# create ECS Task Definition"
awslocal ecs register-task-definition \
    --family icubackend-task-definition \
    --network-mode awsvpc \
    --container-definitions '[
        {
            "name": "icubackend",
            "image": "localhost.localstack.cloud:4511/icubackend:5",
            "essential": true,
            "portMappings": [
                {
                    "containerPort": 8080,
                    "protocol": "tcp"
                }
            ],
            "logConfiguration": {
                "logDriver": "awslogs",
                "options": {
                    "awslogs-group": "<your-log-group>",
                    "awslogs-region": "eu-west-1",
                    "awslogs-stream-prefix": "icubackend"
                }
            }
        }
    ]' \
    --requires-compatibilities FARGATE

echo "# create VPC"
vpc_id=$(awslocal ec2 create-vpc --cidr-block 10.0.0.0/16 --query 'Vpc.VpcId' --output text)

echo "# create subnets"
subnet1_id=$(awslocal ec2 create-subnet --vpc-id "$vpc_id" --cidr-block 10.0.1.0/24 --availability-zone eu-west-1a --query 'Subnet.SubnetId' --output text)
subnet2_id=$(awslocal ec2 create-subnet --vpc-id "$vpc_id" --cidr-block 10.0.2.0/24 --availability-zone eu-west-1b --query 'Subnet.SubnetId' --output text)

echo "# create security group"
security_group_id=$(awslocal ec2 create-security-group --group-name ecs-security-group --description "ECS Security Group" --vpc-id "$vpc_id" --query 'GroupId' --output text)

echo "# add inbound traffic rules to security group"
awslocal ec2 authorize-security-group-ingress --group-id "$security_group_id" --protocol tcp --port 8080 --cidr 0.0.0.0/0

echo "# create ECS service"
awslocal ecs create-service \
    --cluster icubackend-cluster \
    --service-name icubackend-service \
    --task-definition icubackend-task-definition \
    --desired-count 5 \
    --launch-type FARGATE \
    --platform-version LATEST \
    --deployment-controller type=ECS \
    --network-configuration "{
        \"awsvpcConfiguration\": {
            \"subnets\": [
                \"$subnet1_id\",
                \"$subnet2_id\"
            ],
            \"securityGroups\": [
                \"$security_group_id\"
            ],
            \"assignPublicIp\": \"ENABLED\"
        }
    }" \
    --scheduling-strategy REPLICA
