# Project Structure 
```markdown

    quiz-platform-backend/
    ├── 📋 README.md
    ├── ⚙️ Makefile
    ├── 🔧 build.gradle.kts
    ├── ⚙️ settings.gradle.kts
    ├── 📋 gradle.properties
    ├── 🐳 docker-compose.yml
    │
    ├── 📂 gradle/
    │   ├── 📚 libs.versions.toml
    │   └── 📁 wrapper/
    │
    ├── 🔨 buildSrc/
    │   ├── 🔧 build.gradle.kts
    │   └── 📂 src/main/kotlin/
    │       ├── ⚙️ java-service-conventions.gradle.kts
    │       ├── 🍃 spring-boot-conventions.gradle.kts
    │       ├── 🎯 aop-conventions.gradle.kts
    │       └── 🐳 docker-conventions.gradle.kts
    │
    ├── 🌐 gateway-service/
    │   ├── 🔧 build.gradle.kts
    │   ├── 🐳 Dockerfile
    │   └── 📂 src/main/java/com/quizplatform/gateway/
    │       ├── 🚀 GatewayApplication.java
    │       ├── 📂 aspect/
    │       ├── 📂 configuration/
    │       ├── 📂 filter/
    │       └── 📂 security/
    │
    ├── 🎯 quiz-services/
    │   ├── 📚 common-lib/
    │   ├── 🧩 quiz-service/
    │   ├── 📋 session-service/
    │   └── 📊 results-service/
    │
    ├── ⚡ code-execution-service/
    │   ├── 🔧 go.mod
    │   ├── ⚙️ Makefile
    │   ├── 🐳 Dockerfile
    │   └── 📂 internal/
    │
    └── 🏗️ infrastructure/
    ├── 📂 kubernetes/
    ├── 📂 helm/
    └── 📂 terraform/
```

```markdown
      quiz-platform-backend/
      ├── quiz-modules/                     # All service modules
      │   ├── gateway-service/             # API Gateway
      │   └── quiz-core-services/          # Core quiz functionality
      │       ├── common-lib/              # Shared utilities
      │       └── quiz-service/            # Quiz business logic
      └── system/                          # System-level configurations
          ├── config/                      # Configuration files
          ├── infrastructure/              # Future infrastructure (docker, k8s, terraform)
          ├── scripts/                     # Build and deployment scripts
          └── tools/                       # Development tools
```