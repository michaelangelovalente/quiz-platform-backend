package api

import (
	"code-execution-service/internal/api/executor"
	"code-execution-service/internal/store"
	"context"
	"database/sql"
	"log"
	"strconv"
	"time"
)

type ExecuteRequest struct {
	Language string `json:"language" validate:"required,oneof=java python javascript go cpp"`
	Code     string `json:"code" validate:"required,min=1,max=10000"`

	// Optional test cases (if not provided, use defaults)
	TestCases []SimpleTestCase `json:"test_cases,omitempty"`

	// Optional context
	QuestionID *int64  `json:"question_id,omitempty"`
	UserID     *int64  `json:"user_id,omitempty"`
	SessionID  *string `json:"session_id,omitempty"`
}

type SimpleTestCase struct {
	Name           string `json:"name"`
	Input          string `json:"input"`
	ExpectedOutput string `json:"expected_output"`
}

type ExecuteResponse struct {
	ExecutionID   string           `json:"execution_id"`
	Status        string           `json:"status"`
	Success       bool             `json:"success"`
	Output        string           `json:"output,omitempty"`
	ErrorOutput   string           `json:"error_output,omitempty"`
	ExecutionTime int              `json:"execution_time_ms"`
	TestResults   []TestCaseResult `json:"test_results,omitempty"`
	StartedAt     time.Time        `json:"started_at"`
	CompletedAt   *time.Time       `json:"completed_at,omitempty"`
}

type TestCaseResult struct {
	TestName       string `json:"test_name"`
	Input          string `json:"input"`
	ExpectedOutput string `json:"expected_output"`
	ActualOutput   string `json:"actual_output"`
	Passed         bool   `json:"passed"`
	ExecutionTime  int    `json:"execution_time_ms"`
	ErrorMessage   string `json:"error_message,omitempty"`
}

type ExecutionHandler struct {
	db       *sql.DB
	executor *executor.DockerExecutor
	logger   *log.Logger
}

func NewExecutionHandler(db *sql.DB, logger *log.Logger) *ExecutionHandler {
	return &ExecutionHandler{
		db:       db,
		executor: executor.NewDockerExecutor(),
		logger:   logger,
	}
}

func (s *ExecutionHandler) ExecuteCode(ctx context.Context, req ExecuteRequest) (*ExecuteResponse, error) {
	// Generate execution ID
	executionID := "exec_" + strconv.FormatInt(time.Now().UnixNano(), 10)

	// Save initial execution record --> TODO: move init to store. Handler should handle parsing
	execution := &store.Execution{
		ExecutionID: executionID,
		Language:    req.Language,
		Code:        req.Code,
		Status:      "RUNNING",
		StartedAt:   time.Now(),
		QuestionID:  req.QuestionID,
		//UserID:      req.UserID,
		SessionID: req.SessionID,
	}

	err := s.db.SaveExecution(ctx, execution)
	if err != nil {
		return nil, err
	}

	// Execute code
	result, err := s.executor.ExecuteCode(req.Language, req.Code)

	// Update execution record
	execution.Status = "COMPLETED"
	if err != nil {
		execution.Status = "FAILED"
		execution.ErrorOutput = &err.Error()
	} else {
		execution.Output = &result.Output
		execution.Success = result.Success
	}
	execution.CompletedAt = &time.Now()
	execution.ExecutionTime = &result.ExecutionTime

	err = s.db.UpdateExecution(ctx, execution)
	if err != nil {
		s.logger.Printf("Failed to update execution: %v", err)
	}

	// Build response
	return &ExecuteResponse{
		ExecutionID:   executionID,
		Status:        execution.Status,
		Success:       execution.Success,
		Output:        result.Output,
		ErrorOutput:   result.ErrorOutput,
		ExecutionTime: result.ExecutionTime,
		StartedAt:     execution.StartedAt,
		CompletedAt:   execution.CompletedAt,
	}, nil
}
