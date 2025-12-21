-- Добавление поля upk_id в таблицу events
-- События теперь привязаны к конкретному УПК

-- Добавляем колонку upk_id (пока nullable для существующих данных)
ALTER TABLE events
ADD COLUMN upk_id UUID;

-- Заполняем существующие записи первым доступным UPK
-- Если событий нет или UPK нет, эта команда ничего не сделает
UPDATE events 
SET upk_id = (SELECT id FROM upks ORDER BY created_at LIMIT 1)
WHERE upk_id IS NULL;

-- Удаляем записи, которые не удалось связать с UPK (на случай, если в БД нет UPK)
DELETE FROM events WHERE upk_id IS NULL;

-- Теперь делаем поле обязательным
ALTER TABLE events 
ALTER COLUMN upk_id SET NOT NULL;

-- Добавляем внешний ключ на таблицу upks
ALTER TABLE events
ADD CONSTRAINT fk_events_upk
FOREIGN KEY (upk_id) REFERENCES upks(id) ON DELETE CASCADE;

-- Комментарий
COMMENT ON COLUMN events.upk_id IS 'Идентификатор УПК, к которому относится событие';

