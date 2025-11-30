-- Переименование колонок в таблице participations
-- Изменение bonus_coefficient -> wage и penalty_coefficient -> penalty

-- Проверяем и выполняем миграцию только если старые колонки существуют
DO $$ 
BEGIN
    -- Проверяем, существуют ли старые колонки
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'participations' 
        AND column_name = 'bonus_coefficient'
    ) THEN
        -- Устанавливаем значения по умолчанию для NULL записей
        UPDATE participations 
        SET bonus_coefficient = 1.0 
        WHERE bonus_coefficient IS NULL;
        
        UPDATE participations 
        SET penalty_coefficient = 0.0 
        WHERE penalty_coefficient IS NULL;
        
        -- Переименовываем колонки
        ALTER TABLE participations 
          RENAME COLUMN bonus_coefficient TO wage;
        
        ALTER TABLE participations 
          RENAME COLUMN penalty_coefficient TO penalty;
        
        -- Устанавливаем NOT NULL constraints
        ALTER TABLE participations 
          ALTER COLUMN wage SET NOT NULL;
        
        ALTER TABLE participations 
          ALTER COLUMN penalty SET NOT NULL;
        
        RAISE NOTICE 'Successfully renamed bonus_coefficient to wage and penalty_coefficient to penalty';
    ELSE
        RAISE NOTICE 'Columns already renamed or do not exist - skipping migration';
    END IF;
END $$;

