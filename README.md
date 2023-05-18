# icu-connect-backend
[![Stable Build](https://github.com/The-Agile-Avengers/icu-connect-backend/actions/workflows/maven.yml/badge.svg)](https://github.com/The-Agile-Avengers/icu-connect-backend/actions/workflows/ci_cd_pipeline.yml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=The-Agile-Avengers_icu-connect-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=The-Agile-Avengers_icu-connect-backend)
## Requirements
- [Docker](https://www.docker.com/products/docker-desktop/) 
- Java 17
- [Maven](https://www.baeldung.com/install-maven-on-windows-linux-mac)
## Get Started
1. Clone the repository
2. Edit env.properties file with your credentials
3. Edit docker-compose file with your credentials

### Run backend in an editor
4. Install the maven dependencies with `mvn dependencies:resolve`
5. Comment out the configuration for the backend in the docker-compose file, so that only the database and volume config is left.
6. Run `docker-compose up -d` which will start your database container.
7. Use your editor to start the backend. This should set up the tables in the database and then it is ready to be used.

### Run backend in a docker container
6. Run `docker-compose up -d` which will start your database and your backend container.

### OpenAPI
To access the openAPI resource visit: `localhost:8080/swagger-ui/index.html`

### Localstack
#### S3 File System
1. Set your `LOCALSTACK_API_KEY` in `docker-compose-localstack-pro`
1. Execute `docker-compose -f docker-compose-localstack-pro up`
2. SSH into container or open Docker Desktop and open the terminal of the container
3. In the container terminal, execute  `aws configure`
4. Set `AWS Access Key ID` to whatever you like (and remember the value)
5. Set `AWS Secret Access Key` to whatever you like (and remember the value)
6. Set `Default region name` to `eu-west-1`
7. Set `Default output format` to `json`
8. Execute `aws --endpoint-url=http://localhost:4566 s3 mb s3://icu.connect`
9. In the application.properties file of the project set `config.aws.s3.access-key`
`config.aws.s3.secret-key` and `config.aws.region` to the values you chose before
10. Run the Application
