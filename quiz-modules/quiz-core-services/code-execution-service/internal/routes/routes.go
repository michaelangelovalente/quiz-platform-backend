package routes

import (
	"code-execution-service/internal/app"
	"github.com/go-chi/chi/v5"
)

func SetupRoutes(app *app.Application) *chi.Mux {
	r := chi.NewRouter()
	//r.Use(middleware.Logger)
	//r.Use(cors.Handler(cors.Options{
	//	AllowedOrigins:   []string{"https://*", "http://*"},
	//	AllowedMethods:   []string{"GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"},
	//	AllowedHeaders:   []string{"Accept", "Authorization", "Content-Type"},
	//	AllowCredentials: true,
	//	MaxAge:           300,
	//}))

	// TODO: User
	//r.Post("/users", app.UserHandler.HandleRegisterUser)
	//r.Post("/tokens/authentication", app.TokenHandler.HandleCreateToken)

	// chi grouping utility
	// TODO: add base /api/v1 here
	//r.Group(func(r chi.Router) {
	//	//r.Use(app.Middleware.Authenticate)
	//	// main endpoints....
	//})

	r.Post("/execute", app.ExecutionHandler.Execute)
	return r
}
