quiz-platform-backend/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   └── libs.versions.toml
├── buildSrc/
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       ├── java-service-conventions.gradle.kts
│       ├── spring-boot-conventions.gradle.kts
│       └── aop-conventions.gradle.kts
└── gateway-service/
├── build.gradle.kts
└── src/main/
├── java/com/quizplatform/gateway/
│   ├── GatewayApplication.java
│   ├── configuration/
│   │   ├── GatewayConfiguration.java
│   │   └── SecurityConfiguration.java
│   └── aspect/
│       └── LoggingAspect.java
└── resources/
└── application.yml