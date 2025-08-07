package app

import (
	"fmt"
	"log"
	"net/http"
	"os"

	"code-execution-service/internal/database"
)

type Application struct {
	Logger *log.Logger
	DB     database.Service
	// Handlers....
	// Middleware     middleware.UserMiddleware
}

func NewApplication() (*Application, error) {
	db := database.New()

	// run migraiton from base of directory
	// err = store.MigrateFS(pgDB, migrations.FS, ".")
	// if err != nil {
	// 	panic(err) // DB not working --> app shouldn't work
	// }
	logger := log.New(os.Stdout, "", log.Ldate|log.Ltime)

	// ---- Store layer ------------
	// -------------------------------------
	// ----- Handler layer  ----------
	// middlewareHandler := middleware.UserMiddleware{UserStore: userStore}
	// -------------------------------------

	app := &Application{
		Logger: logger,
		DB:     db,
		// Middleware:     middlewareHandler,
	}

	return app, nil
}

// HealthCheck function
// handler <- HTTP HANDLER
// method receiver on *Application
func (app *Application) HealthCheck(w http.ResponseWriter, r *http.Request) {
	fmt.Fprint(w, "Status is available\n") // Fprint specifically used to pass data to cleint
}
