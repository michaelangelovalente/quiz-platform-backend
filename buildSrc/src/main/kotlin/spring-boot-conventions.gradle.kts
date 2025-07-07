plugins {
    id("java-service-conventions")
    id("boot-jar-conventions")
}

dependencies {
    // Actuator for health checks and metrics
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Configuration processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    
    // Note: DevTools should be added per-project when needed as developmentOnly
    // developmentOnly("org.springframework.boot:spring-boot-starter-devtools")
}
