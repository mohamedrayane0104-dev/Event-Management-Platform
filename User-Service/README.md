# User-Service

Service d’authentification et de gestion des utilisateurs. Émet les JWT consommés par les autres microservices.

## Tech / Port / DB
- Spring Boot 2.7.x, Security, JWT
- Port : 8081
- DB : MySQL `user-db`

## Endpoints principaux (via Gateway ou direct)
- `POST /auth/register` : inscription
- `POST /auth/login` : login (retourne JWT)
- `GET  /users` : liste des utilisateurs

## Swagger
- http://localhost:8081/swagger-ui.html

## Démarrage local (service seul)
```bash
mvn clean package -DskipTests
docker build -t services-user-service .
docker run --rm -p 8081:8080 --name user-service services-user-service
```
(en pratique, utiliser `docker compose up -d` depuis la racine projet).

## Launch Instructions
To run the application, use the following command:
```
mvn spring-boot:run
```
Ensure that the MySQL database is running and properly configured in `application.yml`.

## Database Initialization
A SQL script is provided to initialize the User table with the necessary fields.

## Validation
Ensure all endpoints are tested and functioning correctly before proceeding to the next micro-service.



todo :
✅ Micro-service User avec registration + authentication
✅ BCrypt password hashing
✅ MySQL en Docker
✅ API REST testée
✅ Docker Compose configuré
✅ Scripts de démarrage et test
