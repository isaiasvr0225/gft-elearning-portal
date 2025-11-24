-- Add is_completed flag to courses to track when a course is fully completed by a user base
ALTER TABLE courses ADD COLUMN IF NOT EXISTS is_completed BOOLEAN NOT NULL DEFAULT FALSE;