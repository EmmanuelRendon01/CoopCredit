-- Initial setup script for CoopCredit database
-- This is executed automatically when the PostgreSQL container starts for the first time

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- The database 'coopcredit' is already created by POSTGRES_DB environment variable
-- The user 'coopcredit_user' is already created by POSTGRES_USER environment variable

-- Grant all privileges (already done by PostgreSQL init, but explicit for clarity)
GRANT ALL PRIVILEGES ON DATABASE coopcredit TO coopcredit_user;

-- Log initialization
\echo 'CoopCredit database initialized successfully'
