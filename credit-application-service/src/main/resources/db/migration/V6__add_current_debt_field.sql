-- Flyway Migration V6: Add current_debt field to credit_applications
-- Adds current_debt column

ALTER TABLE credit_applications 
ADD COLUMN IF NOT EXISTS current_debt DECIMAL(12, 2);

COMMENT ON COLUMN credit_applications.current_debt IS 'Current debt of the applicant at the time of application';
