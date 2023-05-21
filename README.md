# icu-connect-backend
[![Stable Build](https://github.com/The-Agile-Avengers/icu-connect-backend/actions/workflows/maven.yml/badge.svg)](https://github.com/The-Agile-Avengers/icu-connect-backend/actions/workflows/ci_cd_pipeline.yml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=The-Agile-Avengers_icu-connect-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=The-Agile-Avengers_icu-connect-backend)
## Requirements
- [Docker](https://www.docker.com/products/docker-desktop/) 
- Java 17
- [Maven](https://www.baeldung.com/install-maven-on-windows-linux-mac)
- [Localstack](https://docs.localstack.cloud/user-guide/integrations/aws-cli/#localstack-aws-cli-awslocal) (you need the pro version) 
- [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html)

## Setup
### Docker Version
Execute the following commands:
1. `mvn clean package`
2. `docker build -t icu-connect-backend .`
3. `docker-compose up -d` 
This will start a MySQL database and the backend server as separate containers. After waiting until both containers are up and running, open a new terminal and run:
4. `docker run --rm -it -p 4566:4566 -p 4510-4559:4510-4559 -v /var/run/docker.sock:/var/run/docker.sock -e PERSISTENCE=1 -e LOCALSTACK_API_KEY=**<your_key>** localstack/localstack-pro`
5. `awslocal s3api create-bucket --bucket icufiles --create-bucket-configuration LocationConstraint=eu-west-1`

Again you need LocalStack Pro to execute command #4.
These last two commands will initialize a LocalStack container and create an [S3](https://docs.localstack.cloud/user-guide/aws/s3/) bucket that we will use for file uploading.
Congratulations your backend on **port 8080** is good to go!

These last two commands will initialize a LocalStack container and create an S3 bucket that we will use for file uploading. Congratulations your backend on port 8080 is good to go!
Youll find further information [here](https://github.com/The-Agile-Avengers/icu-connect-frontend/wiki/2.3-Installation-and-Setup)

### OpenAPI
To access the openAPI resource visit: `localhost:8080/swagger-ui/index.html`
