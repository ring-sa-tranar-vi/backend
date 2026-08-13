-- =============================================================================
-- 02-migration.sql: Database updates after initial schema
-- =============================================================================

-- Allow TRIGGERED as a valid scheduled call status
ALTER TABLE scheduled_call
DROP CONSTRAINT IF EXISTS scheduled_call_call_back_status_check;

ALTER TABLE scheduled_call
    ADD CONSTRAINT scheduled_call_call_back_status_check
        CHECK (
            call_back_status IN (
                                 'PENDING',
                                 'TRIGGERED',
                                 'COMPLETED',
                                 'CANCELLED',
                                 'MISSED'
                )
            );