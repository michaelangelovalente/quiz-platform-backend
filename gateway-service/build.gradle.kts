

plugins {
    id("spring-boot-conventions")
    id("aop-conventions")
}

dependencies {
    // Spring Cloud Gateway
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")

    // Security
//    implementation("org.springframework.boot:spring-boot-starter-security")

    // Redis for caching and session (reactive)
//    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
}

// Gateway-specific configuration
tasks.test {
    systemProperty("spring.profiles.active", "test")
}

// Enable bootJar (disable regular jar)
tasks.bootJar {
    enabled = true
    archiveClassifier.set("")
}

tasks.jar {
    enabled = false
}