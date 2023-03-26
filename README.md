# icu-connect-backend
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

### Run backend in an editor
6. Run `docker-compose up -d` which will start your database and your backend container.
