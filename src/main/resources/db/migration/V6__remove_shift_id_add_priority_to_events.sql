-- Открепление событий от УПК и добавление приоритета
-- Добавляем колонку priority
ALTER TABLE events
ADD COLUMN IF NOT EXISTS priority VARCHAR(255) NOT NULL DEFAULT 'NORMAL';

-- Удаляем колонку shift_id
ALTER TABLE events
DROP COLUMN IF EXISTS shift_id;

-- Комментарий к колонке priority
COMMENT ON COLUMN events.priority IS 'Приоритет события (LOW, NORMAL, HIGH, CRITICAL)';

