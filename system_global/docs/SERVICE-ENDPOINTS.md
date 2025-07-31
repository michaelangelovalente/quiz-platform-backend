# Service Endpoints Documentation

This document provides comprehensive information about all available endpoints across the Quiz Platform Backend services.

## 📋 Table of Contents

- [Service Overview](#service-overview)
- [Gateway Service Endpoints](#gateway-service-endpoints)
- [Quiz Service Endpoints](#quiz-service-endpoints)
- [Health Monitoring](#health-monitoring)
- [Testing Endpoints](#testing-endpoints)

## 🎯 Service Overview

| Service | Port | Status | Database | Purpose |
|---------|------|--------|----------|---------|
| Gateway Service | 8080 | ✅ UP | N/A | API Gateway, Routing, Cross-cutting concerns |
| Quiz Service | 8081 | ✅ UP | H2 (In-memory) | Quiz management and administration |

## 🚪 Gateway Service Endpoints

**Base URL**: `http://localhost:8080`

### Actuator Endpoints

| Endpoint | Method | Description | Response Format |
|----------|--------|-------------|-----------------|
| `/actuator` | GET | Lists all available actuator endpoints | JSON |
| `/actuator/health` | GET | Service health status and component checks | JSON |
| `/actuator/health/{path}` | GET | Specific health component details | JSON |
| `/actuator/info` | GET | Build information and service metadata | JSON |
| `/actuator/metrics` | GET | Available metrics list | JSON |
| `/actuator/metrics/{metricName}` | GET | Specific metric details | JSON |

### Business Endpoints

| Endpoint | Method | Description | Response Format |
|----------|--------|-------------|-----------------|
| `/health` | GET | Custom health check endpoint | JSON |
| `/info` | GET | Custom service information | JSON |

### Planned Routing (Future)

| Route Pattern | Target Service | Description |
|---------------|----------------|-------------|
| `/api/v1/quizzes/**` | quiz-service | Quiz management operations |
| `/api/v1/sessions/**` | session-service | Real-time test sessions |
| `/api/v1/results/**` | results-service | Analytics and reporting |
| `/api/v1/execute/**` | code-execution-service | Code execution engine |

## 📝 Quiz Service Endpoints

**Base URL**: `http://localhost:8081`

### Actuator Endpoints

| Endpoint | Method | Description | Response Format |
|----------|--------|-------------|-----------------|
| `/actuator` | GET | Lists all available actuator endpoints | JSON |
| `/actuator/health` | GET | Service health status and database checks | JSON |
| `/actuator/health/{path}` | GET | Specific health component details | JSON |
| `/actuator/info` | GET | Build information and service metadata | JSON |
| `/actuator/metrics` | GET | Available metrics list | JSON |
| `/actuator/metrics/{metricName}` | GET | Specific metric details | JSON |

### Business Endpoints

| Endpoint | Method | Description | Response Format |
|----------|--------|-------------|-----------------|
| `/health` | GET | Custom health check endpoint | JSON |
| `/quizzes` | GET | Retrieve all quizzes | JSON Array |

## 🏥 Health Monitoring

### Gateway Service Health Response

```bash
curl http://localhost:8080/actuator/health
```

**Response Structure**:
```json
{
  "status": "UP",
  "components": {
    "discoveryComposite": {
      "description": "Discovery Client not initialized",
      "status": "UNKNOWN",
      "components": {
        "discoveryClient": {
          "description": "Discovery Client not initialized",
          "status": "UNKNOWN"
        }
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 466831618048,
        "free": 195413012480,
        "threshold": 10485760,
        "path": "/home/panda/Desktop/AI-BOT-00-DOJO-QUIZ-PF/.",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    },
    "reactiveDiscoveryClients": {
      "description": "Discovery Client not initialized",
      "status": "UNKNOWN",
      "components": {
        "Simple Reactive Discovery Client": {
          "description": "Discovery Client not initialized",
          "status": "UNKNOWN"
        }
      }
    },
    "refreshScope": {
      "status": "UP"
    }
  }
}
```

### Quiz Service Health Response

```bash
curl http://localhost:8081/actuator/health
```

**Response Structure**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "H2",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 466831618048,
        "free": 195413053440,
        "threshold": 10485760,
        "path": "/home/panda/Desktop/AI-BOT-00-DOJO-QUIZ-PF/.",
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

## 📊 Service Information

### Gateway Service Info

```bash
curl http://localhost:8080/actuator/info
```

**Response**:
```json
{
  "build": {
    "java": {
      "version": "21"
    },
    "spring": {
      "boot": {
        "version": "3.2.0"
      }
    },
    "version": "0.0.1-SNAPSHOT",
    "artifact": "gateway-service",
    "name": "gateway-service",
    "time": "2025-07-05T09:04:43.252Z",
    "group": "com.quizplatform"
  }
}
```

### Quiz Service Info

```bash
curl http://localhost:8081/actuator/info
```

**Response**:
```json
{
  "build": {
    "java": {
      "version": "21"
    },
    "spring": {
      "boot": {
        "version": "3.2.0"
      }
    },
    "version": "0.0.1-SNAPSHOT",
    "artifact": "quiz-service",
    "name": "quiz-service",
    "time": "2025-07-05T09:05:19.421Z",
    "group": "com.quizplatform"
  }
}
```

## 🧪 Testing Endpoints

### Quick Health Check Commands

```bash
# Check all services are running
curl -s http://localhost:8080/actuator/health | jq '.status'
curl -s http://localhost:8081/actuator/health | jq '.status'

# Check database connectivity (Quiz Service)
curl -s http://localhost:8081/actuator/health | jq '.components.db.status'

# Get build information
curl -s http://localhost:8080/actuator/info | jq '.build.version'
curl -s http://localhost:8081/actuator/info | jq '.build.version'

# List all available actuator endpoints
curl -s http://localhost:8080/actuator | jq '._links | keys'
curl -s http://localhost:8081/actuator | jq '._links | keys'
```

### Service Discovery

```bash
# Check available actuator endpoints
curl http://localhost:8080/actuator
curl http://localhost:8081/actuator

# Gateway Service custom endpoints
curl http://localhost:8080/health
curl http://localhost:8080/info

# Quiz Service custom endpoints  
curl http://localhost:8081/health
curl http://localhost:8081/quizzes
```

## 🔍 Health Component Details

### Gateway Service Components

- **discoveryComposite**: Spring Cloud service discovery (not initialized - expected in development)
- **diskSpace**: File system disk space monitoring
- **ping**: Basic connectivity check
- **reactiveDiscoveryClients**: Reactive service discovery clients
- **refreshScope**: Spring Cloud configuration refresh capability

### Quiz Service Components

- **db**: H2 database connectivity and health
- **diskSpace**: File system disk space monitoring  
- **ping**: Basic connectivity check

## 🚀 Starting Services

```bash
# Start Gateway Service
./gradlew :quiz-modules:gateway-service:bootRun

# Start Quiz Service (different terminal)
./gradlew :quiz-modules:quiz-core-services:quiz-service:bootRun

# With specific profiles
./gradlew :quiz-modules:gateway-service:bootRun --args="--spring.profiles.active=local"
./gradlew :quiz-modules:quiz-core-services:quiz-service:bootRun --args="--spring.profiles.active=local"
```

## 📋 Status Summary

- ✅ **Gateway Service**: Healthy, running on port 8080
- ✅ **Quiz Service**: Healthy, running on port 8081 with H2 database
- ✅ **Database**: H2 in-memory database connected and operational
- ✅ **Disk Space**: Sufficient free space available
- ⚠️ **Service Discovery**: Not initialized (expected in development mode)

---

**Last Updated**: 2025-07-05  
**Services Version**: 0.0.1-SNAPSHOT  
**Java Version**: 21  
**Spring Boot Version**: 3.2.0