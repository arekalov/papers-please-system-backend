-- ============================================================================
-- Fix PL/pgSQL Functions - Direct Apply to Database
-- This script recreates the functions with correct UUID and TIMESTAMP types
-- ============================================================================

-- Drop existing functions (both BIGINT and UUID versions just in case)
DROP FUNCTION IF EXISTS get_active_documents(BIGINT);
DROP FUNCTION IF EXISTS get_active_documents(UUID);
DROP FUNCTION IF EXISTS get_ticket_documents(BIGINT);
DROP FUNCTION IF EXISTS get_ticket_documents(UUID);
DROP FUNCTION IF EXISTS get_users_by_upk(BIGINT);
DROP FUNCTION IF EXISTS get_users_by_upk(UUID);

-- Recreate with correct UUID types
CREATE OR REPLACE FUNCTION get_active_documents(p_owner_id UUID)
RETURNS TABLE(
    id UUID,
    document_type VARCHAR(50),
    body TEXT,
    issued_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE,
    uploaded_at TIMESTAMP WITH TIME ZONE,
    owner_id UUID
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        d.id,
        d.document_type::VARCHAR,
        d.body,
        d.issued_at,
        d.expires_at,
        d.uploaded_at,
        d.owner_id
    FROM documents d
    WHERE d.owner_id = p_owner_id
      AND (d.expires_at IS NULL OR d.expires_at > NOW());
END;
$$ LANGUAGE plpgsql STABLE;

CREATE OR REPLACE FUNCTION get_ticket_documents(p_ticket_id UUID)
RETURNS TABLE(
    id UUID,
    document_type VARCHAR(50),
    body TEXT,
    issued_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE,
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

CREATE OR REPLACE FUNCTION get_users_by_upk(p_upk_id UUID)
RETURNS TABLE(
    id UUID,
    email VARCHAR(255),
    name VARCHAR(255),
    password_hash VARCHAR(255),
    role VARCHAR(50),
    upk_id UUID
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.id,
        u.email,
        u.name,
        u.password_hash,
        u.role::VARCHAR,
        u.upk_id
    FROM users u
    WHERE u.upk_id = p_upk_id
    ORDER BY u.role, u.name;
END;
$$ LANGUAGE plpgsql STABLE;

-- Success message
SELECT 'All functions recreated successfully with correct UUID and TIMESTAMP WITH TIME ZONE types!' as status;

