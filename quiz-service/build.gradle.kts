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
    
    // Caching
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:3.24.3")
    
    // Mapping and utilities
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    
    // Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    
    // JSON processing
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Search capabilities (future)
    // implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:redis")
    
    // Test slices
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
    }
}

// buildInfo() is already configured in java-service-conventions with common properties

// Quiz-service specific configuration
tasks.test {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
    
    // JVM args for Java 21 features
    jvmArgs("--enable-preview")
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
    
    shouldRunAfter("test")
}

tasks.check {
    dependsOn("integrationTest")
}