plugins {
    id("java-service-conventions")
}

dependencies {
    // Actuator for health checks and metrics
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Prometheus metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Development tools (commented out due to version issues - can be re-enabled later)
    // developmentOnly("org.springframework.boot:spring-boot-starter-devtools")

    // Configuration processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

// buildInfo() is already configured in java-service-conventions