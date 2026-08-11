-- Idempotent migration for databases created before the current organisation schema.
ALTER TABLE organisation
    DROP COLUMN IF EXISTS initial_followers;

ALTER TABLE organisation
    ADD COLUMN IF NOT EXISTS motivation VARCHAR(2000);

ALTER TABLE organisation
    ALTER COLUMN description TYPE VARCHAR(2000);

CREATE UNIQUE INDEX IF NOT EXISTS uk_organisation_organizer
    ON organisation (organizer_id)
    WHERE organizer_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_org_app_active_user
    ON organization_application (user_id)
    WHERE application_status IN ('PENDING', 'APPROVED');
