-- Скрипт для исправления проблем с Flyway миграциями
-- Используйте этот скрипт, если приложение не запускается из-за ошибок валидации Flyway

-- Показать текущее состояние миграций
SELECT 
    installed_rank,
    version,
    description,
    type,
    script,
    installed_on,
    success
FROM flyway_schema_history 
ORDER BY installed_rank;

-- Удалить проблемную миграцию 003 (которой больше нет в проекте)
DELETE FROM flyway_schema_history WHERE version = '003';

-- Удалить неуспешную миграцию 8 (если она есть и не успешна)
DELETE FROM flyway_schema_history WHERE version = '8' AND success = false;

-- Показать результат
SELECT 'Проблемные миграции удалены. Теперь пересоберите проект и перезапустите приложение.' AS status;

-- Если нужен полный сброс (ВНИМАНИЕ: удалит всю историю миграций!)
-- Раскомментируйте следующую строку только если уверены:
-- TRUNCATE TABLE flyway_schema_history;

