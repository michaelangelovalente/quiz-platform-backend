// Common test configuration for all projects
tasks.withType<Test> {
    useJUnitPlatform()
    
    // Enable preview features JAVA
    jvmArgs("--enable-preview")
    
    // Test logging configuration
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = false
    }
    
    // Common test system properties
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    
    // Memory settings for tests
    minHeapSize = "256m"
    maxHeapSize = "1024m"
}

// Default test configuration
tasks.named<Test>("test") {
    systemProperty("spring.profiles.active", "test")


    // TODO? Fail build on test failures
    ignoreFailures = false
    
    // Run tests in parallel
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
}