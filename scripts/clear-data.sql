-- Script to clean all tables before seeding
-- Run this before seed-data.sql to avoid conflicts

-- Disable foreign key checks temporarily
SET session_replication_role = replica;

-- Clear all tables in reverse order (to respect foreign keys)
TRUNCATE TABLE notifications CASCADE;
TRUNCATE TABLE events CASCADE;
TRUNCATE TABLE ticket_documents CASCADE;
TRUNCATE TABLE ticket_relations CASCADE;
TRUNCATE TABLE documents CASCADE;
TRUNCATE TABLE tickets CASCADE;
TRUNCATE TABLE participations CASCADE;
TRUNCATE TABLE shifts CASCADE;
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE upks CASCADE;

-- Re-enable foreign key checks
SET session_replication_role = DEFAULT;

-- Success message
SELECT 'All tables cleared successfully' AS status;

