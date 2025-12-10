-- Flyway Migration V1: Initial Schema
-- Creates core tables for CoopCredit application

-- Create affiliates table
CREATE TABLE affiliates (
    id BIGSERIAL PRIMARY KEY,
    document_type VARCHAR(10) NOT NULL,
    document_number VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),
    salary DECIMAL(12, 2) NOT NULL CHECK (salary > 0),
    affiliation_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    CONSTRAINT chk_affiliate_email CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Create indexes for affiliates
CREATE INDEX idx_affiliate_document ON affiliates(document_number);
CREATE INDEX idx_affiliate_email ON affiliates(email);
CREATE INDEX idx_affiliate_status ON affiliates(status);

-- Create credit_applications table
CREATE TABLE credit_applications (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL,
    requested_amount DECIMAL(12, 2) NOT NULL CHECK (requested_amount > 0),
    purpose VARCHAR(500) NOT NULL,
    application_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'IN_REVIEW')),
    monthly_income DECIMAL(12, 2) NOT NULL CHECK (monthly_income > 0),
    requested_term_months INTEGER NOT NULL CHECK (requested_term_months > 0 AND requested_term_months <= 120),
    CONSTRAINT fk_credit_app_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates(id) ON DELETE CASCADE
);

-- Create indexes for credit_applications
CREATE INDEX idx_credit_app_status ON credit_applications(status);
CREATE INDEX idx_credit_app_affiliate ON credit_applications(affiliate_id);
CREATE INDEX idx_credit_app_date ON credit_applications(application_date);

-- Create risk_evaluations table
CREATE TABLE risk_evaluations (
    id BIGSERIAL PRIMARY KEY,
    credit_application_id BIGINT NOT NULL UNIQUE,
    score INTEGER NOT NULL CHECK (score >= 0 AND score <= 1000),
    evaluation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recommendation VARCHAR(50) NOT NULL CHECK (recommendation IN ('APPROVE', 'REJECT', 'MANUAL_REVIEW')),
    risk_level VARCHAR(20) NOT NULL CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH')),
    external_reference VARCHAR(100),
    CONSTRAINT fk_risk_eval_credit_app FOREIGN KEY (credit_application_id) REFERENCES credit_applications(id) ON DELETE CASCADE
);

-- Create indexes for risk_evaluations
CREATE INDEX idx_risk_eval_credit_app ON risk_evaluations(credit_application_id);
CREATE INDEX idx_risk_eval_date ON risk_evaluations(evaluation_date);

-- Create roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE CHECK (name IN ('ROLE_AFILIADO', 'ROLE_ANALISTA', 'ROLE_ADMIN'))
);

-- Create unique index for role name
CREATE INDEX idx_role_name ON roles(name);

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    affiliate_id BIGINT,
    CONSTRAINT fk_user_affiliate FOREIGN KEY (affiliate_id) REFERENCES affiliates(id) ON DELETE SET NULL,
    CONSTRAINT chk_user_email CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

-- Create indexes for users
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);

-- Create user_roles junction table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Add comments for documentation
COMMENT ON TABLE affiliates IS 'Stores affiliate (member) information for the credit cooperative';
COMMENT ON TABLE credit_applications IS 'Stores credit application requests made by affiliates';
COMMENT ON TABLE risk_evaluations IS 'Stores risk assessment results from external risk central service';
COMMENT ON TABLE users IS 'Stores system user authentication and authorization data';
COMMENT ON TABLE roles IS 'Stores available system roles (AFILIADO, ANALISTA, ADMIN)';
COMMENT ON TABLE user_roles IS 'Junction table linking users to their assigned roles';
