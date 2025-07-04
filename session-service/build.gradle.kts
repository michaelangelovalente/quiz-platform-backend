plugins {
    id("spring-boot-conventions")
    id("aop-conventions")
}

dependencies {
    // Internal dependencies
    implementation(project(":common-lib"))
    
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    
    // Reactive support for real-time features
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2") // For testing
    
    // Messaging for real-time events
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.kafka:spring-kafka")
    
    // Session state management
    implementation("org.springframework.session:spring-session-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:3.24.3")
    
    // State machine for session lifecycle
    implementation("org.springframework.statemachine:spring-statemachine-starter:4.0.0")
    
    // Real-time communication
    implementation("org.springframework:spring-messaging")
    implementation("org.springframework.security:spring-security-messaging")
    
    // Mapping and utilities
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    
    // JSON processing for WebSocket messages
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    
    // Rate limiting and throttling
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0")
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-redis:7.6.0")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:redis")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("org.testcontainers:kafka")
    
    // WebSocket testing
    testImplementation("org.springframework:spring-websocket")
    testImplementation("org.springframework.security:spring-security-test")
    
    // Reactive testing
    testImplementation("io.projectreactor:reactor-test")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
        mavenBom("org.springframework.kafka:spring-kafka-bom:3.1.0")
    }
}

// buildInfo() is already configured in java-service-conventions with common properties

// Session-service specific configuration
tasks.test {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
    
    // JVM args for Java 21 features and WebSocket testing
    jvmArgs("--enable-preview", "--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

// Enable bootJar
tasks.bootJar {
    enabled = true
    archiveClassifier.set("")
    launchScript()
}

// Disable plain jar
tasks.jar {
    enabled = false
}

// Integration testing with WebSocket support
configurations {
    create("integrationTestImplementation") {
        extendsFrom(configurations.testImplementation.get())
    }
}

sourceSets {
    create("integrationTest") {
        java.srcDir("src/integrationTest/java")
        resources.srcDir("src/integrationTest/resources")
        compileClasspath += sourceSets.main.get().output + configurations.testRuntimeClasspath.get()
        runtimeClasspath += output + compileClasspath
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests"
    group = "verification"
    
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "integration-test")
    jvmArgs("--enable-preview", "--add-opens", "java.base/java.lang=ALL-UNNAMED")
    
    // Increase timeout for WebSocket tests
    systemProperty("test.timeout.websocket", "30000")
    
    shouldRunAfter("test")
}

tasks.check {
    dependsOn("integrationTest")
}