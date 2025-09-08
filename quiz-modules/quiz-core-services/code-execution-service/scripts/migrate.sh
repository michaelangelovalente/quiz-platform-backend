#!/bin/bash

# Migration script for code-execution-service database
# Usage: ./scripts/migrate.sh [up|down|status|create|version]

set -e

# Configuration
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_DATABASE="${DB_DATABASE:-code_execution}"
DB_USERNAME="${DB_USERNAME:-postgres}"
DB_PASSWORD="${DB_PASSWORD:-password}"
DB_SCHEMA="${DB_SCHEMA:-public}"

# Construct connection string
DB_CONNECTION="postgres://${DB_USERNAME}:${DB_PASSWORD}@${DB_HOST}:${DB_PORT}/${DB_DATABASE}?sslmode=disable&search_path=${DB_SCHEMA}"

# Migration directory
MIGRATIONS_DIR="./migrations"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Functions
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if goose is installed
check_goose() {
    if ! command -v goose &> /dev/null; then
        log_error "goose is not installed. Please install it:"
        echo "go install github.com/pressly/goose/v3/cmd/goose@latest"
        exit 1
    fi
}

# Test database connection
test_connection() {
    log_info "Testing database connection..."
    if goose -dir "$MIGRATIONS_DIR" postgres "$DB_CONNECTION" version &> /dev/null; then
        log_success "Database connection successful"
        return 0
    else
        log_error "Failed to connect to database"
        log_info "Connection string: ${DB_CONNECTION//:$DB_PASSWORD@/:***@}"
        return 1
    fi
}

# Main command execution
execute_command() {
    local cmd="$1"
    
    case "$cmd" in
        "up")
            log_info "Running migrations up..."
            goose -dir "$MIGRATIONS_DIR" postgres "$DB_CONNECTION" up
            log_success "Migrations completed successfully"
            ;;
        "down")
            log_warning "Rolling back last migration..."
            read -p "Are you sure you want to rollback? (y/N): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                goose -dir "$MIGRATIONS_DIR" postgres "$DB_CONNECTION" down
                log_success "Rollback completed"
            else
                log_info "Rollback cancelled"
            fi
            ;;
        "status")
            log_info "Checking migration status..."
            goose -dir "$MIGRATIONS_DIR" postgres "$DB_CONNECTION" status
            ;;
        "version")
            log_info "Current database version:"
            goose -dir "$MIGRATIONS_DIR" postgres "$DB_CONNECTION" version
            ;;
        "create")
            if [ -z "$2" ]; then
                log_error "Please provide a migration name: ./migrate.sh create migration_name"
                exit 1
            fi
            log_info "Creating new migration: $2"
            goose -dir "$MIGRATIONS_DIR" create "$2" sql
            log_success "Migration file created"
            ;;
        "reset")
            log_warning "This will drop all tables and re-run all migrations!"
            read -p "Are you sure? This cannot be undone (y/N): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                goose -dir "$MIGRATIONS_DIR" postgres "$DB_CONNECTION" reset
                log_success "Database reset completed"
            else
                log_info "Reset cancelled"
            fi
            ;;
        *)
            echo "Usage: $0 {up|down|status|version|create|reset}"
            echo ""
            echo "Commands:"
            echo "  up      - Run all pending migrations"
            echo "  down    - Rollback the last migration"
            echo "  status  - Show migration status"
            echo "  version - Show current migration version"
            echo "  create  - Create a new migration file"
            echo "  reset   - Rollback all migrations and re-apply"
            echo ""
            echo "Environment variables:"
            echo "  DB_HOST     (default: localhost)"
            echo "  DB_PORT     (default: 5432)"
            echo "  DB_DATABASE (default: code_execution)"
            echo "  DB_USERNAME (default: postgres)"
            echo "  DB_PASSWORD (default: password)"
            echo "  DB_SCHEMA   (default: public)"
            exit 1
            ;;
    esac
}

# Main execution
main() {
    check_goose
    
    if ! test_connection; then
        log_error "Cannot proceed without database connection"
        exit 1
    fi
    
    execute_command "$1" "$2"
}

# Run main function with all arguments
main "$@"