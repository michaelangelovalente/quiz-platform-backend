// server specific configuration
package server

import (
	"fmt"
	"net/http"
	"os"
	"strconv"
	"time"

	_ "github.com/joho/godotenv/autoload"

	"code-execution-service/internal/app"
)

type Server struct {
	port int
	app  *app.Application
}

func NewServer(application *app.Application) *http.Server {
	port, _ := strconv.Atoi(os.Getenv("PORT"))
	server := &Server{
		port: port,
		app:  application,
	}

	// HTTP Server config
	httpServer := &http.Server{
		Addr:         fmt.Sprintf(":%d", server.port),
		Handler:      server.RegisterRoutes(),
		IdleTimeout:  time.Minute,
		ReadTimeout:  10 * time.Second,
		WriteTimeout: 30 * time.Second,
	}

	return httpServer
}
