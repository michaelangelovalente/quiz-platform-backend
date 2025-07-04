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