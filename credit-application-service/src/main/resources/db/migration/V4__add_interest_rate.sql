-- V4: Add interest_rate column to credit_applications table
-- Author: CoopCredit Team
-- Date: 2025-12-09

-- Add interest_rate column
ALTER TABLE credit_applications 
ADD COLUMN interest_rate DECIMAL(5,2);

-- Update existing records with default 12% annual interest rate
UPDATE credit_applications 
SET interest_rate = 12.0 
WHERE interest_rate IS NULL;

-- Add NOT NULL constraint after populating existing data
ALTER TABLE credit_applications 
ALTER COLUMN interest_rate SET NOT NULL;

-- Add check constraint to ensure valid interest rate (between 0 and 100)
ALTER TABLE credit_applications 
ADD CONSTRAINT chk_interest_rate CHECK (interest_rate >= 0 AND interest_rate <= 100);

COMMENT ON COLUMN credit_applications.interest_rate IS 'Annual interest rate as percentage (e.g., 12.5 for 12.5%)';
