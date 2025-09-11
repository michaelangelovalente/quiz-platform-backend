package api

import (
	"code-execution-service/internal/store"
	"code-execution-service/internal/utils"
	"encoding/json"
	"log"
	"net/http"
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
	executionStore store.ExecutionStore
	logger         *log.Logger
}

func NewExecutionHandler(executionStore store.ExecutionStore, logger *log.Logger) *ExecutionHandler {
	return &ExecutionHandler{
		executionStore: executionStore,
		logger:         logger,
	}
}

func (eh *ExecutionHandler) Execute(w http.ResponseWriter, r *http.Request) {

	var execution store.Execution
	executionID := "exec_" + strconv.FormatInt(time.Now().UnixNano(), 10)
	execution.ExecutionID = executionID

	err := json.NewDecoder(r.Body).Decode(&execution)
	if err != nil {
		eh.logger.Printf("ERROR: decoding json: %v", err)
		utils.WriteJSON(w, http.StatusBadRequest,
			utils.Envelope{
				"error": "invalid request: error decoding json",
			})
	}

	err = eh.executionStore.SaveExecution(&execution)
	if err != nil {
		eh.logger.Printf("ERROR: saving execution: %v", err)
		utils.WriteJSON(w, http.StatusInternalServerError,
			utils.Envelope{
				"error": "internal server error: error saving execution",
			})
	}

}
