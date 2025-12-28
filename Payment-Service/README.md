# Payment-Service

Service de simulation de paiement et mise à jour des réservations. Résilience via Resilience4j.

## Tech / Port / DB
- Spring Boot 2.7.x, Resilience4j
- Port : 8084
- DB : MySQL `payment-db`

## Endpoints principaux (via Gateway ou direct)
- `POST /api/payments` : effectuer un paiement (reservationId, paymentMethod)
- `GET  /api/payments/{id}` : détail d’un paiement
- `GET  /api/payments` : liste des paiements

## Swagger
- http://localhost:8084/swagger-ui.html

## Démarrage local (service seul)
```bash
mvn clean package -DskipTests
docker build -t services-payment-service .
docker run --rm -p 8084:8084 --name payment-service services-payment-service
```
(en pratique, utiliser `docker compose up -d` depuis la racine projet).
