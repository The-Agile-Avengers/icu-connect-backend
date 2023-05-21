#!/bin/bash

echo "Please enter localstack PRO account API key:"
read localstack_api_key

echo -e "\n\n\n\n# stop and destroy existing containers"
docker stop $(docker ps -aq) && docker rm $(docker ps -aq)
echo -e "\033[32m\xE2\x9C\x94 Done\033[0m"

echo -e "\n\n\n\n# start localstack"

#!/bin/bash

# Open a new terminal and execute the Docker command
osascript <<END
tell application "Terminal"
    do script "docker run --rm -it -p 4566:4566 -p 4510-4559:4510-4559 -v /var/run/docker.sock:/var/run/docker.sock -e PERSISTENCE=1 -e LOCALSTACK_API_KEY=$localstack_api_key localstack/localstack-pro"
end tell
END


# Wait until localhost:4566 is available
echo -e "Listening for localstack startup"
echo -e "."
while ! nc -z localhost 4566; do
  echo -e "."
  sleep 0.5
done

# Proceed with commands in the script
echo -e "\n\n\n\nConnected to localstack (localhost:4566) \033[32m\xE2\x9C\x94 Done\033[0m"

echo -e "\n\n\n\nSetup Localstack Env"

echo -e "# setup files S3 bucket"
awslocal s3api create-bucket --bucket icufiles --create-bucket-configuration LocationConstraint=eu-west-1

echo -e "\n\n\n\n# setup RDS MySql database"
awslocal rds create-db-cluster --db-cluster-identifier dbcluster1 --engine mysql --database-name icudb1

echo -e "\033[32m\xE2\x9C\x94 Done\033[0m"

echo -e "\n\n\n\n# build maven project (JAR)"
mvn clean package
echo -e "\033[32m\xE2\x9C\x94 Done\033[0m"

echo -e "\n\n\n\n# register backend image in ECR"
awslocal ecr create-repository --repository-name icubackend
docker build -t localhost.localstack.cloud:4511/icubackend .
docker push localhost.localstack.cloud:4511/icubackend:latest
echo -e "\033[32m\xE2\x9C\x94 Done\033[0m"

echo -e "\n\n\n\n# setup ECS"
awslocal ecs create-cluster --cluster-name icubackend-cluster

echo -e "# create ECS Task Definition"
awslocal ecs register-task-definition \
    --family icubackend-task-definition \
    --network-mode awsvpc \
    --container-definitions '[
        {
            "name": "icubackend",
            "image": "localhost.localstack.cloud:4511/icubackend:latest",
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

echo -e "# create VPC"
vpc_id=$(awslocal ec2 create-vpc --cidr-block 10.0.0.0/16 --query 'Vpc.VpcId' --output text)

echo -e "# create subnets"
subnet1_id=$(awslocal ec2 create-subnet --vpc-id "$vpc_id" --cidr-block 10.0.1.0/24 --availability-zone eu-west-1a --query 'Subnet.SubnetId' --output text)
subnet2_id=$(awslocal ec2 create-subnet --vpc-id "$vpc_id" --cidr-block 10.0.2.0/24 --availability-zone eu-west-1b --query 'Subnet.SubnetId' --output text)

echo -e "# create security group"
security_group_id=$(awslocal ec2 create-security-group --group-name ecs-security-group --description "ECS Security Group" --vpc-id "$vpc_id" --query 'GroupId' --output text)

echo -e "# add inbound traffic rules to security group"
awslocal ec2 authorize-security-group-ingress --group-id "$security_group_id" --protocol tcp --port 8080 --cidr 0.0.0.0/0

echo -e "# create ECS service"
awslocal ecs create-service \
    --cluster icubackend-cluster \
    --service-name icubackend-service \
    --task-definition icubackend-task-definition \
    --desired-count 1 \
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

echo -e "\033[32m\xE2\x9C\x94 Done\033[0m"

# Set the initial value of the port variable to an empty string
port=""

# Loop until a container with port 8080 mapped is found
while [ -z "$port" ]
do
  # Run the docker ps command to find a container with port 8080 mapped
  port=$(docker ps --format "table {{.Names}}\t{{.Ports}}" | grep "8080/tcp" | awk '{print $NF}' | cut -d'-' -f1)

  # If the port variable is still empty, wait for 2 seconds before trying again
  if [ -z "$port" ]
  then
    sleep 2
  fi
done

# The port variable now contains the host port that maps to port 8080 in a container
GREEN='\033[0;32m'
NO_COLOR='\033[0m'

echo -e "${GREEN}  ___ _   _  ___ ___ ___ ___ ___ "
echo -e " / __| | | |/ __/ __| __/ __/ __|"
echo -e " \__ | |_| | (_| (__| _|\__ \__ \\"
echo -e " |___/\___/ \___\___|___|___|___/${NO_COLOR}"
echo -e "                                 "

echo -e "The ECS container port is $port"

port_only=${port:8}
echo -e "\nGo to the frontend repository and run the following command to start the ICU-CONNECT client:\n"
echo -e "REACT_APP_BACKEND_PORT=$port_only npm start"
echo -e "\n"
