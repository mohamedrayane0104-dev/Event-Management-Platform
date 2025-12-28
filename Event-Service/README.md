# Event-Service

Service de gestion des événements (CRUD) avec vérification des participants/organisateurs.

## Tech / Port / DB
- Spring Boot 2.7.x
- Port : 8082
- DB : MySQL `event-db`

## Endpoints principaux (via Gateway ou direct)
- `GET  /api/events` : liste des événements
- `POST /api/events` : créer un événement
- `GET  /api/events/{id}` : détail
- `PUT  /api/events/{id}` : mise à jour
- `DELETE /api/events/{id}` : suppression
- `POST /api/events/{eventId}/register` : enregistrer un participant (tickets)

## Swagger
- http://localhost:8082/swagger-ui.html

## Démarrage local (service seul)
```bash
mvn clean package -DskipTests
docker build -t services-event-service .
docker run --rm -p 8082:8082 --name event-service services-event-service
```
(en pratique, utiliser `docker compose up -d` depuis la racine projet).
