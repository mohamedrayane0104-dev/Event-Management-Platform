#!/usr/bin/env bash

echo '============ Test health ====='
curl -sS -X GET http://localhost:8082/api/events
echo -e "\n\n"

echo '=========== Create Event ============'
curl -sS -X POST http://localhost:8082/api/events \
  -H 'Content-Type: application/json' \
  -d '{"title":"Spring Conference 2025","description":"Annual Spring Boot conference","eventDate":"2025-06-15T09:00:00","location":"Paris","organizerId":1,"totalTickets":100,"ticketPrice":50.0,"active":true}'
echo -e "\n\n"

echo '========== Get All Events =========='
curl -sS -X GET http://localhost:8082/api/events
echo -e "\n\n"

echo '========== Get Event by ID ========='
curl -sS -X GET http://localhost:8082/api/events/1
echo -e "\n\n"

echo '========== Register Participant ========='
curl -sS -X POST 'http://localhost:8082/api/events/1/register?userId=1&ticketsRequested=2'
echo -e "\n\n"

echo '========== Get Event Participants ========='
curl -sS -X GET http://localhost:8082/api/events/1/participants
echo -e "\n\n"

echo '========== Done =========='