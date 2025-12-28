#!/usr/bin/env bash
# Minimal test script (Git Bash). If you use PowerShell, use curl.exe and escape JSON differently.

set -e

echo '============ Test health ====='
curl -sS -X GET http://localhost:8081/api/users || true
echo -e "\n"

echo '=========== Register users ============'
curl -sS -X POST http://localhost:8081/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"bob","email":"bob@example.com","password":"pass123","roles":["USER"]}' || true
echo -e "\n"

curl -sS -X POST http://localhost:8081/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"carol","email":"carol@example.com","password":"pass123","roles":["USER","ORGANIZER"]}' || true
echo -e "\n"

echo '========== Login (bob) =========='
curl -sS -X POST http://localhost:8081/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"bob","password":"pass123"}' || true
echo -e "\n"

echo '========== Get all users ========='
curl -sS -X GET http://localhost:8081/api/users || true
echo -e "\n"

echo '========== Done =========='