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
    implementation(libs.spring.boot.starter.actuator)

    // Development tools
    developmentOnly(libs.spring.boot.starter.devtools)

    // Configuration processor
    annotationProcessor(libs.spring.boot.configuration.processor)
}
