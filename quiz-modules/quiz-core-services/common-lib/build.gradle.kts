plugins {
    id("java-service-conventions")
    id("java-library")
    id("maven-publish")
}

description = "Common library sharing DTOs, entities, exceptions, and utility code."

dependencies {
    // Core Spring Framework (no Boot starter for library)
    api(libs.spring.context)
    api(libs.spring.core)
    api(libs.bundles.spring.data)
    api(libs.bundles.spring.web)
    
    // Validation API
    api(libs.jakarta.validation.api)
    implementation(libs.hibernate.validator)
    
    // JSON processing
    api(libs.bundles.jackson)
    
    // Utilities
    api(libs.bundles.common.utils)
    
    // Testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.bundles.testing.basic)

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