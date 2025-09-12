package executor

import (
	"context"
	"errors"
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
	"strings"
	"time"
)

type DockerExecutor struct {
	timeout     time.Duration
	memoryLimit string
	cpuLimit    string
	tempDir     string
}

func NewDockerExecutor() *DockerExecutor {
	return &DockerExecutor{
		timeout:     30 * time.Second,
		memoryLimit: "512m",  // Increased memory for faster compilation
		cpuLimit:    "2.0",   // Increased CPU for faster compilation
		tempDir:     "/tmp/code-execution",
	}
}

func (e *DockerExecutor) ExecuteCode(language, code string) (*ExecutionResult, error) {
	switch strings.ToLower(language) {
	case "go":
		return e.executeGo(code)
	case "java":
		return e.executeJava(code)
	default:
		return nil, fmt.Errorf("unsupported language: %s", language)
	}
}

func (e *DockerExecutor) executeGo(code string) (*ExecutionResult, error) {
	return e.executeInDocker("golang:1.24.5-alpine", "go", code, ".go", []string{
		"sh", "-c", "cd /app && go build -o ./code_exec code.go && ./code_exec", //TODO: replace with go run
	})
}

func (e *DockerExecutor) executeJava(code string) (*ExecutionResult, error) {
	// Extract class name from code for proper Java execution
	className := "Solution" // Default class name
	return e.executeInDockerWithCompile("openjdk:17-alpine", "java", code, ".java", []string{
		"sh", "-c", fmt.Sprintf("cd /app && javac %s.java && java %s", className, className),
	})
}

func (e *DockerExecutor) executeInDocker(image, language, code, extension string, command []string) (*ExecutionResult, error) {
	// Create execution directory
	execID := fmt.Sprintf("exec_%d", time.Now().UnixNano())
	execDir := filepath.Join(e.tempDir, execID)

	err := os.MkdirAll(execDir, 0755)
	if err != nil {
		return nil, fmt.Errorf("failed to create execution directory: %w", err)
	}
	defer os.RemoveAll(execDir)

	// Write code to file
	codeFile := filepath.Join(execDir, "code"+extension)
	err = os.WriteFile(codeFile, []byte(code), 0644)
	if err != nil {
		return nil, fmt.Errorf("failed to write code file: %w", err)
	}

	// Prepare Docker command with security constraints
	dockerArgs := []string{
		"run", "--rm",
		"--memory=" + e.memoryLimit,
		"--cpus=" + e.cpuLimit,
		"--network=none",                   // No network access
		"--read-only",                      // Read-only file system
		"--tmpfs", "/tmp:nosuid,size=250m", // Limited temp space
		"--tmpfs", "/.cache:noexec,nosuid,size=100m", // Go build cache
		"--user", "1000:1000", // Run as non-root user
		"--workdir", "/app",
		"-v", execDir + ":/app", // Mount code as read-write
	}

	// Add timeout and command
	dockerArgs = append(dockerArgs, image)
	dockerArgs = append(dockerArgs, "timeout", fmt.Sprintf("%ds", int(e.timeout.Seconds())))
	dockerArgs = append(dockerArgs, command...)

	// Execute with timeout
	ctx, cancel := context.WithTimeout(context.Background(), e.timeout+5*time.Second)
	defer cancel()

	start := time.Now()
	cmd := exec.CommandContext(ctx, "docker", dockerArgs...)
	output, err := cmd.CombinedOutput()
	duration := time.Since(start)

	result := &ExecutionResult{
		Output:        string(output),
		ExecutionTime: int(duration.Milliseconds()),
		Success:       err == nil,
	}

	if err != nil {
		result.ErrorOutput = err.Error()
		if errors.Is(ctx.Err(), context.DeadlineExceeded) {
			result.ErrorOutput = "Execution timeout exceeded"
			result.ExitCode = 124 // Timeout exit code
		}
	}

	return result, nil
}

func (e *DockerExecutor) executeInDockerWithCompile(image, language, code, extension string, command []string) (*ExecutionResult, error) {
	// Similar to executeInDocker but for compiled languages
	return e.executeInDocker(image, language, code, extension, command)
}

type ExecutionResult struct {
	Output        string
	ErrorOutput   string
	ExecutionTime int
	Success       bool
	ExitCode      int
}
