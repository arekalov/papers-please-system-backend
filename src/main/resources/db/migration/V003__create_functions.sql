-- ============================================================================
-- PL/pgSQL Functions for Papers Please System
-- Migration V003 - Critical Business Logic Functions
-- ============================================================================

-- ============================================================================
-- 1. DOCUMENT MANAGEMENT FUNCTIONS
-- ============================================================================

-- Быстро получить все активные документы пользователя
-- Use Case: 4.1.14 - Просмотр документов мигранта, Use Case 102 - Проверка документов
CREATE OR REPLACE FUNCTION get_active_documents(p_owner_id BIGINT)
RETURNS TABLE(
    id BIGINT,
    document_type VARCHAR(50),
    body TEXT,
    issued_at TIMESTAMP,
    expires_at TIMESTAMP,
    uploaded_at TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        d.id,
        d.document_type::VARCHAR,
        d.body,
        d.issued_at,
        d.expires_at,
        d.uploaded_at
    FROM documents d
    WHERE d.owner_id = p_owner_id
      AND (d.expires_at IS NULL OR d.expires_at > NOW());
END;
$$ LANGUAGE plpgsql STABLE;

COMMENT ON FUNCTION get_active_documents(BIGINT) IS 
'Возвращает все действительные (не истекшие) документы пользователя. 
Используется при создании заявки и проверке инспектором.';


-- ============================================================================
-- 2. USER & UPK MANAGEMENT FUNCTIONS
-- ============================================================================

-- Получить список всех сотрудников УПК с их ролями
-- Use Case 101: Формирование смены начальником
CREATE OR REPLACE FUNCTION get_users_by_upk(p_upk_id BIGINT)
RETURNS TABLE(
    id BIGINT,
    name VARCHAR(255),
    email VARCHAR(255),
    role VARCHAR(50)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.id,
        u.name,
        u.email,
        u.role::VARCHAR
    FROM users u
    WHERE u.upk_id = p_upk_id
    ORDER BY u.role, u.name;
END;
$$ LANGUAGE plpgsql STABLE;

COMMENT ON FUNCTION get_users_by_upk(BIGINT) IS 
'Возвращает список всех сотрудников конкретного УПК.
Используется начальником при формировании состава смены.';


-- ============================================================================
-- 3. TICKET DOCUMENT FUNCTIONS
-- ============================================================================

-- Получить все документы, прикрепленные к тикету
-- Use Case 102: Проверка документов инспектором
CREATE OR REPLACE FUNCTION get_ticket_documents(p_ticket_id BIGINT)
RETURNS TABLE(
    id BIGINT,
    document_type VARCHAR(50),
    body TEXT,
    issued_at TIMESTAMP,
    expires_at TIMESTAMP,
    owner_name VARCHAR(255)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        d.id,
        d.document_type::VARCHAR,
        d.body,
        d.issued_at,
        d.expires_at,
        u.name as owner_name
    FROM documents d
    JOIN ticket_documents td ON d.id = td.document_id
    JOIN users u ON d.owner_id = u.id
    WHERE td.ticket_id = p_ticket_id
    ORDER BY d.document_type;
END;
$$ LANGUAGE plpgsql STABLE;

COMMENT ON FUNCTION get_ticket_documents(BIGINT) IS 
'Возвращает все документы, прикрепленные к тикету.
Используется инспектором при проверке заявки.';


-- ============================================================================
-- END OF MIGRATION
-- ============================================================================

