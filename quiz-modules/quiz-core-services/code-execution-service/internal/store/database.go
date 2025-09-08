package store

import (
	"context"
	"database/sql"
	"fmt"
	"github.com/pressly/goose/v3"
	"io/fs"
	"log"
	"os"
	"strconv"
	"time"

	_ "github.com/jackc/pgx/v5/stdlib"
	_ "github.com/joho/godotenv/autoload"
)

// Service represents a DBService that interacts with a database.
type Service interface {
	// Health returns a map of health status information.
	// The keys and values in the map are DBService-specific.
	Health() map[string]string

	// Close terminates the database connection.
	// It returns an error if the connection cannot be closed.
	Close() error

	// GetDB returns the underlying *sql.DB for advanced operations like migrations.
	GetDB() *sql.DB
}

type DBService struct {
	DB *sql.DB
}

var (
	database   = os.Getenv("DB_DATABASE")
	password   = os.Getenv("DB_PASSWORD")
	username   = os.Getenv("DB_USERNAME")
	port       = os.Getenv("DB_PORT")
	host       = os.Getenv("DB_HOST")
	schema     = os.Getenv("DB_SCHEMA")
	dbInstance *DBService
)

func New() Service {
	// Reuse Connection
	if dbInstance != nil {
		return dbInstance
	}
	connStr := fmt.Sprintf("postgres://%s:%s@%s:%s/%s?sslmode=disable&search_path=%s", username, password, host, port, database, schema)
	db, err := sql.Open("pgx", connStr)
	if err != nil {
		// log.Fatal(err)
		panic(err)
	}

	dbInstance = &DBService{
		DB: db,
	}
	return dbInstance
}

// Health checks the health of the database connection by pinging the database.
// It returns a map with keys indicating various health statistics.
func (s *DBService) Health() map[string]string {
	ctx, cancel := context.WithTimeout(context.Background(), 1*time.Second)
	defer cancel()

	stats := make(map[string]string)

	// Ping the database
	err := s.DB.PingContext(ctx)
	if err != nil {
		stats["status"] = "down"
		stats["error"] = fmt.Sprintf("DB down: %v", err)
		log.Fatalf("DB down: %v", err) // Log the error and terminate the program
		return stats
	}

	// Database is up, add more statistics
	stats["status"] = "up"
	stats["message"] = "It's healthy"

	// Get database stats (like open connections, in use, idle, etc.)
	dbStats := s.DB.Stats()
	stats["open_connections"] = strconv.Itoa(dbStats.OpenConnections)
	stats["in_use"] = strconv.Itoa(dbStats.InUse)
	stats["idle"] = strconv.Itoa(dbStats.Idle)
	stats["wait_count"] = strconv.FormatInt(dbStats.WaitCount, 10)
	stats["wait_duration"] = dbStats.WaitDuration.String()
	stats["max_idle_closed"] = strconv.FormatInt(dbStats.MaxIdleClosed, 10)
	stats["max_lifetime_closed"] = strconv.FormatInt(dbStats.MaxLifetimeClosed, 10)

	// Evaluate stats to provide a health message
	if dbStats.OpenConnections > 40 { // Assuming 50 is the max for this example
		stats["message"] = "The database is experiencing heavy load."
	}

	if dbStats.WaitCount > 1000 {
		stats["message"] = "The database has a high number of wait events, indicating potential bottlenecks."
	}

	if dbStats.MaxIdleClosed > int64(dbStats.OpenConnections)/2 {
		stats["message"] = "Many idle connections are being closed, consider revising the connection pool settings."
	}

	if dbStats.MaxLifetimeClosed > int64(dbStats.OpenConnections)/2 {
		stats["message"] = "Many connections are being closed due to max lifetime, consider increasing max lifetime or revising the connection usage pattern."
	}

	return stats
}

// Close closes the database connection.
// It logs a message indicating the disconnection from the specific database.
// If the connection is successfully closed, it returns nil.
// If an error occurs while closing the connection, it returns the error.
func (s *DBService) Close() error {
	log.Printf("Disconnected from database: %s", database)
	return s.DB.Close()
}

// GetDB returns the underlying *sql.DB for advanced operations like migrations.
func (s *DBService) GetDB() *sql.DB {
	return s.DB
}

// ----  GOOSE MIGRATION UTILITIES  ----
// Provides both CLI and programmatic migrations  (Functions are for programmatic goose usage, might be removed for security issues)

// MigrateFS runs database migrations from embedded filesystem.
// Use this for bundling migration files in the application binary.
func MigrateFS(db *sql.DB, migrationFS fs.FS, dir string) error {
	goose.SetBaseFS(migrationFS)
	defer func() {
		goose.SetBaseFS(nil)
	}()
	return Migrate(db, dir)
}

// Migrate runs all pending migrations in the specified directory.
// Sets PostgreSQL dialect and executes goose.Up().
func Migrate(db *sql.DB, dir string) error {
	err := goose.SetDialect("postgres")
	if err != nil {
		return fmt.Errorf("failed to set goose dialect: %w", err)
	}

	err = goose.Up(db, dir)
	if err != nil {
		return fmt.Errorf("failed to execute migrations: %w", err)
	}

	return nil
}

// MigrationStatus checks if migrations are needed without running them.
// Returns true if migrations are pending, false if up-to-date.
func MigrationStatus(db *sql.DB, dir string) (bool, error) {
	err := goose.SetDialect("postgres")
	if err != nil {
		return false, fmt.Errorf("failed to set dialect: %w", err)
	}

	currentVersion, err := goose.GetDBVersion(db)
	if err != nil {
		return false, fmt.Errorf("failed to get database version: %w", err)
	}

	// Version 0 means no migrations applied yet
	return currentVersion == 0, nil
}
