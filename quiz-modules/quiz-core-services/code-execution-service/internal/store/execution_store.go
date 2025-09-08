package store

import (
	"context"
	"database/sql"
	"time"
)

type Execution struct {
	ID            int64      `db:"id"`
	ExecutionID   string     `db:"execution_id"`
	Language      string     `db:"language"`
	Code          string     `db:"code"`
	Status        string     `db:"status"`
	StartedAt     time.Time  `db:"started_at"`
	CompletedAt   *time.Time `db:"completed_at"`
	ExecutionTime *int       `db:"execution_time_ms"`
	Output        *string    `db:"output"`
	ErrorOutput   *string    `db:"error_output"`
	ExitCode      *int       `db:"exit_code"`
	Success       bool       `db:"success"`
	QuestionID    *int64     `db:"question_id"`
	//UserID        *int64     `DB:"user_id"`
	SessionID *string   `db:"session_id"`
	CreatedAt time.Time `db:"created_at"`
	UpdatedAt time.Time `db:"updated_at"`
}

// TODO remove structs below once code works -- they're just simple test cases
type TestCase struct {
	ID             int64     `db:"id"`
	ExecutionID    string    `db:"execution_id"`
	TestName       string    `db:"test_name"`
	InputData      string    `db:"input_data"`
	ExpectedOutput string    `db:"expected_output"`
	ActualOutput   *string   `db:"actual_output"`
	Passed         bool      `db:"passed"`
	ExecutionTime  *int      `db:"execution_time_ms"`
	ErrorMessage   *string   `db:"error_message"`
	CreatedAt      time.Time `db:"created_at"`
}

type SimpleTestCase struct {
	Name           string `json:"name"`
	Input          string `json:"input"`
	ExpectedOutput string `json:"expected_output"`
}

type ExecutionStore interface {
	GetExecutionByID(id int64) (*Execution, error)
	SaveExecution(ctx context.Context, execution *Execution) error
	UpdateExecution(ctx context.Context, execution *Execution) error
}

type PostgresExecutionStore struct {
	DB *sql.DB
}

func NewPostgresExecutionStore(db *sql.DB) *PostgresExecutionStore {
	return &PostgresExecutionStore{
		DB: db,
	}
}

func (s *PostgresExecutionStore) SaveExecution(ctx context.Context, execution *Execution) error {
	tx, err := s.DB.BeginTx(ctx, nil)
	if err != nil {
		return err
	}

	defer tx.Rollback()

	query := `
	INSERT INTO executions (execution_id, language, code, status, started_at, question_id, session_id) 
	VALUES ($1, $2, $3, $4, $5, $6, $7)
	`
	//_, err := s.DB.ExecContext(ctx, query,
	//	execution.ExecutionID, execution.Language, execution.Code, execution.Status, execution.StartedAt, execution.QuestionID, execution.SessionID)
	err = tx.QueryRow(query, execution.ExecutionID, execution.Language, execution.Code, execution.Status, execution.StartedAt, execution.QuestionID, execution.SessionID).Scan(&execution.ID)
	if err != nil {
		return err
	}
	return nil
}
