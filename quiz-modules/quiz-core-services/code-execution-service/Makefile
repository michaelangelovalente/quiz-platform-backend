# Simple Makefile for a Go project

# Build the application
all: build test

build:
	@echo "Building..."
	
	
	@go build -o main cmd/api/main.go

# Run the application
run:
	@go run cmd/api/main.go
# Create DB container
docker-run:
	@if docker compose up --build 2>/dev/null; then \
		: ; \
	else \
		echo "Falling back to Docker Compose V1"; \
		docker-compose up --build; \
	fi

# Shutdown DB container
docker-down:
	@if docker compose down 2>/dev/null; then \
		: ; \
	else \
		echo "Falling back to Docker Compose V1"; \
		docker-compose down; \
	fi

# Test the application
test:
	@echo "Testing..."
	@go test ./... -v
# Integrations Tests for the application
itest:
	@echo "Running integration tests..."
	@go test ./internal/database -v

# Clean the binary
clean:
	@echo "Cleaning..."
	@rm -f main

# Live Reload
watch:
	@if command -v air > /dev/null; then \
            air; \
            echo "Watching...";\
        else \
            read -p "Go's 'air' is not installed on your machine. Do you want to install it? [Y/n] " choice; \
            if [ "$$choice" != "n" ] && [ "$$choice" != "N" ]; then \
                go install github.com/air-verse/air@latest; \
                air; \
                echo "Watching...";\
            else \
                echo "You chose not to install air. Exiting..."; \
                exit 1; \
            fi; \
        fi

# Database Migration Commands
migrate-install:
	@echo "Installing goose migration tool..."
	@go install github.com/pressly/goose/v3/cmd/goose@latest

migrate-up:
	@echo "Running database migrations..."
	@chmod +x scripts/migrate.sh
	@./scripts/migrate.sh up

migrate-down:
	@echo "Rolling back last migration..."
	@chmod +x scripts/migrate.sh
	@./scripts/migrate.sh down

migrate-status:
	@echo "Checking migration status..."
	@chmod +x scripts/migrate.sh
	@./scripts/migrate.sh status

migrate-create:
	@if [ "$(name)" = "" ]; then \
		echo "Usage: make migrate-create name=migration_name"; \
		exit 1; \
	fi
	@chmod +x scripts/migrate.sh
	@./scripts/migrate.sh create $(name)

migrate-reset:
	@echo "Resetting database (WARNING: This will drop all data!)"
	@chmod +x scripts/migrate.sh
	@./scripts/migrate.sh reset

# Database setup for development
db-setup: migrate-install migrate-up
	@echo "Database setup completed"

# Full development setup
dev-setup: db-setup
	@echo "Development environment setup completed"

.PHONY: all build run test clean watch docker-run docker-down itest migrate-install migrate-up migrate-down migrate-status migrate-create migrate-reset db-setup dev-setup
