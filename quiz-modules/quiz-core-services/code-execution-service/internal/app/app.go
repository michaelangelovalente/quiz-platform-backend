package app

import (
	"code-execution-service/internal/api"
	"code-execution-service/migrations"
	"log"
	"os"

	"code-execution-service/internal/store"
)

type Application struct {
	Logger           *log.Logger
	DB               store.Service
	ExecutionHandler *api.ExecutionHandler
}

func NewApplication() (*Application, error) {
	db := store.New()

	// run migraiton from base of directory
	err := store.MigrateFS(db.GetDB(), migrations.FS, ".")
	if err != nil {
		panic(err) // DB not working --> app shouldn't work
	}
	logger := log.New(os.Stdout, "", log.Ldate|log.Ltime)

	// ---- Store layer ------------
	executionStore := store.NewPostgresExecutionStore(db.GetDB())
	// -------------------------------------

	// ----- Handler layer  ----------
	executionHandler := api.NewExecutionHandler(executionStore, logger)
	// middlewareHandler := middleware.UserMiddleware{UserStore: userStore}
	// -------------------------------------

	app := &Application{
		Logger:           logger,
		DB:               db,
		ExecutionHandler: executionHandler,
		// Middleware:     middlewareHandler,
	}

	return app, nil
}
