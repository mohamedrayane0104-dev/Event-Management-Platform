# Config-Server

Serveur de configuration centralisée (profil `native`) pour les microservices.

## Tech / Port
- Spring Cloud Config Server
- Port : 8888

## Sources de config
- Mode `native` : lit les fichiers dans `config/` (ex. `config/application.yml`, `config/event-service.yml`, etc.).

## Démarrage (seul)
```bash
mvn clean package -DskipTests
docker build -t services-config-server .
docker run --rm -p 8888:8888 --name config-server services-config-server
```
(en pratique, utiliser `docker compose up -d` depuis la racine projet).
