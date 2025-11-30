-- Seed data for Papers Please System (Production version)
-- Simplified version without sequences and replication role

-- Clear existing data (optional - uncomment if needed)
-- TRUNCATE TABLE notifications, events, ticket_documents, documents, ticket_relations, tickets, participations, shifts, users, upks CASCADE;

-- Data for table: upks
INSERT INTO upks (id, name, region) VALUES ('ba31cff1-55db-434c-9902-0c7a8c5c05d5', 'max''s upk', 'ORVECH_VONOR') ON CONFLICT (id) DO NOTHING;
INSERT INTO upks (id, name, region) VALUES ('45575ca3-580e-4818-8556-03d2a7c2160d', 'kate''s upk', 'EAST_GRESTIN') ON CONFLICT (id) DO NOTHING;

-- Data for table: users
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('f9c1f65c-f98a-40a2-b15a-09e2d98f0a67', 'arekalov@ya.ru', 'arekalov', '$2a$10$sZoHX20eoXUOrNLocTBiP.fTUIzAcnU7u2Kk0ab8G3Z.XhRReO792', 'GOD', NULL) ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'max@ya.ru', 'max', '$2a$10$iDmcp.mSphs0C6faRTdjkOZY4tPMi3ZvSjWDkA.lBvVoEsOtdmYG.', 'BOSS', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5') ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('ae3df3bc-77fe-436d-a2fb-e35426621451', 'tim@ya.ru', 'tim', '$2a$10$rA/69syrYH..G3uD5Hsk/.3IyRlo.KkDGSqhK6ifcR5O.JTowmhmW', 'INSPECTOR', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5') ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('f8c7eed2-5a92-4d6e-874b-8a9bf2e42a75', 'stephan@ya.ru', 'stephan', '$2a$10$VmM5.ULxGrFqsRAmDoLgnuHpRrCCvV6Z8zmoH9fTxTD3/SnUNXN0u', 'INSPECTOR', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5') ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('49f05c98-2d2d-44fb-89c6-75c254e7c20d', 'george@ya.ru', 'george', '$2a$10$KjkpImJ715gf0cBFG1QjDOMuXk/ozUmoLjnVa2.O9sZiJFdcwCIV.', 'SECURITY', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5') ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('d2490bdf-5fae-43b5-b689-10ab9f36965e', 'kate@ya.ru', 'kate', '$2a$10$XWVLFnV8GUOSDy3pkbXeme/J7MYaxHSsdjj4y5aueLmQS8c6W5HQa', 'BOSS', '45575ca3-580e-4818-8556-03d2a7c2160d') ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('e960e526-d486-46a2-989f-af6ba4c124dc', 'sonya@ya.ru', 'sonya', '$2a$10$Yj3HcmdDnWW5soTw12bkH.PZ2DIHt9vDCLCvRkQ1IpeqfrC9D0P9.', 'INSPECTOR', '45575ca3-580e-4818-8556-03d2a7c2160d') ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('98401918-310b-4006-bad8-a5cff38582e9', 'alice@ya.ru', 'alice', '$2a$10$URTrD6uJDRQafP8BWLZGbOB73rvVClu7OlzQDDD4MOKQLwVSkD0kG', 'SECURITY', '45575ca3-580e-4818-8556-03d2a7c2160d') ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('b5ba906e-f174-4f9d-a1b3-883aa2a28fd4', 'dana@ya.ru', 'dana', '$2a$10$i/OCMg66FnvD5jfEg5OC2umjZ2/csRvxqBZZ2JjbaI/nhTkN7wi7C', 'SECURITY', '45575ca3-580e-4818-8556-03d2a7c2160d') ON CONFLICT (id) DO NOTHING;
INSERT INTO users (id, email, name, password_hash, role, upk_id) VALUES ('091b55a5-f94b-429d-8569-9dbfd050ae3c', 'leila@ya.ru', 'leila', '$2a$10$SUAw7IiyvSXhd0th8tUgl.U0ES7wAVI7NSyF5K4QGb7CgKaXW80CK', 'MIGRANT', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5') ON CONFLICT (id) DO NOTHING;

-- Data for table: shifts
INSERT INTO shifts (id, end_time, start_time, created_by, upk_id) VALUES ('feefbbd1-f842-4c33-88c9-cf131aef0c78', NULL, '2025-11-23 16:04:10.898247+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5') ON CONFLICT (id) DO NOTHING;
INSERT INTO shifts (id, end_time, start_time, created_by, upk_id) VALUES ('681b3a76-3860-4066-999f-f6b9f86b4468', NULL, '2025-11-24 16:04:10.898247+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5') ON CONFLICT (id) DO NOTHING;
INSERT INTO shifts (id, end_time, start_time, created_by, upk_id) VALUES ('9754d5d7-08d7-430c-a4c8-ece8ca62b53d', NULL, '2025-11-25 16:04:10.898247+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ba31cff1-55db-434c-9902-0c7a8c5c05d5') ON CONFLICT (id) DO NOTHING;

-- Data for table: participations
INSERT INTO participations (id, accepted, wage, penalty, specialization, shift_id, user_id) VALUES ('662d84c1-0264-4a1f-a421-7a9c723846eb', false, 1.0, 0.0, 'PASSPORT', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', 'ae3df3bc-77fe-436d-a2fb-e35426621451') ON CONFLICT DO NOTHING;
INSERT INTO participations (id, accepted, wage, penalty, specialization, shift_id, user_id) VALUES ('1949de4d-2e9d-42c4-a181-edf02b08ce50', false, 1.0, 0.0, 'LOCALS', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', 'ae3df3bc-77fe-436d-a2fb-e35426621451') ON CONFLICT DO NOTHING;
INSERT INTO participations (id, accepted, wage, penalty, specialization, shift_id, user_id) VALUES ('534dd2e2-00ce-485e-87fa-3b5f06d89587', false, 1.0, 0.0, 'PASSPORT', '681b3a76-3860-4066-999f-f6b9f86b4468', 'ae3df3bc-77fe-436d-a2fb-e35426621451') ON CONFLICT DO NOTHING;
INSERT INTO participations (id, accepted, wage, penalty, specialization, shift_id, user_id) VALUES ('a3267a39-be05-4796-aa2d-e3f929aca98a', false, 1.0, 0.0, 'PASSPORT', '9754d5d7-08d7-430c-a4c8-ece8ca62b53d', 'ae3df3bc-77fe-436d-a2fb-e35426621451') ON CONFLICT DO NOTHING;
INSERT INTO participations (id, accepted, wage, penalty, specialization, shift_id, user_id) VALUES ('65d3ac38-2969-4b36-b4ed-9384b3cb0518', false, 1.0, 0.0, 'PASSPORT', '9754d5d7-08d7-430c-a4c8-ece8ca62b53d', 'f8c7eed2-5a92-4d6e-874b-8a9bf2e42a75') ON CONFLICT DO NOTHING;

-- Data for table: tickets
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('5ed93a7e-cb4c-40c3-b4f3-be4ad5e2588b', '2025-11-23 19:00:56.476695+00:00', NULL, 'Заявка мигранта 1', 'LOW', NULL, 'OPEN', 'EXTERNAL', '2025-11-23 19:00:56.476699+00:00', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL, NULL, '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('a4f2b630-3067-4279-8f0e-24f5664e0602', '2025-11-23 18:56:42.018205+00:00', NULL, 'Заявка мигранта 2', 'LOW', NULL, 'OPEN', 'EXTERNAL', '2025-11-23 19:12:58.108161+00:00', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL, NULL, '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('0dcc6758-1989-49a7-8ff0-5bc1d9e1f61e', '2025-11-23 19:02:56.652086+00:00', NULL, 'Технический тикет', 'NORMAL', NULL, 'IN_PROGRESS', 'INTERNAL', '2025-11-23 19:02:56.652092+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ae3df3bc-77fe-436d-a2fb-e35426621451', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', NULL) ON CONFLICT (id) DO NOTHING;

-- Data for table: documents
INSERT INTO documents (id, uploaded_at, document_type, body, issued_at, expires_at, owner_id) 
VALUES ('a05e96e7-0deb-4147-a1be-2a06f07e5cfb', '2025-11-23 19:10:33.979387+00:00', 'PASSPORT', 'Паспорт гражданина', '2020-01-01 00:00:00+00:00', '2030-01-01 00:00:00+00:00', '091b55a5-f94b-429d-8569-9dbfd050ae3c') ON CONFLICT (id) DO NOTHING;

-- Data for table: events  
INSERT INTO events (id, time, description, specialization, shift_id) 
VALUES ('a22f2e91-f35c-4e3b-aaea-8b8f6b6b2df8', '2025-11-23 19:21:36.193314+00:00', 'Проход через УПК', 'PASSPORT', 'feefbbd1-f842-4c33-88c9-cf131aef0c78') ON CONFLICT (id) DO NOTHING;

INSERT INTO events (id, time, description, specialization, shift_id) 
VALUES ('0db6d43d-0cd7-40e3-9ce1-a9ba074a1e98', '2025-11-23 19:21:42.301697+00:00', 'Выход с УПК', 'PASSPORT', 'feefbbd1-f842-4c33-88c9-cf131aef0c78') ON CONFLICT (id) DO NOTHING;

-- Data for table: notifications
INSERT INTO notifications (id, created_at, is_read, message, notification_type, user_id, shift_id) 
VALUES ('1ed5ebb5-7b94-4ad1-bf49-a54074c38cd1', '2025-11-23 19:12:58.108188+00:00', false, 'Ваша заявка обновлена', 'TICKET_UPDATED', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- Summary
SELECT 'Seed data loaded successfully!' AS status;
SELECT 'Users: ' || COUNT(*) FROM users;
SELECT 'UPKs: ' || COUNT(*) FROM upks;
SELECT 'Shifts: ' || COUNT(*) FROM shifts;
SELECT 'Participations: ' || COUNT(*) FROM participations;
SELECT 'Tickets: ' || COUNT(*) FROM tickets;
SELECT 'Documents: ' || COUNT(*) FROM documents;
SELECT 'Events: ' || COUNT(*) FROM events;
SELECT 'Notifications: ' || COUNT(*) FROM notifications;

