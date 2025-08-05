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
| Quiz Service | 8081 | ✅ UP | PostgreSQL (dev) / H2 (test) | Quiz management and administration with full CRUD operations |

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

### Current Routing (Implemented)

| Route Pattern | Target Service | Status | Description |
|---------------|----------------|--------|--------------|
| `/api/v1/quizzes/**` | quiz-service:8081 | ✅ **ACTIVE** | Quiz CRUD operations, question management |

### Planned Routing (Future)

| Route Pattern | Target Service | Status | Description |
|---------------|----------------|--------|--------------|
| `/api/v1/sessions/**` | session-service:8082 | 🔄 **PLANNED** | Real-time test sessions |
| `/api/v1/results/**` | results-service:8083 | 🔄 **PLANNED** | Analytics and reporting |
| `/api/v1/execute/**` | code-execution-service:8084 | 🔄 **PLANNED** | Code execution engine |

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

### Quiz Management Endpoints (Current Implementation)

| Endpoint | Method | Description | Request Body | Response Format |
|----------|--------|-------------|--------------|------------------|
| `/api/v1/quizzes` | POST | Create a new quiz | `QuizRequestDto` | `BaseResponse<QuizResponseDto>` |
| `/api/v1/quizzes` | GET | Retrieve all quizzes (paginated) | None | `BaseListResponse<QuizResponseDto>` |
| `/api/v1/quizzes/{publicId}` | GET | Get quiz by public UUID | None | `BaseResponse<QuizResponseDto>` |
| `/api/v1/quizzes/{publicId}` | PUT | Update existing quiz | `QuizRequestDto` | `BaseResponse<QuizResponseDto>` |
| `/api/v1/quizzes/{publicId}` | DELETE | Delete quiz by public UUID | None | `BaseResponse<GenericResponseDto>` |

### System Endpoints

| Endpoint | Method | Description | Response Format |
|----------|--------|-------------|-----------------|
| `/health` | GET | Custom health check endpoint | JSON |
| `/info` | GET | Custom service information | JSON |

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

## 🧪 Testing Quiz API

### Quiz CRUD Operations

```bash
# Create a new quiz
curl -X POST http://localhost:8081/api/v1/quizzes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Java Basics Quiz",
    "category": "Programming",
    "difficulty": "EASY",
    "description": "Test your Java knowledge",
    "timeLimit": 30,
    "passingScore": 70,
    "status": "DRAFT",
    "questions": [
      {
        "text": "What is Java?",
        "type": "SINGLE_CHOICE",
        "options": ["Programming Language", "Coffee", "Island"],
        "correctAnswer": ["Programming Language"],
        "points": 10
      }
    ]
  }'

# Get all quizzes
curl http://localhost:8081/api/v1/quizzes

# Get quiz by public ID (replace with actual UUID)
curl http://localhost:8081/api/v1/quizzes/{public-uuid}

# Update quiz
curl -X PUT http://localhost:8081/api/v1/quizzes/{public-uuid} \
  -H "Content-Type: application/json" \
  -d '{"title": "Updated Quiz Title", ...}'

# Delete quiz
curl -X DELETE http://localhost:8081/api/v1/quizzes/{public-uuid}
```

### Health Check Commands

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

### API Response Examples

#### Successful Quiz Creation Response
```json
{
  "success": true,
  "message": "QUIZ created successfully",
  "data": {
    "id": 1,
    "publicId": "123e4567-e89b-12d3-a456-426614174000",
    "title": "Java Basics Quiz",
    "category": "Programming",
    "difficulty": "EASY",
    "description": "Test your Java knowledge",
    "timeLimit": 30,
    "passingScore": 70,
    "status": "DRAFT",
    "questions": [...],
    "createdAt": "2025-08-05T10:30:00Z",
    "updatedAt": "2025-08-05T10:30:00Z"
  },
  "timestamp": "2025-08-05T10:30:00.123Z"
}
```

#### Quiz List Response
```json
{
  "data": [
    {
      "id": 1,
      "publicId": "123e4567-e89b-12d3-a456-426614174000", 
      "title": "Java Basics Quiz",
      "difficulty": "EASY",
      "status": "DRAFT"
    }
  ],
  "pageInfo": {
    "page": 0,
    "size": 20,
    "totalPages": 1,
    "totalElements": 1
  },
  "success": true,
  "timestamp": "2025-08-05T10:30:00.123Z"
}
```

### Service Discovery

```bash
# Check available actuator endpoints
curl http://localhost:8080/actuator
curl http://localhost:8081/actuator

# Gateway Service custom endpoints
curl http://localhost:8080/health
curl http://localhost:8080/info

# Quiz Service endpoints
curl http://localhost:8081/health
curl http://localhost:8081/api/v1/quizzes
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

## 📊 API Standards

### Request/Response Patterns

#### Standard Response Wrapper
```java
// All endpoints return BaseResponse<T> or BaseListResponse<T>
public class BaseResponse<T> {
    private boolean success;
    private String message; 
    private T data;
    private String timestamp;
}

public class BaseListResponse<T> {
    private List<T> data;
    private PageInfo pageInfo;
    private boolean success;
    private String timestamp;
}
```

#### Public ID Pattern
- External APIs use **UUID public IDs** instead of internal database IDs
- Example: `/api/v1/quizzes/{publicId}` not `/api/v1/quizzes/{id}`
- Prevents internal ID exposure and provides stable external references

#### Pagination Support
- Built-in pagination with `BasePageableRequest`
- Query parameters: `page`, `size`, `sort`
- Response includes `PageInfo` with total elements and pages

#### Error Handling
- Consistent error codes and HTTP status mapping
- Validation errors with field-specific messages
- Global exception handling via `@ControllerAdvice`

## 📋 Status Summary

- ✅ **Gateway Service**: Healthy, running on port 8080, routing configured
- ✅ **Quiz Service**: Healthy, running on port 8081 with PostgreSQL/H2
- ✅ **Database**: PostgreSQL (dev) / H2 (test) connected and operational
- ✅ **API Endpoints**: Full CRUD operations for quiz management
- ✅ **Documentation**: OpenAPI/Swagger integration
- ✅ **Validation**: Bean validation with Jakarta annotations
- ✅ **Transaction Management**: Proper `@Transactional` boundaries
- ✅ **Lazy Loading Fix**: Resolved LazyInitializationException
- ⚠️ **Service Discovery**: Not initialized (expected in development mode)

---

**Last Updated**: 2025-08-05  
**Services Version**: 0.0.1-SNAPSHOT  
**Java Version**: 21 LTS  
**Spring Boot Version**: 3.2.0  
**Database**: PostgreSQL (dev) / H2 (test)  
**Architecture**: Domain-Driven Design (DDD)