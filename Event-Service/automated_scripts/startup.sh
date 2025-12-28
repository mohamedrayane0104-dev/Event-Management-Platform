#!/usr/bin/env bash

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}  Event-Service Startup Script${NC}"
echo -e "${YELLOW}========================================${NC}\n"

echo -e "${YELLOW}[1/4]${NC} Checking Docker status..."
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}✗ Docker is not running${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker is running${NC}\n"

echo -e "${YELLOW}[2/4]${NC} Building JAR package..."
# Navigate to Event-Service root (parent of automated_tests)
cd "$(dirname "$0")/.."
if ! mvn clean package -DskipTests > /dev/null 2>&1; then
    echo -e "${RED}✗ Maven build failed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ JAR built successfully${NC}\n"

echo -e "${YELLOW}[3/4]${NC} Stopping existing containers..."
docker-compose down -v 2>/dev/null || true
echo -e "${GREEN}✓ Old containers removed${NC}\n"

echo -e "${YELLOW}[4/4]${NC} Starting services..."
docker-compose up -d
echo -e "${GREEN}✓ Services started${NC}\n"

echo -e "${YELLOW}Waiting for services (15s)...${NC}"
sleep 15

echo -e "\n${YELLOW}Testing service health...${NC}"
HEALTH=$(curl -sS -X GET http://localhost:8082/api/events 2>/dev/null || echo "")
if [ -z "$HEALTH" ]; then
    echo -e "${YELLOW}⚠ Service not responding yet${NC}"
    docker-compose logs event-service --tail 50
else
    echo -e "${GREEN}✓ Service is healthy${NC}"
fi

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}  Event-Service is running!${NC}"
echo -e "${GREEN}========================================${NC}\n"