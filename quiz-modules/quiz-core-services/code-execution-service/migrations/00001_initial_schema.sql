-- +goose Up
-- Enable required PostgreSQL extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create custom types for execution status and programming languages
CREATE TYPE execution_status AS ENUM (
    'PENDING', 
    'RUNNING', 
    'COMPLETED', 
    'FAILED', 
    'TIMEOUT'
);

CREATE TYPE programming_language AS ENUM (
    'java', 
    'typescript',
    'go', 
);

-- Create executions table for tracking code execution requests
CREATE TABLE executions (
    id BIGSERIAL PRIMARY KEY,
    execution_id VARCHAR(255) NOT NULL UNIQUE,
    
    -- Code details
    language programming_language NOT NULL,
    code TEXT NOT NULL,
    
    -- Execution status and timing
    status execution_status NOT NULL DEFAULT 'PENDING',
    started_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP WITH TIME ZONE,
    execution_time_ms INTEGER CONSTRAINT check_execution_time_positive CHECK (execution_time_ms >= 0),
    
    -- Execution results
    output TEXT,
    error_output TEXT,
    exit_code INTEGER,
    success BOOLEAN NOT NULL DEFAULT FALSE,
    
    -- Optional context for future integration
    question_id BIGINT,
    user_id BIGINT,
    session_id VARCHAR(255),
    
    -- Audit timestamps
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    
    -- Constraints
    CONSTRAINT check_execution_id_length CHECK (LENGTH(execution_id) >= 5),
    CONSTRAINT check_code_not_empty CHECK (LENGTH(TRIM(code)) > 0),
    CONSTRAINT check_completed_at_after_started CHECK (
        completed_at IS NULL OR completed_at >= started_at
    )
);

-- test_cases table for storing individual test case results
CREATE TABLE test_cases (
    id BIGSERIAL PRIMARY KEY,
    execution_id VARCHAR(255) NOT NULL,
    
    -- Test case details
    test_name VARCHAR(255) NOT NULL,
    input_data TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    actual_output TEXT,
    
    -- Test execution results  
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    execution_time_ms INTEGER CONSTRAINT check_test_execution_time_positive CHECK (execution_time_ms >= 0),
    error_message TEXT,
    
    -- Audit timestamp
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_test_cases_execution_id
        FOREIGN KEY (execution_id) 
        REFERENCES executions(execution_id) 
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    
    CONSTRAINT check_test_name_not_empty CHECK (LENGTH(TRIM(test_name)) > 0),
    CONSTRAINT check_actual_output_when_passed CHECK (
        NOT passed OR (passed AND actual_output IS NOT NULL)
    )
);

COMMENT ON TABLE executions IS 'Stores code execution requests and their results';
COMMENT ON TABLE test_cases IS 'Stores individual test case results for each execution';

COMMENT ON COLUMN executions.execution_id IS 'Unique identifier for tracking execution requests';
COMMENT ON COLUMN executions.execution_time_ms IS 'Total execution time in milliseconds';
COMMENT ON COLUMN test_cases.execution_id IS 'References the parent execution request';

-- +goose Down
-- Drop tables in reverse order (respecting foreign keys)
DROP TABLE IF EXISTS test_cases;
DROP TABLE IF EXISTS executions;

-- Drop custom types
DROP TYPE IF EXISTS programming_language;
DROP TYPE IF EXISTS execution_status;

-- Note: DON'T drop pgcrypto extension as it might be used by other parts of the system