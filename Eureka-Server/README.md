# Eureka-Server

Serveur de découverte pour les microservices.

## Tech / Port
- Spring Cloud Netflix Eureka
- Port : 8761

## Usage
- UI : http://localhost:8761 pour voir les instances enregistrées.
- Les microservices se déclarent automatiquement (Gateway, Event, User, Reservation, Payment).

## Démarrage (seul)
```bash
mvn clean package -DskipTests
docker build -t services-eureka-server .
docker run --rm -p 8761:8761 --name eureka-server services-eureka-server
```
(en pratique, utiliser `docker compose up -d` depuis la racine projet).
