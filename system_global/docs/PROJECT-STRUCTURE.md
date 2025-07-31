# Quiz Platform Backend - Project Structure & Best Practices

This document provides comprehensive guidance on the project structure, architectural decisions, and development best practices for the Quiz Platform Backend.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Project Structure](#project-structure)
- [Clean Architecture Implementation](#clean-architecture-implementation)
- [Modern Java 21 Best Practices](#modern-java-21-best-practices)
- [Domain-Driven Design Patterns](#domain-driven-design-patterns)
- [Microservices Organization](#microservices-organization)
- [Development Workflow](#development-workflow)
- [Testing Strategy](#testing-strategy)
- [Documentation References](#documentation-references)

## 🎯 Project Overview

**Quiz Platform Backend** is an educational quiz platform with real-time code execution capabilities built following modern software engineering principles.


### Key Characteristics

| Aspect | Details                                       |
|--------|-----------------------------------------------|
| **Architecture** | Microservices with Clean Architecture         |
| **Primary Languages** | Java 21 (LTS) and Go 1.24+                    |
| **Development Phase** | -                                             |
| **Build System** | Multi-module Gradle with buildSrc conventions |

## 📁 Project Structure

### Root Directory Organization

```
quiz-platform-backend/
├── buildSrc/                       # ⚠️ Gradle build conventions (must stay at root)
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       ├── aop-conventions.gradle.kts
│       ├── boot-jar-conventions.gradle.kts
│       ├── java-service-conventions.gradle.kts
│       ├── spring-boot-conventions.gradle.kts
│       └── test-conventions.gradle.kts
├── quiz-modules/                   # 🎯 All service modules
│   ├── gateway-service/            # API Gateway (Port 8080)
│   └── quiz-core-services/         # Core business services
│       ├── common-lib/             # Shared utilities and DTOs
│       └── quiz-service/           # Quiz management (Port 8081)
└── system/                         # 🔧 System-level organization
    ├── config/                     # Configuration files
    │   └── gradle.properties       # Gradle configuration copy
    ├── docs/                       # 📚 Project documentation
    │   ├── Quiz-Archi-V1.md        # Architecture documentation
    │   ├── Quiz-Archi-V1.png       # Architecture diagram
    │   ├── SERVICE-ENDPOINTS.md    # Service endpoints reference
    │   └── PROJECT-STRUCTURE.md    # This document
    ├── infrastructure/             # 🏗️ Future infrastructure as code
    │   ├── docker/                 # Docker configurations (future)
    │   ├── k8s/                    # Kubernetes manifests (future)
    │   └── terraform/              # Terraform scripts (future)
    ├── scripts/                    # 📜 Build and deployment scripts
    │   ├── build/                  # Build automation scripts
    │   ├── deployment/             # Deployment scripts
    │   └── db-migration/           # Database migration scripts
    └── tools/                      # 🛠️ Development tools
        └── dev-utils/              # Development utilities
            └── database/           # Database utilities and schemas
```

### Service Module Structure

Each service follows Clean Architecture principles:

```
quiz-service/
├── build.gradle.kts                # Service-specific build configuration
└── src/
    ├── main/
    │   ├── java/com/quizplatform/quiz/
    │   │   ├── QuizServiceApplication.java     # Spring Boot main class
    │   │   └── business/                       # Business logic layer
    │   │       ├── controller/                 # 🌐 Adapter Layer (REST endpoints)
    │   │       │   └── QuizController.java
    │   │       ├── domain/                     # 💎 Domain Layer (core business)
    │   │       │   ├── dto/                    # Data Transfer Objects
    │   │       │   │   ├── Question.java
    │   │       │   │   └── Quiz.java
    │   │       │   ├── entity/                 # Domain Entities
    │   │       │   │   ├── QuestionEntity.java
    │   │       │   │   └── QuizEntity.java
    │   │       │   └── enums/                  # Domain Enumerations
    │   │       │       └── QuestionTypeEnum.java
    │   │       └── service/                    # 🔧 Application Layer (use cases)
    │   │           └── QuizService.java
    │   └── resources/
    │       ├── application.yml                 # Service configuration
    │       └── sql-scripts/                    # Database scripts
    │           ├── initial-ddl.sql
    │           └── initial-dml.sql
    └── test/                                   # 🧪 Test structure mirrors main
        └── java/com/quizplatform/quiz/
            └── business/service/
                └── QuizServiceTest.java
```

## 🏗️ Clean Architecture Implementation

### Architecture Layers (Inside-Out)

The project implements Clean Architecture with four distinct layers:

```
┌─────────────────────────────────────────────────────────┐
│                    Adapter Layer                        │ ← Controllers, REST endpoints, DTOs
│  ┌───────────────────────────────────────────────────┐  │
│  │              Infrastructure Layer                 │  │ ← Framework implementations, DB, messaging
│  │  ┌─────────────────────────────────────────────┐  │  │
│  │  │            Application Layer                │  │  │ ← Use cases, orchestrates domain logic
│  │  │  ┌───────────────────────────────────────┐  │  │  │
│  │  │  │           Domain Layer               │  │  │  │ ← Pure business logic, no dependencies
│  │  │  │                                     │  │  │  │
│  │  │  │  • Entities                        │  │  │  │
│  │  │  │  • Value Objects                   │  │  │  │
│  │  │  │  • Domain Services                 │  │  │  │
│  │  │  │  • Business Rules                  │  │  │  │
│  │  │  └───────────────────────────────────────┘  │  │  │
│  │  └─────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────┐  │
└─────────────────────────────────────────────────────────┘
```

#### 1. Domain Layer (Innermost) 💎

**Location**: `src/main/java/com/quizplatform/quiz/business/domain/`

**Purpose**: Pure business logic with no framework dependencies

**Components**:
- **Entities**: Core business objects (`QuizEntity`, `QuestionEntity`)
- **Value Objects**: Immutable data structures (`QuizMetadata`)
- **Enums**: Domain-specific enumerations (`QuestionTypeEnum`)
- **Domain Services**: Complex business logic that doesn't belong to a single entity

**Best Practices**:
```java
// ✅ GOOD: Pure domain entity with business logic
@Entity
public class QuizEntity {
    // Business method in domain layer
    public void publish() {
        if (status != QuizStatus.DRAFT) {
            throw new InvalidQuizStateException("Only draft quizzes can be published");
        }
        if (questions.isEmpty()) {
            throw new InvalidQuizStateException("Cannot publish quiz without questions");
        }
        this.status = QuizStatus.PUBLISHED;
        registerEvent(new QuizPublishedEvent(id, Instant.now()));
    }
}

// ✅ GOOD: Value object with validation
@Embeddable
public record QuizMetadata(
    String title,
    String description,
    Duration timeLimit,
    Integer passingScore,
    Instant createdAt,
    String createdBy
) {
    public QuizMetadata {
        Objects.requireNonNull(title, "Title is required");
        if (timeLimit.toMinutes() < 1 || timeLimit.toMinutes() > 180) {
            throw new IllegalArgumentException("Time limit must be between 1 and 180 minutes");
        }
    }
}
```

#### 2. Application Layer (Use Cases) 🔧

**Location**: `src/main/java/com/quizplatform/quiz/business/service/`

**Purpose**: Orchestrates domain logic and coordinates with infrastructure

**Components**:
- **Service Classes**: Use case implementations (`QuizService`)
- **Command/Query Objects**: Request/response models
- **Event Handlers**: Domain event processing

**Best Practices**:
```java
// ✅ GOOD: Application service orchestrating domain logic
@Service
@Transactional
public class QuizService {
    private final QuizRepository quizRepository;
    private final QuizEventPublisher eventPublisher;

    public Quiz createQuiz(CreateQuizCommand command) {
        var quiz = Quiz.builder()
            .title(command.title())
            .description(command.description())
            .build();
            
        var savedQuiz = quizRepository.save(quiz);
        eventPublisher.publish(new QuizCreatedEvent(savedQuiz.getId()));
        
        return savedQuiz;
    }
}
```

#### 3. Infrastructure Layer 🏗️

**Location**: Framework-specific implementations (repositories, configurations)

**Purpose**: Framework implementations, database access, external services

**Components**:
- **Repository Implementations**: JPA repositories
- **Configuration Classes**: Spring configuration
- **External Service Clients**: Third-party integrations

#### 4. Adapter Layer (Interface) 🌐

**Location**: `src/main/java/com/quizplatform/quiz/business/controller/`

**Purpose**: External interfaces (REST controllers, message handlers)

**Components**:
- **REST Controllers**: HTTP endpoint handlers (`QuizController`)
- **DTOs**: Data transfer objects for external communication
- **Exception Handlers**: Global error handling

**Best Practices**:
```java
// ✅ GOOD: Controller as thin adapter
@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(
            @Valid @RequestBody QuizRequest request) {
        var command = CreateQuizCommand.from(request);
        var quiz = quizService.createQuiz(command);
        return ResponseEntity.ok(QuizResponse.from(quiz));
    }
}
```

## ☕ Modern Java 21 Best Practices

### 1. Virtual Threads for Concurrency

```java
// ✅ Use virtual threads for I/O-bound operations
@Service
public class QuizAnalyticsService {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    
    public CompletableFuture<QuizStatistics> analyzeQuizAsync(String quizId) {
        return CompletableFuture.supplyAsync(() -> {
            return calculateStatistics(quizId);
        }, executor);
    }
}
```

### 2. Records for Immutable DTOs

```java
// ✅ Records for data transfer objects
public record QuizRequest(
    @NotBlank String title,
    @Size(max = 1000) String description,
    @Min(1) @Max(180) Integer timeLimit,
    @Valid List<QuestionRequest> questions
) {
    // Compact constructor for validation
    public QuizRequest {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Quiz must have at least one question");
        }
    }
}
```

### 3. Sealed Classes for Domain Modeling

```java
// ✅ Sealed interfaces for type safety
public sealed interface QuizCommand 
    permits CreateQuizCommand, UpdateQuizCommand, PublishQuizCommand {
    String quizId();
}

public record CreateQuizCommand(String quizId, QuizData data) implements QuizCommand {}
public record UpdateQuizCommand(String quizId, QuizData data) implements QuizCommand {}
public record PublishQuizCommand(String quizId, Instant publishAt) implements QuizCommand {}
```

### 4. Pattern Matching and Switch Expressions

```java
// ✅ Enhanced switch with pattern matching
public String processQuizEvent(QuizEvent event) {
    return switch (event) {
        case QuizCreated(var id, var title) -> "Quiz created: " + title;
        case QuizPublished(var id, var publishedAt) -> "Quiz published at: " + publishedAt;
        case QuizArchived(var id, var reason) -> "Quiz archived: " + reason;
        default -> "Unknown event";
    };
}
```

### 5. Functional Programming Patterns

```java
// ✅ Functional composition with streams
public QuizStatistics analyzeQuizPerformance(String quizId) {
    return sessionRepository.findByQuizId(quizId).stream()
        .filter(Session::isCompleted)
        .map(this::calculateScore)
        .collect(Collectors.teeing(
            Collectors.averagingDouble(Score::getValue),
            Collectors.counting(),
            (avgScore, count) -> new QuizStatistics(quizId, avgScore, count)
        ));
}
```

## 🎭 Domain-Driven Design Patterns

### Aggregate Root Pattern

```java
// ✅ Aggregate root with domain events
@Entity
@Table(name = "quizzes")
public class Quiz extends AggregateRoot {
    @Id
    private String id;
    
    @Embedded
    private QuizMetadata metadata;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "quiz_id")
    private List<Question> questions = new ArrayList<>();
    
    // Domain methods enforce business rules
    public void addQuestion(Question question) {
        if (questions.size() >= MAX_QUESTIONS_PER_QUIZ) {
            throw new MaxQuestionsExceededException();
        }
        questions.add(question);
        question.setQuiz(this);
        registerEvent(new QuestionAddedEvent(id, question.getId()));
    }
}
```

### Repository Pattern

```java
// ✅ Domain-focused repository interface
public interface QuizRepository extends JpaRepository<Quiz, String> {
    
    @Query("SELECT q FROM Quiz q WHERE q.status = :status")
    Page<Quiz> findByStatus(@Param("status") QuizStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"questions", "questions.options"})
    Optional<Quiz> findWithQuestionsById(String id);
    
    @Query("SELECT new com.quizplatform.dto.QuizSummary(q.id, q.title, q.status) " +
           "FROM Quiz q WHERE q.createdBy = :userId")
    List<QuizSummary> findSummariesByUser(@Param("userId") String userId);
}
```

### Domain Events

```java
// ✅ Domain events for loose coupling
public sealed interface QuizEvent extends DomainEvent {
    String quizId();
    Instant occurredAt();
}

public record QuizCreatedEvent(
    String quizId, 
    String title, 
    String createdBy, 
    Instant occurredAt
) implements QuizEvent {}

// Event publisher with transaction support
@Component
@RequiredArgsConstructor
public class QuizEventPublisher {
    private final ApplicationEventPublisher publisher;
    
    @Transactional(propagation = Propagation.MANDATORY)
    public void publish(QuizEvent event) {
        publisher.publishEvent(event);
    }
}
```

## 🔧 Microservices Organization

### Current Services

| Service | Port | Responsibility | Technology Stack |
|---------|------|----------------|------------------|
| **Gateway Service** | 8080 | API Gateway, routing, cross-cutting concerns | Spring Cloud Gateway (reactive) |
| **Quiz Service** | 8081 | Quiz management and administration | Spring Boot + JPA + H2 |

### Future Services (Planned)

| Service | Port | Responsibility | Technology Stack |
|---------|------|----------------|------------------|
| **Session Service** | 8082 | Real-time test session management | Spring Boot + WebSocket + Redis |
| **Results Service** | 8083 | Analytics and reporting | Spring Boot + JPA + PostgreSQL |
| **Code Execution Service** | 8084 | Secure code execution engine | Go 1.24+ + Docker + Kubernetes |

### Service Communication Patterns

```java
// ✅ Event-driven communication between services
@EventListener
@Async
public void handleQuizPublished(QuizPublishedEvent event) {
    // Notify session service that quiz is available
    sessionService.enableQuizForSessions(event.quizId());
    
    // Update analytics
    analyticsService.trackQuizPublication(event);
}
```

## 🔄 Development Workflow

### 1. Planning Phase (MUST FOLLOW)

Before writing any code:
1. **Create TODO list** using structured planning
2. **Describe plan of action** and reasoning
3. **Base all modifications** on this plan
4. **Update TODO status** as tasks complete

### 2. Code Development

```java
// ✅ Follow Clean Code principles
public class QuizService {
    
    // Clear, expressive method names
    public Optional<Quiz> findActiveQuizById(String quizId) {
        return quizRepository.findById(quizId)
            .filter(Quiz::isActive)
            .map(this::enrichWithStatistics);
    }
    
    // Single responsibility methods
    private Quiz enrichWithStatistics(Quiz quiz) {
        var statistics = statisticsService.calculateFor(quiz.getId());
        quiz.setStatistics(statistics);
        return quiz;
    }
}
```

### 3. Testing Strategy

#### Unit Testing with JUnit 5

```java
// ✅ Comprehensive unit test example
@ExtendWith(MockitoExtension.class)
class QuizServiceTest {
    @Mock private QuizRepository repository;
    @Mock private EventPublisher eventPublisher;
    @InjectMocks private QuizService service;
    
    @Test
    @DisplayName("Should create quiz and publish event when valid request provided")
    void createQuiz_ValidRequest_CreatesQuizAndPublishesEvent() {
        // Given
        var request = new CreateQuizRequest("Java Basics", 30);
        var savedQuiz = Quiz.builder().id("quiz-1").title("Java Basics").build();
        
        when(repository.save(any(Quiz.class))).thenReturn(savedQuiz);
        
        // When
        var result = service.createQuiz(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Java Basics");
        verify(eventPublisher).publish(any(QuizCreatedEvent.class));
    }
}
```

#### Integration Testing with TestContainers

```java
// ✅ Integration test with real database
@SpringBootTest
@Testcontainers
class QuizIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Test
    void shouldPersistQuizWithQuestions() {
        // Integration test implementation
    }
}
```

### 4. AOP for Cross-Cutting Concerns

```java
// ✅ Performance monitoring aspect
@Aspect
@Component
@Slf4j
public class PerformanceMonitoringAspect {
    private final MeterRegistry meterRegistry;
    
    @Around("@annotation(monitored)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint, Monitored monitored) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Object result = joinPoint.proceed();
            sample.stop(Timer.builder("method.execution")
                .tag("method", methodName)
                .tag("status", "success")
                .register(meterRegistry));
            return result;
        } catch (Exception e) {
            sample.stop(Timer.builder("method.execution")
                .tag("method", methodName)
                .tag("status", "error")
                .tag("exception", e.getClass().getSimpleName())
                .register(meterRegistry));
            throw e;
        }
    }
}
```

## 🔨 Build System & Conventions

### Gradle BuildSrc Conventions

The project uses buildSrc for shared build logic:

- **`java-service-conventions.gradle.kts`**: Base Java service configuration
- **`spring-boot-conventions.gradle.kts`**: Spring Boot specific settings
- **`test-conventions.gradle.kts`**: Testing configuration and setup
- **`aop-conventions.gradle.kts`**: AspectJ and AOP configuration

### Essential Build Commands

```bash
# Build and test
./gradlew build          # Build all modules
./gradlew test           # Run all tests
./gradlew buildAll       # Build all subprojects
./gradlew cleanAll       # Clean all subprojects

# Run services
./gradlew :quiz-modules:gateway-service:bootRun
./gradlew :quiz-modules:quiz-core-services:quiz-service:bootRun

# With profiles
./gradlew :quiz-modules:gateway-service:bootRun --args="--spring.profiles.active=local"
```

## 📚 Documentation References

### Internal Documentation

- **[SERVICE-ENDPOINTS.md](./SERVICE-ENDPOINTS.md)** - Complete service endpoints reference
- **[Quiz-Archi-V1.md](./Quiz-Archi-V1.md)** - Detailed architecture documentation  
- **[Quiz-Archi-V1.png](./Quiz-Archi-V1.png)** - Architecture diagram


## 🎯 Development Best Practices Summary

### Code Quality Principles

1. **Clarity over Cleverness**: Write code that is easy to understand
2. **Single Responsibility**: Each class/method should have one reason to change
3. **Dependency Inversion**: Depend on abstractions, not concretions
4. **Immutability**: Prefer immutable objects (records, final fields)
5. **Fail Fast**: Validate inputs early and throw meaningful exceptions

### Testing Principles

1. **Test Pyramid**: Many unit tests, some integration tests, few E2E tests
2. **Arrange-Act-Assert**: Clear test structure
3. **Descriptive Names**: Test names should describe the scenario
4. **Test Behavior**: Test what the code does, not how it does it

### Performance Considerations

1. **Virtual Threads**: Use for I/O-bound operations
2. **Caching**: Cache expensive operations with Spring Cache
3. **Database Optimization**: Use @EntityGraph for eager loading
4. **Lazy Loading**: Default to lazy loading for associations

---

**Last Updated**: 2025-07-05  
**Architecture Version**: Clean Architecture with DDD  
**Java Version**: 21 
**Spring Boot Version**: 3.2.0  
