plugins {
    id("spring-boot-conventions")
    id("aop-conventions")
}

dependencies {
    // Internal dependencies
    implementation(project(":common-lib"))
    
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2") // For testing
    
    // Analytics and data processing
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    
    // Caching for performance
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:3.24.3")
    
    // Messaging for event processing
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.kafka:spring-kafka")
    
    // Report generation (commented out for MVP - can be re-enabled later)
    // implementation("net.sf.jasperreports:jasperreports:6.20.0")
    // implementation("com.itextpdf:itextpdf:5.5.13.3")
    implementation("org.apache.poi:poi:5.2.4")
    implementation("org.apache.poi:poi-ooxml:5.2.4")
    
    // CSV processing
    implementation("com.opencsv:opencsv:5.8")
    implementation("org.apache.commons:commons-csv:1.10.0")
    
    // JSON processing and data transformation
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    
    // Mathematical and statistical operations
    implementation("org.apache.commons:commons-math3:3.6.1")
    
    // Mapping and utilities
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    
    // Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    
    // Async processing
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    
    // File storage and handling
    implementation("commons-io:commons-io:2.13.0")
    
    // Email notifications for reports
    implementation("org.springframework.boot:spring-boot-starter-mail")
    
    // Template engine for report formatting
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:redis")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("org.testcontainers:kafka")
    
    // Batch testing
    testImplementation("org.springframework.batch:spring-batch-test")
    
    // Security testing
    testImplementation("org.springframework.security:spring-security-test")
    
    // Report testing
    testImplementation("org.xmlunit:xmlunit-core:2.9.1")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
        mavenBom("org.springframework.kafka:spring-kafka-bom:3.1.0")
    }
}

// buildInfo() is already configured in java-service-conventions with common properties

// Results-service specific configuration
tasks.test {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
    
    // JVM args for Java 21 features
    jvmArgs("--enable-preview")
    
    // Increase memory for report generation tests
    maxHeapSize = "1024m"
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

// Integration testing
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
    jvmArgs("--enable-preview")
    
    // Higher memory for report generation integration tests
    maxHeapSize = "2048m"
    
    shouldRunAfter("test")
}

tasks.check {
    dependsOn("integrationTest")
}

// Custom task for generating sample reports (useful for testing)
tasks.register<JavaExec>("generateSampleReports") {
    description = "Generates sample reports for testing"
    group = "application"
    
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.quizplatform.results.ReportGeneratorApplication")
    args("--generate-samples")
    
    systemProperty("spring.profiles.active", "sample-generation")
}