-- Обновление check constraint для notification_type
-- Добавление всех актуальных типов уведомлений из enum NotificationType

-- Удаляем старый constraint
ALTER TABLE notifications
DROP CONSTRAINT IF EXISTS notifications_notification_type_check;

-- Создаем новый constraint со всеми типами
ALTER TABLE notifications
ADD CONSTRAINT notifications_notification_type_check 
CHECK (notification_type IN (
    'TICKET_ASSIGNED',
    'TICKET_UPDATED',
    'SHIFT_STARTED',
    'APPEAL_RESULT',
    'EVENT_UPDATE'
));

-- Комментарий
COMMENT ON COLUMN notifications.notification_type IS 'Тип уведомления: TICKET_ASSIGNED, TICKET_UPDATED, SHIFT_STARTED, APPEAL_RESULT, EVENT_UPDATE';


