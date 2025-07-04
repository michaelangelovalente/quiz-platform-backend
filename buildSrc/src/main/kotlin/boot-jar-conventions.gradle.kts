// Common Spring Boot jar configuration for executable services
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    archiveClassifier.set("")
    // Add launch script for Unix systems
    launchScript()
}

// Disable plain jar for executable services  
tasks.named<Jar>("jar") {
    enabled = false
}