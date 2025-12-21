-- Добавление статуса APPROVED в enum TicketStatus
-- Обновление check constraint для status в таблице tickets

-- Удаляем старый constraint если он существует
ALTER TABLE tickets
DROP CONSTRAINT IF EXISTS tickets_status_check;

-- Создаем новый constraint со всеми статусами включая APPROVED
ALTER TABLE tickets
ADD CONSTRAINT tickets_status_check 
CHECK (status IN (
    'OPEN',
    'IN_PROGRESS',
    'NEED_INFO',
    'APPROVED',
    'CLOSED',
    'REJECTED'
));

-- Комментарий
COMMENT ON COLUMN tickets.status IS 'Статус тикета: OPEN, IN_PROGRESS, NEED_INFO, APPROVED, CLOSED, REJECTED';

