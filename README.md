## SETTING UP AND RUNNING THE APPLICATION

### USING DOCKER COMPOSE
#### Prerequisites
- Docker installed
- Git installed on your system

#### Set Up
1. Clone the repository `git clone https://github.com/dushimsam/account-management-system.git`
2. Navigate to the project directory `cd account-management-system`
3. Run `docker-compose up -d` to start the application


#### The client application will be accessible at `http://localhost:3000` and the backend application will be accessible at `http://localhost:4600`.

#### NOTE

The environment variables for the mail configuration are used, Please ensure that you specify the values for `spring.mail.username` and `spring.mail.password` in the application.properties file.


### MANUAL SETUP
#### Prerequisites
- Java 17 installed
- Maven installed
- Git installed on your system

#### Set Up
1. Clone the repository `git clone https://github.com/dushimsam/account-management-system.git`
2. Navigate to the project directory `cd account-management-system`
3. Install the dependencies for the client application by running npm install in the client folder. `cd client && npm install`
4. Start the client application by running npm run dev in the client folder. `npm run dev`
5. Navigate to the backend folder from the root directory `cd backend`
6. Start the backend application by running mvn spring-boot:run in the root folder. `mvn spring-boot:run`
7. The client application will be accessible at `http://localhost:3000` and the backend application will be accessible at `http://localhost:4600`.

#### NOTE

Open the application.properties file located in the `backend/src/main/resources` directory.  Make sure that the variables for the database connection, such as `spring.datasource.username`, `spring.datasource.url` and `spring.datasource.password`, are set correctly in this file.
Please ensure that you specify the values for `spring.mail.username` and `spring.mail.password` in this file.