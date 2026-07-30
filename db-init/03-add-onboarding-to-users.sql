ALTER TABLE users
    ADD COLUMN onboarding BOOLEAN DEFAULT TRUE;

UPDATE users
SET onboarding = TRUE;

ALTER TABLE users
    ALTER COLUMN onboarding SET NOT NULL;