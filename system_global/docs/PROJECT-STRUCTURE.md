# Quiz Platform Backend - Project Structure & Best Practices

This document provides comprehensive guidance on the project structure, architectural decisions, and development best practices for the Quiz Platform Backend.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Project Structure](#project-structure)
- [Architecture Analysis](#architecture-analysis)
- [Current Implementation](#current-implementation)
- [Domain-Driven Design Implementation](#domain-driven-design-implementation)
- [Modern Java 21 Best Practices](#modern-java-21-best-practices)
- [Microservices Organization](#microservices-organization)
- [Development Workflow](#development-workflow)
- [Testing Strategy](#testing-strategy)
- [Documentation References](#documentation-references)

## 🎯 Project Overview

**Quiz Platform Backend** is an educational quiz platform built following **Domain-Driven Design (DDD)** principles with modern Java 21 features and Spring Boot 3.2.0.

### Key Characteristics

| Aspect | Details                                       |
|--------|-----------------------------------------------|
| **Architecture** | **Domain-Driven Design** with microservices pattern |
| **Primary Languages** | Java 21 (LTS), Go 1.24+ (future)                    |
| **Development Phase** | Core quiz management implemented, session management planned |
| **Build System** | Multi-module Gradle with buildSrc conventions |
| **Current Status** | ✅ Quiz Service operational, Gateway configured |
| **Database** | PostgreSQL (dev), H2 (test) with JPA/Hibernate |

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
└── system_global/                  # 🔧 System-level organization
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

## 🏗️ Architecture Analysis

### Current Architecture: Domain-Driven Design (DDD)

The project **follows Domain-Driven Design principles** rather than strict Clean Architecture. Here's the actual implementation:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│                 (Controllers & DTOs)                        │
│  ┌───────────────────────────────────────────────────────┐  │
│  │                APPLICATION LAYER                      │  │
│  │              (Services & Use Cases)                   │  │
│  │  ┌─────────────────────────────────────────────────┐  │  │
│  │  │                DOMAIN LAYER                     │  │  │
│  │  │           (Entities & Business Logic)           │  │  │
│  │  │                                                 │  │  │
│  │  │  • QuizEntity (Aggregate Root)                 │  │  │
│  │  │  • QuestionEntity                              │  │  │
│  │  │  • Value Objects (Enums)                       │  │  │
│  │  │  • Business Rules & Validation                 │  │  │
│  │  │  • Repository Interfaces                       │  │  │
│  │  └─────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│                INFRASTRUCTURE LAYER                         │
│              (JPA, Database, Configuration)                 │
└─────────────────────────────────────────────────────────────┘
```

#### 1. Domain Layer (Core Business Logic) 💎

**Location**: `src/main/java/com/quizplatform/quiz/business/domain/`

**Purpose**: Rich domain model with business behavior and rules

**Current Implementation**:
- **Aggregate Root**: `QuizEntity` with embedded business logic
- **Entity**: `QuestionEntity` as part of Quiz aggregate
- **Value Objects**: `QuizDifficultyEnum`, `QuestionTypeEnum`
- **Repository Interfaces**: Domain-driven repository contracts

**Current Implementation Example**:
```java
// ✅ ACTUAL: Rich domain entity with business behavior
@Entity
@Table(name = "quizzes")
public class QuizEntity extends BasePublicEntity<Long> {
    
    @Column(nullable = false, unique = true)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizDifficultyEnum difficulty;
    
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionEntity> questions = new ArrayList<>();
    
    // Business logic: Relationship management
    public void addQuestion(QuestionEntity question) {
        this.questions.add(question);
        question.setQuiz(this);
    }
    
    // Business logic: Encapsulated collection management
    public void setQuestions(List<QuestionEntity> questions) {
        this.questions.clear();
        if (CommonUtils.nonEmptyNorNull(questions)) {
            questions.forEach(this::addQuestion);
        }
    }
}

// ✅ ACTUAL: Value objects as enums
public enum QuizDifficultyEnum {
    EASY, MEDIUM, HARD
}

public enum QuestionTypeEnum {
    SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE, TEXT_INPUT, CODE_CHALLENGE
}
```

#### 2. Application Layer (Services) 🔧

**Location**: `src/main/java/com/quizplatform/quiz/business/service/`

**Purpose**: Application services that orchestrate business operations

**Current Implementation**:
- **QuizService**: Extends `AbstractBasePublicService` with custom business logic
- **Transaction Management**: Proper `@Transactional` boundaries
- **Repository Orchestration**: Coordinates with JPA repositories

**Current Implementation Example**:
```java
// ✅ ACTUAL: Service with proper transaction management
@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService extends AbstractBasePublicService<QuizEntity, Long> {
    
    @Getter 
    private final QuizRepository repository;
    
    // Custom business operation with eager loading
    @Override
    @Transactional(readOnly = true)
    public Page<QuizEntity> findAll(Pageable pageable) {
        List<QuizEntity> allQuizzes = repository.findAllWithQuestions();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allQuizzes.size());
        List<QuizEntity> pageContent = allQuizzes.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allQuizzes.size());
    }
    
    // Proper transaction boundary for business operations
    @Override
    @Transactional(readOnly = true)
    public Optional<QuizEntity> findByPublicId(UUID publicId) {
        log.debug("Finding quiz by public ID: {}", publicId);
        return repository.findByPublicId(publicId);
    }
}
```

#### 3. Infrastructure Layer 🏗️

**Location**: JPA entities, Spring configurations, database access

**Purpose**: Framework-specific implementations and persistence

**Current Implementation**:
- **Base Classes**: Generic CRUD operations via `BasePublicRepository`
- **JPA Repository**: `QuizRepository` with custom queries
- **Database Configuration**: PostgreSQL (dev) and H2 (test)
- **Transaction Support**: Spring's `@Transactional` management

#### 4. Presentation Layer (Controllers) 🌐

**Location**: `src/main/java/com/quizplatform/quiz/business/controller/`

**Purpose**: REST API endpoints and external communication

**Current Implementation**:
- **QuizController**: Extends `BasePublicController` for standard CRUD operations
- **DTOs**: Request/Response DTOs with validation
- **Exception Handling**: Inherited from base controller framework
- **OpenAPI Documentation**: Swagger annotations for API documentation

**Current Implementation Example**:
```java
// ✅ ACTUAL: Controller leveraging base framework
@RestController
@RequestMapping("/api/v1/quizzes")
@CrossOrigin(origins = "*") // For development
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quiz Management", description = "APIs for managing quizzes")
public class QuizController extends BasePublicController<QuizEntity, Long, QuizRequestDto, QuizResponseDto, QuizFilterDto> {
    
    @Getter final QuizService service;
    @Getter final QuizMapper mapper;
    
    private static final String RESOURCE_NAME = "QUIZ";
    
    @PostMapping
    @Operation(summary = "Create a new quiz")
    public BaseResponse<QuizResponseDto> createQuiz(@Valid @RequestBody QuizRequestDto quiz) {
        return super.create(quiz);
    }
    
    @GetMapping
    @Operation(summary = "Get all quizzes")
    public BaseListResponse<QuizResponseDto> getAllQuizzes() {
        log.info("Fetching all quizzes");
        return super.findAll();
    }
    
    @GetMapping("/{publicId}")
    @Operation(summary = "Get quiz by public ID")
    public BaseResponse<QuizResponseDto> getQuizByPublicId(@PathVariable UUID publicId) {
        log.info("Fetching quiz with public ID: {}", publicId);
        return super.findByPublicId(publicId);
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

## 🎭 Domain-Driven Design Implementation

### Current DDD Patterns in Use

The project demonstrates solid DDD foundations with these implemented patterns:

### 1. Aggregate Root Pattern ✅

```java
// ✅ IMPLEMENTED: Quiz as Aggregate Root managing Question entities
@Entity
@Table(name = "quizzes")
public class QuizEntity extends BasePublicEntity<Long> {
    
    // Aggregate manages its internal entities
    @OneToMany(
        mappedBy = "quiz",
        cascade = CascadeType.ALL, 
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<QuestionEntity> questions = new ArrayList<>();
    
    // Business invariants enforced through methods
    public void addQuestion(QuestionEntity question) {
        this.questions.add(question);
        question.setQuiz(this); // Maintains bidirectional relationship
    }
    
    public void removeQuestion(QuestionEntity question) {
        this.questions.remove(question);
        question.setQuiz(null);
    }
    
    // Encapsulation: Controlled access to internal state
    public void setQuestions(List<QuestionEntity> questions) {
        this.questions.clear();
        if (CommonUtils.nonEmptyNorNull(questions)) {
            questions.forEach(this::addQuestion);
        }
    }
}
```

### 2. Repository Pattern ✅

```java
// ✅ IMPLEMENTED: Domain-focused repository with business queries
@Repository
public interface QuizRepository extends BasePublicRepository<QuizEntity, Long> {
    
    // Business-focused query: Find quiz with questions loaded
    @Query("SELECT q FROM QuizEntity q LEFT JOIN FETCH q.questions WHERE q.publicId = :publicId")
    Optional<QuizEntity> findByPublicId(@Param("publicId") UUID publicId);
    
    // Eager loading for avoiding LazyInitializationException
    @Query("SELECT DISTINCT q FROM QuizEntity q LEFT JOIN FETCH q.questions")
    List<QuizEntity> findAllWithQuestions();
    
    // Inherits from BasePublicRepository:
    // - findAll(), save(), delete()
    // - findByPublicId(), deleteByPublicId()
    // - existsByPublicId(), findAllByPublicIdIn()
}
```

### 3. Value Objects ✅

```java
// ✅ IMPLEMENTED: Enums as Value Objects
public enum QuizDifficultyEnum {
    EASY, MEDIUM, HARD
}

public enum QuestionTypeEnum {
    SINGLE_CHOICE,
    MULTIPLE_CHOICE, 
    TRUE_FALSE,
    TEXT_INPUT,
    CODE_CHALLENGE
}

// ✅ IMPLEMENTED: Record DTOs as Value Objects
@Builder
public record QuizRequestDto(
    Long id,
    UUID publicId,
    String title,
    String category,
    QuizDifficultyEnum difficulty,
    String description,
    Integer timeLimit,
    Integer passingScore,
    String status,
    String createdBy,
    List<QuestionRequestDto> questions,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) implements BaseDto {}
```

### 4. Base Entity Framework ✅

```java
// ✅ IMPLEMENTED: Generic base classes for common operations
public abstract class BaseEntity<ID> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;
}

public abstract class BasePublicEntity<ID> extends BaseEntity<ID> {
    @Builder.Default
    @Column(name = "public_id", unique = true, nullable = false, updatable = false)
    private UUID publicId = UUID.randomUUID();
}

// Generic service operations
public abstract class AbstractBasePublicService<E extends BasePublicEntity<ID>, ID>
        extends AbstractBaseService<E, ID>
        implements BaseService<E, ID> {
    // Provides CRUD operations for entities with public IDs
}
```

### 5. Domain Services ✅

```java
// ✅ IMPLEMENTED: Application services with business logic
@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService extends AbstractBasePublicService<QuizEntity, Long> {
    
    @Getter 
    private final QuizRepository repository;
    
    // Domain service operation: Custom business logic
    @Override
    @Transactional(readOnly = true)
    public Page<QuizEntity> findAll(Pageable pageable) {
        // Business logic: Eager loading to prevent lazy initialization
        List<QuizEntity> allQuizzes = repository.findAllWithQuestions();
        
        // Pagination logic
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allQuizzes.size());
        List<QuizEntity> pageContent = allQuizzes.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, allQuizzes.size());
    }
}
```

## 🔧 Microservices Organization

### Current Services (Implemented)

| Service | Port | Status | Responsibility | Technology Stack |
|---------|------|--------|----------------|------------------|
| **Gateway Service** | 8080 | ✅ **ACTIVE** | API Gateway, routing, actuator endpoints | Spring Cloud Gateway (reactive) |
| **Quiz Service** | 8081 | ✅ **ACTIVE** | Quiz CRUD, question management, business logic | Spring Boot + JPA + PostgreSQL/H2 |

### Service Communication

```yaml
# Current Gateway Routing (application.yml)
spring:
  cloud:
    gateway:
      routes:
        - id: quiz-service
          uri: http://localhost:8081
          predicates:
            - Path=/api/v1/quizzes/**
```

### Future Services (Planned)

| Service | Port | Responsibility                                           | Technology Stack                  |
|---------|------|----------------------------------------------------------|-----------------------------------|
| **Session Service** | 8082 | ( Real-time ? stdin/stout/stderr) test session management | Spring Boot                       |
| **Results Service** | 8083 | Analytics and reporting                                  | Spring Boot + JPA + PostgreSQL    |
| **Code Execution Service** | 8084 | Secure code execution engine                             | Go 1.24+ + Docker + Kubernetes(?) |

### Current API Structure

```java
// ✅ IMPLEMENTED: RESTful API with base framework
@RestController
@RequestMapping("/api/v1/quizzes")
public class QuizController extends BasePublicController {
    
    // POST /api/v1/quizzes - Create quiz
    // GET /api/v1/quizzes - List all quizzes (with pagination)
    // GET /api/v1/quizzes/{publicId} - Get quiz by public ID
    // PUT /api/v1/quizzes/{publicId} - Update quiz (inherited)
    // DELETE /api/v1/quizzes/{publicId} - Delete quiz (inherited)
}

// Standard response format
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp;
}

public class BaseListResponse<T> {
    private List<T> data;
    private PageInfo pageInfo;
    private long totalElements;
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


### 🔄 Areas for Enhancement

1. **More Value Objects**: Replace primitive obsession
2. **Domain Events**: Add event-driven capabilities
3. **Business Rules**: Move more logic into domain entities
4. **Specifications**: Add query specification pattern
5. **Factory Pattern**: Centralize complex object creation
6. **Configuration Management**:  Current project has multiple application.yaml files and inline string config (e.g api endpoints), this should be standardized to a fewer shared config files


## 🎯 Development Best Practices Summary

### Code Quality Principles

1. **Domain-First Design**: Start with business concepts, not database
2. **Rich Domain Models**: Behavior in entities, not just data
3. **Aggregate Boundaries**: Clear ownership and consistency rules
4. **Ubiquitous Language**: Business terms in code and communication
5. **Transaction Boundaries**: At service layer, not controller level

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

**Last Updated**: 2025-08-05  
**Architecture Pattern**: Domain-Driven Design (DDD)  
**Java Version**: 21 LTS  
**Spring Boot Version**: 3.2.0  
**Current Status**: Core quiz management operational, session management planned  
