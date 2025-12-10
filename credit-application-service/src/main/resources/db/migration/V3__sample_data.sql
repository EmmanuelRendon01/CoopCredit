-- Flyway Migration V3: Sample Test Data (Optional)
-- Inserts sample affiliates and credit applications for testing
-- This migration is optional and can be skipped in production

-- Insert sample affiliates
INSERT INTO affiliates (document_type, document_number, first_name, last_name, email, phone, salary, affiliation_date, status) VALUES
    ('CC', '1234567890', 'Juan', 'Pérez', 'juan.perez@email.com', '+57 300 1234567', 3000000.00, CURRENT_DATE - INTERVAL '6 months', 'ACTIVE'),
    ('CC', '0987654321', 'María', 'González', 'maria.gonzalez@email.com', '+57 301 7654321', 4500000.00, CURRENT_DATE - INTERVAL '1 year', 'ACTIVE'),
    ('TI', '1122334455', 'Carlos', 'Rodríguez', 'carlos.rodriguez@email.com', '+57 302 5544332', 2500000.00, CURRENT_DATE - INTERVAL '2 months', 'ACTIVE'),
    ('CC', '5566778899', 'Ana', 'Martínez', 'ana.martinez@email.com', '+57 303 9988776', 5000000.00, CURRENT_DATE - INTERVAL '2 years', 'ACTIVE'),
    ('CE', '9988776655', 'Pedro', 'López', 'pedro.lopez@email.com', '+57 304 6677889', 3500000.00, CURRENT_DATE - INTERVAL '1 month', 'INACTIVE');

-- Create users for affiliates
INSERT INTO users (username, password, email, enabled, affiliate_id) 
SELECT 
    LOWER(REPLACE(a.first_name, ' ', '') || SUBSTRING(a.last_name, 1, 1)),
    '$2b$10$PwXmwe.VTxtMtOZS6M1Cf.6H2ZIgnEhWzqv5iLQKkGGGQnXcfJbV6', -- Password: Affiliate123!
    a.email,
    true,
    a.id
FROM affiliates a
WHERE NOT EXISTS (SELECT 1 FROM users u WHERE u.email = a.email);

-- Assign AFILIADO role to affiliate users
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
CROSS JOIN roles r
WHERE r.name = 'ROLE_AFILIADO'
AND u.affiliate_id IS NOT NULL
AND NOT EXISTS (
    SELECT 1 FROM user_roles ur 
    WHERE ur.user_id = u.id AND ur.role_id = r.id
);

-- Insert sample credit applications
INSERT INTO credit_applications (affiliate_id, requested_amount, purpose, application_date, status, monthly_income, requested_term_months)
SELECT 
    a.id,
    5000000.00,
    'Compra de vehículo',
    CURRENT_TIMESTAMP - INTERVAL '5 days',
    'APPROVED',
    a.salary,
    36
FROM affiliates a
WHERE a.document_number = '1234567890';

-- Insert risk evaluation for the approved application
INSERT INTO risk_evaluations (credit_application_id, score, evaluation_date, recommendation, risk_level, external_reference)
SELECT 
    ca.id,
    750,
    CURRENT_TIMESTAMP - INTERVAL '5 days',
    'APPROVE',
    'LOW',
    'RISK-' || TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDD') || '-001'
FROM credit_applications ca
WHERE ca.requested_amount = 5000000.00
LIMIT 1;

-- Insert pending application
INSERT INTO credit_applications (affiliate_id, requested_amount, purpose, application_date, status, monthly_income, requested_term_months)
SELECT 
    a.id,
    10000000.00,
    'Remodelación de vivienda',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    'PENDING',
    a.salary,
    48
FROM affiliates a
WHERE a.document_number = '0987654321';

-- Insert rejected application
INSERT INTO credit_applications (affiliate_id, requested_amount, purpose, application_date, status, monthly_income, requested_term_months)
SELECT 
    a.id,
    3000000.00,
    'Consolidación de deudas',
    CURRENT_TIMESTAMP - INTERVAL '10 days',
    'REJECTED',
    a.salary,
    24
FROM affiliates a
WHERE a.document_number = '5566778899';

-- Insert risk evaluation for rejected application
INSERT INTO risk_evaluations (credit_application_id, score, evaluation_date, recommendation, risk_level, external_reference)
SELECT 
    ca.id,
    350,
    CURRENT_TIMESTAMP - INTERVAL '10 days',
    'REJECT',
    'HIGH',
    'RISK-' || TO_CHAR(CURRENT_TIMESTAMP, 'YYYYMMDD') || '-002'
FROM credit_applications ca
WHERE ca.status = 'REJECTED'
LIMIT 1;

-- Comments
COMMENT ON TABLE affiliates IS 'Sample data: 5 test affiliates with varying statuses and affiliation dates';
COMMENT ON TABLE credit_applications IS 'Sample data: 3 test applications (APPROVED, PENDING, REJECTED)';
