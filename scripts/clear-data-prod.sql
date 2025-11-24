-- Clear all data from Papers Please database
-- Removes all data while keeping table structure

-- Start transaction
BEGIN;

-- Disable foreign key checks temporarily
SET CONSTRAINTS ALL DEFERRED;

-- Delete data in correct order (child tables first, then parent tables)

-- Clear notifications (no foreign key dependencies)
DELETE FROM notifications;

-- Clear events (no foreign key dependencies from other tables)
DELETE FROM events;

-- Clear ticket_documents (junction table)
DELETE FROM ticket_documents;

-- Clear documents
DELETE FROM documents;

-- Clear ticket_relations (junction table)
DELETE FROM ticket_relations;

-- Clear tickets
DELETE FROM tickets;

-- Clear participations
DELETE FROM participations;

-- Clear shifts
DELETE FROM shifts;

-- Clear users
DELETE FROM users;

-- Clear upks (last, as users reference it)
DELETE FROM upks;

-- Commit transaction
COMMIT;

-- Summary
SELECT 'All data cleared successfully!' AS status;

-- Show remaining counts (should all be 0)
SELECT 'Remaining users: ' || COUNT(*) FROM users;
SELECT 'Remaining upks: ' || COUNT(*) FROM upks;
SELECT 'Remaining shifts: ' || COUNT(*) FROM shifts;
SELECT 'Remaining participations: ' || COUNT(*) FROM participations;
SELECT 'Remaining tickets: ' || COUNT(*) FROM tickets;
SELECT 'Remaining documents: ' || COUNT(*) FROM documents;
SELECT 'Remaining events: ' || COUNT(*) FROM events;
SELECT 'Remaining notifications: ' || COUNT(*) FROM notifications;

