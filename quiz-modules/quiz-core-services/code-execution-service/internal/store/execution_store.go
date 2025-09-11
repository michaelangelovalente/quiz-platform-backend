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
	UserID        *int64     `DB:"user_id"`
	SessionID     *string    `db:"session_id"`
	CreatedAt     time.Time  `db:"created_at"`
	UpdatedAt     time.Time  `db:"updated_at"`
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
	SaveExecution(execution *Execution) error
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

func (s *PostgresExecutionStore) SaveExecution(execution *Execution) error {
	query := `
    INSERT INTO executions (
        execution_id, language, code, status, started_at, 
        completed_at, execution_time_ms, output, error_output, 
        exit_code, success, question_id, user_id, session_id, 
        created_at, updated_at
    ) 
    VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16)
    RETURNING id
    `

	err := s.DB.QueryRow(query,
		execution.ExecutionID,
		execution.Language,
		execution.Code,
		execution.Status,
		execution.StartedAt,
		execution.CompletedAt,
		execution.ExecutionTime,
		execution.Output,
		execution.ErrorOutput,
		execution.ExitCode,
		execution.Success,
		execution.QuestionID,
		execution.UserID,
		execution.SessionID,
		execution.CreatedAt,
		execution.UpdatedAt,
	).Scan(&execution.ID)

	if err != nil {
		return err
	}
	return nil
}

func (s *PostgresExecutionStore) GetExecutionByID(id int64) (*Execution, error) {
	execution := &Execution{}
	query := `
	SELECT execution_id, language, code, status, started_at, completed_at, execution_time_ms, output, error_output, exit_code, success, question_id, session_id, created_at, updated_at
	FROM executions
	WHERE execution_id = $1
	`
	err := s.DB.QueryRow(query, id).Scan(
		&execution.ID,
		&execution.ExecutionID,
		&execution.Language,
		&execution.Code,
		&execution.Status,
		&execution.StartedAt,
		&execution.CompletedAt,
		&execution.ExecutionTime,
		&execution.Output,
		&execution.ErrorOutput,
		&execution.ExitCode,
		&execution.Success,
		&execution.QuestionID,
		&execution.SessionID,
		&execution.CreatedAt,
		&execution.UpdatedAt,
	)

	if err == sql.ErrNoRows {
		return nil, nil
	}

	if err != nil {
		return nil, err
	}

	return execution, nil
}

// TODO needs to be fixed
func (s *PostgresExecutionStore) UpdateExecution(ctx context.Context, execution *Execution) error {
	tx, err := s.DB.BeginTx(ctx, nil)

	if err != nil {
		return err
	}
	defer tx.Rollback()
	query := `
	UPDATE executions
	SET status = $1, completed_at = $2, execution_time_ms = $3, output = $4, error_output = $5, exit_code = $6, success = $7
	WHERE id = $8
	`
	res, err := tx.ExecContext(ctx, query, execution.Status, execution.CompletedAt, execution.ExecutionTime, execution.Output, execution.ErrorOutput, execution.ExitCode, execution.Success, execution.ID)
	if err != nil {
		return err
	}

	rowsAffected, err := res.RowsAffected()
	if err != nil {
		return err
	}

	if rowsAffected == 0 {
		return sql.ErrNoRows
	}

	err = tx.Commit()
	if err != nil {
		return err
	}

	return nil
}
