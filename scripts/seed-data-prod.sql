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
-- Tim участвует во всех трех сменах с разными специализациями
INSERT INTO participations (id, accepted, wage, penalty, specialization, shift_id, user_id) 
VALUES ('279632b7-7da6-4493-91da-845b69ac28ae', true, 100.0, 0.0, 'PASSPORT', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', 'ae3df3bc-77fe-436d-a2fb-e35426621451') ON CONFLICT DO NOTHING;

INSERT INTO participations (id, accepted, wage, penalty, specialization, shift_id, user_id) 
VALUES ('4dbd89c8-6123-41cf-8c89-39dae72bf456', true, 100.0, 0.0, 'PASSPORT', '681b3a76-3860-4066-999f-f6b9f86b4468', 'ae3df3bc-77fe-436d-a2fb-e35426621451') ON CONFLICT DO NOTHING;

INSERT INTO participations (id, accepted, wage, penalty, specialization, shift_id, user_id) 
VALUES ('1de49f13-d108-4950-a179-d5fa0420cf8c', true, 100.0, 0.0, 'PASSPORT', '9754d5d7-08d7-430c-a4c8-ece8ca62b53d', 'ae3df3bc-77fe-436d-a2fb-e35426621451') ON CONFLICT DO NOTHING;

-- Stephan участвует в третьей смене
INSERT INTO participations (id, accepted, wage, penalty, specialization, shift_id, user_id) 
VALUES ('65d3ac38-2969-4b36-b4ed-9384b3cb0518', true, 80.0, 0.1, 'LOCALS', '9754d5d7-08d7-430c-a4c8-ece8ca62b53d', 'f8c7eed2-5a92-4d6e-874b-8a9bf2e42a75') ON CONFLICT DO NOTHING;

-- George участвует во второй смене
INSERT INTO participations (id, accepted, wage, penalty, specialization, shift_id, user_id) 
VALUES ('88a4f2c9-1a3b-4d5e-9f8c-7e6d5c4b3a21', true, 90.0, 0.0, 'WORK', '681b3a76-3860-4066-999f-f6b9f86b4468', '49f05c98-2d2d-44fb-89c6-75c254e7c20d') ON CONFLICT DO NOTHING;

-- Data for table: tickets

-- Заявка мигранта 1 (не назначена)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('5ed93a7e-cb4c-40c3-b4f3-be4ad5e2588b', '2025-11-23 19:00:56.476695+00:00', NULL, 'Заявка мигранта 1', 'LOW', NULL, 'OPEN', 'EXTERNAL', '2025-11-23 19:00:56.476699+00:00', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL, NULL, '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- Заявка мигранта 2 (не назначена)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('a4f2b630-3067-4279-8f0e-24f5664e0602', '2025-11-23 18:56:42.018205+00:00', NULL, 'Заявка мигранта 2', 'LOW', NULL, 'OPEN', 'EXTERNAL', '2025-11-23 19:12:58.108161+00:00', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL, NULL, '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- === ПЕРВАЯ СМЕНА (feefbbd1-f842-4c33-88c9-cf131aef0c78) - Tim executor ===
-- Тикет 1: CLOSED (завершен Tim)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('11111111-1111-1111-1111-111111111111', '2025-11-23 10:00:00+00:00', NULL, 'Проверка паспорта мигранта Иванова', 'HIGH', 'Документы в порядке, проверка пройдена', 'CLOSED', 'EXTERNAL', '2025-11-23 12:30:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ae3df3bc-77fe-436d-a2fb-e35426621451', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- Тикет 2: CLOSED (завершен Tim)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('22222222-2222-2222-2222-222222222222', '2025-11-23 11:00:00+00:00', NULL, 'Регистрация въезда туриста', 'NORMAL', 'Регистрация оформлена успешно', 'CLOSED', 'EXTERNAL', '2025-11-23 13:00:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ae3df3bc-77fe-436d-a2fb-e35426621451', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- Тикет 3: CLOSED (завершен Tim)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('33333333-3333-3333-3333-333333333333', '2025-11-23 12:00:00+00:00', NULL, 'Проверка рабочей визы', 'NORMAL', 'Виза действительна до 2026 года', 'CLOSED', 'EXTERNAL', '2025-11-23 14:00:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ae3df3bc-77fe-436d-a2fb-e35426621451', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- Тикет 4: IN_PROGRESS (Tim работает)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('44444444-4444-4444-4444-444444444444', '2025-11-23 14:00:00+00:00', NULL, 'Проверка документов на вывоз товара', 'HIGH', NULL, 'IN_PROGRESS', 'INTERNAL', '2025-11-23 14:30:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ae3df3bc-77fe-436d-a2fb-e35426621451', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- Тикет 5: NEED_INFO (Tim ждет информацию)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('55555555-5555-5555-5555-555555555555', '2025-11-23 15:00:00+00:00', NULL, 'Несоответствие данных в документах', 'HIGH', NULL, 'NEED_INFO', 'EXTERNAL', '2025-11-23 15:30:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ae3df3bc-77fe-436d-a2fb-e35426621451', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- Тикет 6: REJECTED (Tim отклонил)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('66666666-6666-6666-6666-666666666666', '2025-11-23 16:00:00+00:00', NULL, 'Подозрение на поддельные документы', 'CRITICAL', 'Документы признаны поддельными. Отказано во въезде', 'REJECTED', 'EXTERNAL', '2025-11-23 17:00:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ae3df3bc-77fe-436d-a2fb-e35426621451', 'feefbbd1-f842-4c33-88c9-cf131aef0c78', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- === ВТОРАЯ СМЕНА (681b3a76-3860-4066-999f-f6b9f86b4468) - Tim executor ===
-- Тикет 7: OPEN (назначен Tim, но еще не начат)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('77777777-7777-7777-7777-777777777777', '2025-11-24 09:00:00+00:00', NULL, 'Новая заявка на проверку', 'NORMAL', NULL, 'OPEN', 'EXTERNAL', '2025-11-24 09:00:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ae3df3bc-77fe-436d-a2fb-e35426621451', '681b3a76-3860-4066-999f-f6b9f86b4468', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- Тикет 8: IN_PROGRESS (Tim работает)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('88888888-8888-8888-8888-888888888888', '2025-11-24 10:00:00+00:00', NULL, 'Проверка транзитной визы', 'NORMAL', NULL, 'IN_PROGRESS', 'EXTERNAL', '2025-11-24 10:30:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ae3df3bc-77fe-436d-a2fb-e35426621451', '681b3a76-3860-4066-999f-f6b9f86b4468', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- === ТРЕТЬЯ СМЕНА (9754d5d7-08d7-430c-a4c8-ece8ca62b53d) - Tim executor ===
-- Тикет 9: OPEN (только назначен)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('99999999-9999-9999-9999-999999999999', '2025-11-25 08:00:00+00:00', NULL, 'Срочная проверка документов', 'HIGH', NULL, 'OPEN', 'EXTERNAL', '2025-11-25 08:00:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'ae3df3bc-77fe-436d-a2fb-e35426621451', '9754d5d7-08d7-430c-a4c8-ece8ca62b53d', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- === Тикеты для других исполнителей ===
-- Тикет Stephan в третьей смене (CLOSED)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2025-11-25 09:00:00+00:00', NULL, 'Проверка местных документов', 'LOW', 'Проверка завершена успешно', 'CLOSED', 'INTERNAL', '2025-11-25 11:00:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', 'f8c7eed2-5a92-4d6e-874b-8a9bf2e42a75', '9754d5d7-08d7-430c-a4c8-ece8ca62b53d', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

-- Тикет George во второй смене (IN_PROGRESS)
INSERT INTO tickets (id, created_at, deadline_at, description, priority, resolution, status, ticket_type, updated_at, author_id, executor_id, shift_id, subject_id, appeal_decision) 
VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '2025-11-24 11:00:00+00:00', NULL, 'Проверка рабочего разрешения', 'NORMAL', NULL, 'IN_PROGRESS', 'EXTERNAL', '2025-11-24 11:30:00+00:00', '0247d06e-7f44-4835-b7af-25cc2c9d8afb', '49f05c98-2d2d-44fb-89c6-75c254e7c20d', '681b3a76-3860-4066-999f-f6b9f86b4468', '091b55a5-f94b-429d-8569-9dbfd050ae3c', NULL) ON CONFLICT (id) DO NOTHING;

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

