package migrations

import "embed"

// FS contains all migration files embedded at compile time.
// This allows the application to bundle migration files in the binary
// for programmatic migrations when MIGRATION_MODE=auto.
//
//go:embed *.sql
var FS embed.FS
