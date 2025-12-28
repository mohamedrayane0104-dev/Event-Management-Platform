#!/usr/bin/env bash

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}  User-Service Startup Script${NC}"
echo -e "${YELLOW}========================================${NC}\n"

# Check if Docker is running
echo -e "${YELLOW}[1/4]${NC} Checking Docker status..."
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}✗ Docker is not running. Please start Docker Desktop.${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker is running${NC}\n"

# Build the JAR
echo -e "${YELLOW}[2/4]${NC} Building JAR package..."
if ! mvn clean package -DskipTests > /dev/null 2>&1; then
    echo -e "${RED}✗ Maven build failed${NC}"
    exit 1
fi
echo -e "${GREEN}✓ JAR built successfully${NC}\n"

# Stop existing containers
echo -e "${YELLOW}[3/4]${NC} Stopping existing containers..."
docker-compose down -v 2>/dev/null || true
echo -e "${GREEN}✓ Old containers removed${NC}\n"

# Start services
echo -e "${YELLOW}[4/4]${NC} Starting services with Docker Compose..."
docker-compose up -d
echo -e "${GREEN}✓ Services started${NC}\n"

# Wait for services to be ready
echo -e "${YELLOW}Waiting for services to be ready...${NC}"
sleep 15

# Test health
echo -e "\n${YELLOW}Testing service health...${NC}"
HEALTH=$(curl -sS -X GET http://localhost:8081/api/users 2>/dev/null || echo "")
if [ -z "$HEALTH" ]; then
    echo -e "${YELLOW}⚠ Service not responding yet, checking logs...${NC}"
    docker-compose logs user-service --tail 50
else
    echo -e "${GREEN}✓ Service is healthy and responding${NC}"
fi

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}  User-Service is running!${NC}"
echo -e "${GREEN}========================================${NC}"
echo -e "\n${YELLOW}Service URLs:${NC}"
echo -e "  API Base:    http://localhost:8081/api"
echo -e "  Get Users:   http://localhost:8081/api/users"
echo -e "  Register:    POST http://localhost:8081/api/auth/register"
echo -e "  Login:       POST http://localhost:8081/api/auth/login"
echo -e "\n${YELLOW}Useful commands:${NC}"
echo -e "  View logs:       docker-compose logs -f user-service"
echo -e "  Stop services:   docker-compose down"
echo -e "  Test script:     bash automated_tests/test.sh"
echo -e "\n"