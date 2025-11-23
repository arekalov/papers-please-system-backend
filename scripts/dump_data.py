#!/usr/bin/env python3
"""
Script to dump data from PostgreSQL database and generate SQL INSERT statements
"""

import os
import psycopg2
from urllib.parse import urlparse

# Database connection parameters
DB_URL = os.getenv('DB_URL', 'postgresql://neondb_owner:npg_AQuTw7XShcL6@ep-fancy-mud-a4icq7ux-pooler.us-east-1.aws.neon.tech/neondb?sslmode=require')

# Parse connection URL
result = urlparse(DB_URL)
username = result.username
password = result.password
database = result.path[1:]
hostname = result.hostname
port = result.port or 5432

# Tables to dump (in order to respect foreign key constraints)
TABLES = [
    'upks',
    'users',
    'shifts',
    'participations',
    'tickets',
    'ticket_relations',
    'documents',
    'ticket_documents',
    'events',
    'notifications',
]

def connect_db():
    """Connect to the PostgreSQL database"""
    try:
        conn = psycopg2.connect(
            host=hostname,
            port=port,
            database=database,
            user=username,
            password=password,
            sslmode='require'
        )
        return conn
    except Exception as e:
        print(f"❌ Error connecting to database: {e}")
        return None

def get_column_names(cursor, table_name):
    """Get column names for a table"""
    cursor.execute(f"""
        SELECT column_name, data_type
        FROM information_schema.columns
        WHERE table_name = '{table_name}'
        AND table_schema = 'public'
        ORDER BY ordinal_position;
    """)
    return cursor.fetchall()

def format_value(value):
    """Format value for SQL INSERT"""
    if value is None:
        return 'NULL'
    elif isinstance(value, (int, float)):
        return str(value)
    elif isinstance(value, bool):
        return 'true' if value else 'false'
    else:
        # Escape single quotes
        escaped = str(value).replace("'", "''")
        return f"'{escaped}'"

def dump_table_data(cursor, table_name, output_file):
    """Dump data from a single table"""
    try:
        # Get column information
        columns_info = get_column_names(cursor, table_name)
        if not columns_info:
            print(f"  ⚠️  Table '{table_name}' not found or empty")
            return
        
        column_names = [col[0] for col in columns_info]
        columns_str = ', '.join(column_names)
        
        # Get table data
        cursor.execute(f'SELECT * FROM {table_name};')
        rows = cursor.fetchall()
        
        if not rows:
            print(f"  ℹ️  Table '{table_name}' is empty")
            return
        
        # Write INSERT statements
        output_file.write(f"\n-- Data for table: {table_name}\n")
        output_file.write(f"-- Rows: {len(rows)}\n\n")
        
        for row in rows:
            values = ', '.join(format_value(val) for val in row)
            output_file.write(f"INSERT INTO {table_name} ({columns_str}) VALUES ({values});\n")
        
        print(f"  ✅ Dumped {len(rows)} rows from '{table_name}'")
        
    except Exception as e:
        print(f"  ❌ Error dumping table '{table_name}': {e}")

def main():
    print("=== Dumping data from PostgreSQL database ===")
    print(f"Host: {hostname}")
    print(f"Database: {database}")
    print(f"Username: {username}")
    print()
    
    # Connect to database
    conn = connect_db()
    if not conn:
        return
    
    cursor = conn.cursor()
    
    # Create output file
    output_path = 'scripts/seed-data.sql'
    os.makedirs('scripts', exist_ok=True)
    
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write("-- Seed data for Papers Please System\n")
        f.write("-- Generated automatically\n")
        f.write(f"-- Database: {database}\n")
        f.write("-- Date: " + str(psycopg2.TimestampFromTicks(__import__('time').time())) + "\n")
        f.write("\n-- Disable triggers and constraints for faster insertion\n")
        f.write("SET session_replication_role = replica;\n\n")
        
        # Dump each table
        for table in TABLES:
            dump_table_data(cursor, table, f)
        
        f.write("\n-- Re-enable triggers and constraints\n")
        f.write("SET session_replication_role = DEFAULT;\n")
        f.write("\n-- Update sequences\n")
        
        # Reset sequences for tables with auto-increment IDs
        for table in TABLES:
            f.write(f"SELECT setval(pg_get_serial_sequence('{table}', 'id'), COALESCE((SELECT MAX(id) FROM {table}), 1), false);\n")
    
    cursor.close()
    conn.close()
    
    print()
    print(f"✅ Data successfully dumped to {output_path}")
    print()
    print("To restore data, run:")
    print(f"  psql -h <host> -U <username> -d <database> -f {output_path}")

if __name__ == '__main__':
    main()

