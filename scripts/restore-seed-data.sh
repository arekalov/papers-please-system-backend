#!/bin/bash

# Script to restore seed data to PostgreSQL database

# Database connection parameters
DB_URL="${DB_URL:-jdbc:postgresql://ep-fancy-mud-a4icq7ux-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require}"
DB_USERNAME="${DB_USERNAME:-neondb_owner}"
DB_PASSWORD="${DB_PASSWORD:-npg_AQuTw7XShcL6}"

# Parse connection string to extract host and database
HOST=$(echo "$DB_URL" | sed -n 's/.*:\/\/\([^:\/]*\).*/\1/p')
DATABASE=$(echo "$DB_URL" | sed -n 's/.*\/\([^?]*\).*/\1/p')

echo "=== Restoring seed data to PostgreSQL database ==="
echo "Host: $HOST"
echo "Database: $DATABASE"
echo "Username: $DB_USERNAME"
echo ""

# Ask for confirmation
read -p "This will CLEAR all existing data and restore from seed-data.sql. Continue? (yes/no): " -r
if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
    echo "Aborted."
    exit 1
fi

echo ""
echo "Step 1/2: Clearing existing data..."
PGPASSWORD="$DB_PASSWORD" psql \
  -h "$HOST" \
  -U "$DB_USERNAME" \
  -d "$DATABASE" \
  -f scripts/clear-data.sql

if [ $? -ne 0 ]; then
    echo "❌ Error clearing data"
    exit 1
fi

echo "✅ Data cleared"
echo ""
echo "Step 2/2: Restoring seed data..."
PGPASSWORD="$DB_PASSWORD" psql \
  -h "$HOST" \
  -U "$DB_USERNAME" \
  -d "$DATABASE" \
  -f scripts/seed-data.sql

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Seed data successfully restored!"
    echo ""
    echo "Restored:"
    echo "  - 2 UPKs"
    echo "  - 10 Users"
    echo "  - 3 Shifts"
    echo "  - 5 Participations"
    echo "  - 3 Tickets"
    echo "  - 1 Document"
    echo "  - 2 Events"
    echo "  - 1 Notification"
else
    echo ""
    echo "❌ Error restoring seed data"
    exit 1
fi

