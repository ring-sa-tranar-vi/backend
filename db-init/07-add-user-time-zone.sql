-- Add the user time-zone column for existing databases and fresh init runs.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS time_zone VARCHAR(255) NOT NULL DEFAULT 'UTC';
