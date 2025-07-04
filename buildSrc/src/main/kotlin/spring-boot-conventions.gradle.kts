plugins {
    id("java-service-conventions")
    id("boot-jar-conventions")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
        mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
    }
}

dependencies {
    // Actuator for health checks and metrics
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Development tools (disabled for MVP to avoid dependency issues)
    // developmentOnly("org.springframework.boot:spring-boot-starter-devtools")

    // Configuration processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}
