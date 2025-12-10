-- Flyway Migration V2: Initial Data
-- Inserts seed data for roles and test users

-- Insert system roles
INSERT INTO roles (name) VALUES 
    ('ROLE_AFILIADO'),
    ('ROLE_ANALISTA'),
    ('ROLE_ADMIN');

-- Insert default admin user
-- Password: Admin123! (BCrypt hashed with rounds=10)
INSERT INTO users (username, password, email, enabled) VALUES
    ('admin', '$2b$10$SUFuSAjtKrto9dMFyTafkOid5qedSQDNIkkhtOAEVuFIuEIXsYxUm', 'admin@coopcredit.com', true);

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

-- Insert sample analyst user
-- Password: Analyst123! (BCrypt hashed with rounds=10)
INSERT INTO users (username, password, email, enabled) VALUES
    ('analyst', '$2b$10$vldpaRIrBlT41b8cchg9qeaEzJdoAbEGRxn0c/GIB9oMJSebvaSGS', 'analyst@coopcredit.com', true);

-- Assign ANALISTA role to analyst user
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'analyst' AND r.name = 'ROLE_ANALISTA';

-- Comments
COMMENT ON TABLE roles IS 'Populated with 3 system roles: AFILIADO (affiliate), ANALISTA (analyst), ADMIN (administrator)';
