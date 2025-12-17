-- Добавление колонки attach_to_profile к таблице documents
-- Добавляем колонку (сначала без NOT NULL, если её пытался создать Hibernate)
ALTER TABLE documents
ADD COLUMN IF NOT EXISTS attach_to_profile BOOLEAN DEFAULT false;

-- Обновляем существующие NULL значения на false
UPDATE documents
SET attach_to_profile = false
WHERE attach_to_profile IS NULL;

-- Теперь делаем колонку NOT NULL
ALTER TABLE documents
ALTER COLUMN attach_to_profile SET NOT NULL;

-- Комментарий к колонке
COMMENT ON COLUMN documents.attach_to_profile IS 'Флаг, указывающий, прикреплен ли документ к профилю пользователя для отображения в истории';

