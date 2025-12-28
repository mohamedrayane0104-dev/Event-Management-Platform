# Navigate to User-Service directory
cd c:\Users\Vec\Desktop\school\tp\java\projet\attempt2\User-Service

# Start services
docker-compose up -d

# Check logs
docker-compose logs -f user-service

# Check MySQL
docker-compose logs mysql-user-db