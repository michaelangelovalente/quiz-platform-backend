// Package server is specific for server configuration
package server

import (
	"code-execution-service/internal/routes"
	"fmt"
	"log"
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
	if port == 0 {
		log.Print("environment variable for PORT was not detected, defaulting to 8087")
		port = 8087
	}

	server := &Server{
		port: port,
		app:  application,
	}

	// HTTP Server config
	httpServer := &http.Server{
		Addr:         fmt.Sprintf(":%d", server.port),
		Handler:      routes.SetupRoutes(application),
		IdleTimeout:  time.Minute,
		ReadTimeout:  10 * time.Second,
		WriteTimeout: 30 * time.Second,
	}

	return httpServer
}
