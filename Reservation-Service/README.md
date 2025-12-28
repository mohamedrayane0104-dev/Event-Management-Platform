# Reservation-Service

Service de gestion des réservations : limite 4 tickets, vérifie la disponibilité auprès d’Event-Service. Résilience via Resilience4j.

## Tech / Port / DB
- Spring Boot 2.7.x, Resilience4j
- Port : 8083
- DB : MySQL `reservation-db`

## Endpoints principaux (via Gateway ou direct)
- `POST /api/reservations` : créer une réservation (eventId, userId, quantity ≤ 4)
- `GET  /api/reservations/{id}` : détail
- `GET  /api/reservations` : liste

## Swagger
- http://localhost:8083/swagger-ui.html

## Démarrage local (service seul)
```bash
mvn clean package -DskipTests
docker build -t services-reservation-service .
docker run --rm -p 8083:8083 --name reservation-service services-reservation-service
```
(en pratique, utiliser `docker compose up -d` depuis la racine projet).
