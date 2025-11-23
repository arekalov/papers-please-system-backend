-- Seed data for Papers Please System
-- Generated automatically
-- Database: neondb
-- Date: '2025-11-23T23:53:29.550711+03:00'::timestamptz

-- Disable triggers and constraints for faster insertion
SET session_replication_role = replica;


-- Data for table: upks
-- Rows: 2

INSERT INTO upks (id, name, region) VALUES ('ba31cff1-55db-434c-9902-0c7a8c5c05d5', 'max''s upk', 'ORVECH_VONOR');
INSERT INTO upks (id, name, region) VALUES ('45575ca3-580e-4818-8556-03d2a7c2160d', 'kate''s upk', 'EAST_GRESTIN');

-- Data for table: users
-- Rows: 10

INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('f9c1f65c-f98a-40a2-b15a-09e2d98f0a67', 'arekalov@ya.ru', 'arekalov', '$2a$10$sZoHX20eoXUOrNLocTBiP.fTUIzAcnU7u2Kk0ab8G3Z.XhRReO792', 'GOD', NULL);
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'max@ya.ru', 'max', '$2a$10$iDmcp.mSphs0C6faRTdjkOZY4tPMi3ZvSjWDkA.lBvVoEsOtdmYG.', 'BOSS', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5');
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('ae3df3bc-77fe-436d-a2fb-e35426621451', 'tim@ya.ru', 'tim', '$2a$10$rA/69syrYH..G3uD5Hsk/.3IyRlo.KkDGSqhK6ifcR5O.JTowmhmW', 'INSPECTOR', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5');
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('f8c7eed2-5a92-4d6e-874b-8a9bf2e42a75', 'stephan@ya.ru', 'stephan', '$2a$10$VmM5.ULxGrFqsRAmDoLgnuHpRrCCvV6Z8zmoH9fTxTD3/SnUNXN0u', 'INSPECTOR', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5');
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('49f05c98-2d2d-44fb-89c6-75c254e7c20d', 'george@ya.ru', 'george', '$2a$10$KjkpImJ715gf0cBFG1QjDOMuXk/ozUmoLjnVa2.O9sZiJFdcwCIV.', 'SECURITY', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5');
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('d2490bdf-5fae-43b5-b689-10ab9f36965e', 'kate@ya.ru', 'kate', '$2a$10$XWVLFnV8GUOSDy3pkbXeme/J7MYaxHSsdjj4y5aueLmQS8c6W5HQa', 'BOSS', '45575ca3-580e-4818-8556-03d2a7c2160d');
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('e960e526-d486-46a2-989f-af6ba4c124dc', 'sonya@ya.ru', 'sonya', '$2a$10$Yj3HcmdDnWW5soTw12bkH.PZ2DIHt9vDCLCvRkQ1IpeqfrC9D0P9.', 'INSPECTOR', '45575ca3-580e-4818-8556-03d2a7c2160d');
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('98401918-310b-4006-bad8-a5cff38582e9', 'alice@ya.ru', 'alice', '$2a$10$URTrD6uJDRQafP8BWLZGbOB73rvVClu7OlzQDDD4MOKQLwVSkD0kG', 'SECURITY', '45575ca3-580e-4818-8556-03d2a7c2160d');
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('b5ba906e-f174-4f9d-a1b3-883aa2a28fd4', 'dana@ya.ru', 'dana', '$2a$10$i/OCMg66FnvD5jfEg5OC2umjZ2/csRvxqBZZ2JjbaI/nhTkN7wi7C', 'SECURITY', '45575ca3-580e-4818-8556-03d2a7c2160d');
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('091b55a5-f94b-429d-8569-9dbfd050ae3c', 'leila@ya.ru', 'leila', '$2a$10$SUAw7IiyvSXhd0th8tUgl.U0ES7wAVI7NSyF5K4QGb7CgKaXW80CK', 'MIGRANT', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5');

-- Data for table: shifts
-- Rows: 3

INSERT INTO shifts (id, end_time, start_time, created_by, upk_id) VALUES ('feefbbd1-f842-4c33-88c9-cf131aef0c78', NULL, '2025-11-23 16:04:10.898247+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5');
INSERT INTO shifts (id, end_time, start_time, created_by, upk_id) VALUES ('681b3a76-3860-4066-999f-f6b9f86b4468', NULL, '2025-11-24 16:04:10.898247+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5');
INSERT INTO shifts (id, end_time, start_time, created_by, upk_id) VALUES ('9754d5d7-08d7-430c-a4c8-ece8ca62b53d', NULL, '2025-11-25 16:04:10.898247+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5');

-- Data for table: participations
-- Rows: 5

INSERT INTO participations (id, accepted, bonus_coefficient, penalty_coefficient, specialization, shift_id, user_id) VALUES ('662d84c1-0264-4a1f-a421-7a9c723846eb', False, 1.0, 0.0, 'PASSPORT', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', 'ae3df3bc-77fe-436d-a2fb-e35426621451');
INSERT INTO participations (id, accepted, bonus_coefficient, penalty_coefficient, specialization, shift_id, user_id) VALUES ('1949de4d-2e9d-42c4-a181-edf02b08ce50', False, 1.0, 0.0, 'LOCALS', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', 'ae3df3bc-77fe-436d-a2fb-e35426621451');
INSERT INTO participations (id, accepted, bonus_coefficient, penalty_coefficient, specialization, shift_id, user_id) VALUES ('534dd2e2-00ce-485e-87fa-3b5f06d89587', False, 1.0, 0.0, 'PASSPORT', '681b3a76-3860-4066-999f-f6b9f86b4468', 'ae3df3bc-77fe-436d-a2fb-e35426621451');
INSERT INTO participations (id, accepted, bonus_coefficient, penalty_coefficient, specialization, shift_id, user_id) VALUES ('a3267a39-be05-4796-aa2d-e3f929aca98a', False, 1.0, 0.0, 'PASSPORT', '9754d5d7-08d7-430c-a4c8-ece8ca62b53d', 'ae3df3bc-77fe-436d-a2fb-e35426621451');
INSERT INTO participations (id, accepted, bonus_coefficient, penalty_coefficient, specialization, shift_id, user_id) VALUES ('65d3ac38-2969-4b36-b4ed-9384b3cb0518', False, 1.0, 0.0, 'PASSPORT', '9754d5d7-08d7-430c-a4c8-ece8ca62b53d', 'f8c7eed2-5a92-4d6e-874b-8a9bf2e42a75');

-- Data for table: tickets
-- Rows: 3

INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) VALUES ('5ed93a7e-cb4c-40c3-b4f3-be4ad5e2588b', '2025-11-23 19:00:56.476695+00:00', NULL, 'Заявка мигранта', 'LOW', NULL, 'OPEN', 'EXTERNAL', '2025-11-23 19:00:56.476699+00:00', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL, NULL, '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL);
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) VALUES ('a4f2b630-3067-4279-8f0e-24f5664e0602', '2025-11-23 18:56:42.018205+00:00', NULL, 'Заявка мигранта', 'LOW', NULL, 'OPEN', 'EXTERNAL', '2025-11-23 19:12:58.108161+00:00', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL, NULL, '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL);
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) VALUES ('a3d35878-8c58-404a-8d52-1774c94031d4', '2025-11-23 20:46:08.469455+00:00', NULL, 'Заявка мигранта', 'LOW', NULL, 'OPEN', 'EXTERNAL', '2025-11-23 20:46:08.469457+00:00', '091b55a5-f94b-429d-8569-9dbfd050ae3c', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', NULL, '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL);

-- Data for table: documents
-- Rows: 1

INSERT INTO documents (id, body, document_type, expires_at, issued_at, uploaded_at, owner_id) VALUES ('9fa0d8f6-4571-4fbd-81fe-348aa3531473', '{"additionalProp1":{},"additionalProp2":{},"additionalProp3":{}}', 'PASSPORT', '2027-11-23 17:45:06.064000+00:00', '2025-11-22 17:45:06.064000+00:00', '2025-11-23 18:07:24.367204+00:00', '091b55a5-f94b-429d-8569-9dbfd050ae3c');

-- Data for table: events
-- Rows: 2

INSERT INTO events (id, description, time, shift_id, specialization) VALUES ('5f134040-b69c-47b1-98fb-402930baa4f0', 'Новое не очень важное событие', '2025-11-23 20:22:41.324073+00:00', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', NULL);
INSERT INTO events (id, description, time, shift_id, specialization) VALUES ('c99da24e-3aff-47b9-aa45-7a9dd0444bf7', 'Новое очень важное событие', '2025-11-23 20:22:49.462306+00:00', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', NULL);

-- Data for table: notifications
-- Rows: 1

INSERT INTO notifications (id, created_at, is_read, message, notification_type, user_id, shift_id) VALUES ('341f03a5-e69c-452c-82b4-0add87a0119c', '2025-11-23 20:46:08.485498+00:00', False, 'You have been assigned to ticket #a3d35878-8c58-404a-8d52-1774c94031d4: Заявка мигранта', 'TICKET_ASSIGNED', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', NULL);

-- Re-enable triggers and constraints
SET session_replication_role = DEFAULT;

-- Update sequences
SELECT setval(pg_get_serial_sequence('upks', 'id'), COALESCE((SELECT MAX(id) FROM upks), 1), false);
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE((SELECT MAX(id) FROM users), 1), false);
SELECT setval(pg_get_serial_sequence('shifts', 'id'), COALESCE((SELECT MAX(id) FROM shifts), 1), false);
SELECT setval(pg_get_serial_sequence('participations', 'id'), COALESCE((SELECT MAX(id) FROM participations), 1), false);
SELECT setval(pg_get_serial_sequence('tickets', 'id'), COALESCE((SELECT MAX(id) FROM tickets), 1), false);
SELECT setval(pg_get_serial_sequence('ticket_relations', 'id'), COALESCE((SELECT MAX(id) FROM ticket_relations), 1), false);
SELECT setval(pg_get_serial_sequence('documents', 'id'), COALESCE((SELECT MAX(id) FROM documents), 1), false);
SELECT setval(pg_get_serial_sequence('ticket_documents', 'id'), COALESCE((SELECT MAX(id) FROM ticket_documents), 1), false);
SELECT setval(pg_get_serial_sequence('events', 'id'), COALESCE((SELECT MAX(id) FROM events), 1), false);
SELECT setval(pg_get_serial_sequence('notifications', 'id'), COALESCE((SELECT MAX(id) FROM notifications), 1), false);
