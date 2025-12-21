-- Полный откат миграции V8: удаление поля upk_id из таблицы events
-- Выполните этот скрипт, затем перезапустите приложение для повторного применения миграции

-- 1. Удаляем внешний ключ
ALTER TABLE events
DROP CONSTRAINT IF EXISTS fk_events_upk;

-- 2. Удаляем колонку upk_id
ALTER TABLE events
DROP COLUMN IF EXISTS upk_id;

-- 3. Удаляем запись о миграции из истории Flyway
DELETE FROM flyway_schema_history WHERE version = '8';

-- После выполнения этого скрипта перезапустите приложение
-- Миграция V8 применится заново с корректным заполнением данных

