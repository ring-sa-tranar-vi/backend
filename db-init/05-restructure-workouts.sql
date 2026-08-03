ALTER TABLE workouts ALTER COLUMN instructions TYPE TEXT;

UPDATE workouts
SET instructions = workout_instructions
WHERE workout_instructions IS NOT NULL;

ALTER TABLE workouts RENAME COLUMN workout_guidance TO guidance;
ALTER TABLE workouts RENAME COLUMN workout_image TO image;
ALTER TABLE workouts RENAME COLUMN workout_video TO video;

ALTER TABLE workouts DROP COLUMN instructions_audio;
ALTER TABLE workouts DROP COLUMN instructions_image;
ALTER TABLE workouts DROP COLUMN workout_audio;
ALTER TABLE workouts DROP COLUMN duration_seconds;
ALTER TABLE workouts DROP COLUMN beginner_friendly;
ALTER TABLE workouts DROP COLUMN knee_friendly;
ALTER TABLE workouts DROP COLUMN low_impact;
ALTER TABLE workouts DROP COLUMN seated;
ALTER TABLE workouts DROP COLUMN duration_minutes;
ALTER TABLE workouts DROP COLUMN instructions_video;
ALTER TABLE workouts DROP COLUMN instructions_video_start;
ALTER TABLE workouts DROP COLUMN instructions_video_stop;
ALTER TABLE workouts DROP COLUMN subtitle_text;
ALTER TABLE workouts DROP COLUMN instructions_subtitle_text;
ALTER TABLE workouts DROP COLUMN workout_instructions;
ALTER TABLE workouts DROP COLUMN trainer_id;
