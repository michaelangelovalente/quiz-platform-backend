-- +goose Up
-- Create performance indexes for frequently queried columns
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_executions_execution_id 
    ON executions(execution_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_executions_status 
    ON executions(status) 
    WHERE status IN ('PENDING', 'RUNNING');

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_executions_created_at 
    ON executions(created_at DESC);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_executions_user_question 
    ON executions(user_id, question_id) 
    WHERE user_id IS NOT NULL AND question_id IS NOT NULL;

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_executions_session 
    ON executions(session_id) 
    WHERE session_id IS NOT NULL;

-- Index for test_cases table
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_test_cases_execution_id 
    ON test_cases(execution_id);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_test_cases_execution_passed 
    ON test_cases(execution_id, passed);

-- Create function for automatically updating the updated_at timestamp
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Add comment to function
COMMENT ON FUNCTION trigger_set_timestamp() IS 'Automatically updates updated_at column on row modification';

-- Create trigger to automatically update the updated_at column
CREATE TRIGGER tr_executions_updated_at
    BEFORE UPDATE ON executions
    FOR EACH ROW
    EXECUTE FUNCTION trigger_set_timestamp();

-- Create partial indexes for better query performance
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_executions_active_status_created 
    ON executions(status, created_at DESC) 
    WHERE status IN ('PENDING', 'RUNNING');

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_executions_completed_success 
    ON executions(completed_at DESC, success) 
    WHERE status = 'COMPLETED';

-- Create index for analytics queries
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_executions_language_status 
    ON executions(language, status, created_at DESC);

-- Add table statistics for better query planning
ANALYZE executions;
ANALYZE test_cases;

-- +goose Down
-- Drop indexes (order doesn't matter for indexes)
DROP INDEX CONCURRENTLY IF EXISTS idx_executions_execution_id;
DROP INDEX CONCURRENTLY IF EXISTS idx_executions_status;
DROP INDEX CONCURRENTLY IF EXISTS idx_executions_created_at;
DROP INDEX CONCURRENTLY IF EXISTS idx_executions_user_question;
DROP INDEX CONCURRENTLY IF EXISTS idx_executions_session;
DROP INDEX CONCURRENTLY IF EXISTS idx_test_cases_execution_id;
DROP INDEX CONCURRENTLY IF EXISTS idx_test_cases_execution_passed;
DROP INDEX CONCURRENTLY IF EXISTS idx_executions_active_status_created;
DROP INDEX CONCURRENTLY IF EXISTS idx_executions_completed_success;
DROP INDEX CONCURRENTLY IF EXISTS idx_executions_language_status;

-- Drop trigger
DROP TRIGGER IF EXISTS tr_executions_updated_at ON executions;

-- Drop function
DROP FUNCTION IF EXISTS trigger_set_timestamp();