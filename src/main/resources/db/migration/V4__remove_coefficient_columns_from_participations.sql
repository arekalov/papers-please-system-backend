-- Удаление устаревших колонок bonus_coefficient и penalty_coefficient
-- Теперь используются абсолютные значения wage и penalty

ALTER TABLE participations
DROP COLUMN IF EXISTS bonus_coefficient;

ALTER TABLE participations
DROP COLUMN IF EXISTS penalty_coefficient;

-- Комментарии к актуальным колонкам
COMMENT ON COLUMN participations.wage IS 'Абсолютное значение коэффициента оплаты/премии (заменяет bonus_coefficient)';
COMMENT ON COLUMN participations.penalty IS 'Абсолютное значение коэффициента штрафа (заменяет penalty_coefficient)';

