plugins {
    id("java-service-conventions")
    id("java-library")
    id("maven-publish")
}

dependencies {
    // Core Spring Framework (no Boot starter for library)
    api("org.springframework:spring-context")
    api("org.springframework:spring-core")
    
    // Validation API
    api("jakarta.validation:jakarta.validation-api")
    implementation("org.hibernate.validator:hibernate-validator")
    
    // JSON processing
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    
    // Utilities
    api("org.apache.commons:commons-lang3")
    api("org.apache.commons:commons-collections4:4.4")
    
    // Date/Time
    api("org.springframework:spring-context")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.assertj:assertj-core")
}

// This is a library, not an application
tasks.jar {
    enabled = true
    archiveClassifier.set("")
}

// Disable bootJar since this is not a Spring Boot application
tasks.findByName("bootJar")?.let { task ->
    task.enabled = false
}

// Java library plugin configuration
java {
    withSourcesJar()
    withJavadocJar()
}

// Publication configuration for internal use
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            artifactId = "quiz-platform-common"
            
            pom {
                name.set("Quiz Platform Common Library")
                description.set("Shared utilities and domain objects for Quiz Platform services")
            }
        }
    }
}