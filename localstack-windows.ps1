Write-Host "Listening for localstack startup"
Write-Host "." -NoNewline
awslocal ecs register-task-definition --cli-input-json file://./containerDefinitions.json
$family = 'icubackend-task-definition'
$containerDefinitions = '[ { "name": "icubackend", "image": "localhost.localstack.cloud:4511/icubackend:latest", "essential": true, "portMappings": [ { "containerPort": 8080, "protocol": "tcp" } ], "logConfiguration": { "logDriver": "awslogs", "options": { "awslogs-group": "<your-log-group>", "awslogs-region": "eu-west-1", "awslogs-stream-prefix": "icubackend" } } } ]'

while ((Test-NetConnection -ComputerName localhost -Port 4566 -InformationLevel Quiet) -ne $true)
{
    Write-Host "." -NoNewline
    Start-Sleep -s 0.5
    Write-Host "Connected to localstack (localhost:4566)" -ForegroundColor Green
    
    Write-Host "Setup Localstack Env"
}
Write-Host "Setup ECS"
awslocal ecs create-cluster --cluster-name icubackend-cluster
Write-Host "# Done:ECS Setup"

awslocal s3api create-bucket --bucket icufiles --create-bucket-configuration LocationConstraint=eu-west-1
Write-Host "# Setup file store S3 bucket"
Write-Host "S3 bucket icufiles created" -ForegroundColor Green

awslocal rds create-db-cluster --db-cluster-identifier dbcluster1 --engine mysql --database-name icudb1
Write-Host "# setup RDS MySql database"
Write-Host "RDS MySql database created" -ForegroundColor Green

mvn clean package
Write-Host "# build maven project (JAR)"
Write-Host "Maven project built successfully" -ForegroundColor Green

awslocal ecr create-repository --repository-name icubackend
docker build -t localhost:4511/icubackend .
docker push localhost:4511/icubackend:latest
Write-Host "# register backend image in ECR"
Write-Host "Backend image registered in ECR" -ForegroundColor Green

Write-Host "ECS cluster created" -ForegroundColor Green

Write-Host "Register task definition"
awslocal ecs register-task-definition --cli-input-json file://./containerDefinitions.json

$vpc_id = (awslocal ec2 create-vpc --cidr-block 10.0.0.0/16 --query 'Vpc.VpcId' --output text)

$subnet1_id = (awslocal ec2 create-subnet --vpc-id $vpc_id --cidr-block 10.0.1.0/24 --availability-zone eu-west-1a --query 'Subnet.SubnetId' --output text)
$subnet2_id = (awslocal ec2 create-subnet --vpc-id $vpc_id --cidr-block 10.0.2.0/24 --availability-zone eu-west-1b --query 'Subnet.SubnetId' --output text)

$security_group_id = (awslocal ec2 create-security-group --group-name ecs-security-group --description "ECS Security Group" --vpc-id $vpc_id --query 'GroupId' --output text)

awslocal ec2 authorize-security-group-ingress --group-id $security_group_id --protocol tcp --port 8080 --cidr 0.0.0.0/0
awslocal ecs create-service --cluster icubackend-cluster --service-name icubackend-service --task-definition icubackend-task-definition --desired-count 1 --launch-type FARGATE --platform-version LATEST --deployment-controller type=ECS --network-configuration "`"awsvpcConfiguration`"={`"subnets`"=[`"$subnet1_id`", `"$subnet2_id`"],`"securityGroups`"=[`"$security_group_id`"],`"assignPublicIp`"=`"ENABLED`"}" --scheduling-strategy REPLICA
Write-Host "Done"

$port = ""

while (-not $port)
{
    $port = docker ps --format "table {{.Names}}\t{{.Ports}}" | Select-String "8080/tcp" | ForEach-Object { $_.ToString().Split(' ')[-1].Split('-')[0] }
    if (-not $port) {
        Start-Sleep -Seconds 2
        }
}

Write-Host "The ECS container port is $port"


Write-Host "██╗ ██████╗██╗   ██╗"
Write-Host "██║██╔════╝██║   ██║"
Write-Host "██║██║     ██║   ██║"
Write-Host "██║██║     ██║   ██║"
Write-Host "██║╚██████╗╚██████╔╝"
Write-Host "╚═╝ ╚═════╝ ╚═════╝  Fachverein Informatik"

Write-Host " ______                 ___                ______                                                            "
Write-Host "/\  _  \            __ /\_ \              /\  _  \                                                           "
Write-Host "\ \ \L\ \      __  /\_\\//\ \       __    \ \ \L\ \   __  __     __     ___       __       __   _ __   ____  "
Write-Host " \ \  __ \   /'_ `\\/\ \ \ \ \    /'__`\   \ \  __ \ /\ \/\ \  /'__`\ /' _ `\   /'_ `\   /'__`\/\`'__\/',__\ "
Write-Host "  \ \ \/\ \ /\ \L\ \\ \ \ \_\ \_ /\  __/    \ \ \/\ \\ \ \_/ |/\  __/ /\ \/\ \ /\ \L\ \ /\  __/\ \ \//\__, `\"
Write-Host "   \ \_\ \_\\ \____ \\ \_\/\____\\ \____\    \ \_\ \_\\ \___/ \ \____\\ \_\ \_\\ \____ \\ \____\\ \_\\/\____/"
Write-Host "    \/_/\/_/ \/___L\ \\/_/\/____/ \/____/     \/_/\/_/ \/__/   \/____/ \/_/\/_/ \/___L\ \\/____/ \/_/ \/___/ "
Write-Host "               /\____/                                                            /\____/                    "
Write-Host "               \_/__/                                                             \_/__/                     "
Write-Host ""

Write-Host "Environment is running. You can close this window"