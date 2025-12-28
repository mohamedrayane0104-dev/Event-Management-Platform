# API Gateway

Portail d’entrée des appels REST : routage, CORS, sécurité de base. Toutes les requêtes front passent par le Gateway vers les microservices.

## Tech / Port
- Spring Cloud Gateway
- Port : 8080

## Routes principales
- `/auth/**`, `/users/**` → user-service
- `/events/**` → event-service
- `/reservations/**` → reservation-service
- `/payments/**` → payment-service

## Particularités
- CORS géré ici (origines autorisées pour le front).
- Rewrite des paths vers les endpoints `/api/...` internes.

## Démarrage (seul)
```bash
mvn clean package -DskipTests
docker build -t services-api-gateway .
docker run --rm -p 8080:8080 --name api-gateway services-api-gateway
```
(en pratique, utiliser `docker compose up -d` depuis la racine projet).
