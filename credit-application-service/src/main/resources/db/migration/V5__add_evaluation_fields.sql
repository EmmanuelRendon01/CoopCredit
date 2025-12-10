-- Flyway Migration V5: Add evaluation fields to credit_applications
-- Adds evaluation_date and evaluation_comments columns

ALTER TABLE credit_applications 
ADD COLUMN IF NOT EXISTS evaluation_date TIMESTAMP;

ALTER TABLE credit_applications 
ADD COLUMN IF NOT EXISTS evaluation_comments VARCHAR(1000);

COMMENT ON COLUMN credit_applications.evaluation_date IS 'Date when the credit application was evaluated';
COMMENT ON COLUMN credit_applications.evaluation_comments IS 'Comments from the risk evaluation including score, level, and factors';
