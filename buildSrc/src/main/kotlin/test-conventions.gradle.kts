// Common test configuration for all projects
tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
        excludeTags("slow", "integration")
    }
    

    // JVM options for testing
    jvmArgs(
        "-XX:+UseParallelGC",
        "-Xmx1g",
        "-Xms512m"
    )
    
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
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    
    // Fail build on test failures (production-ready setting)
    ignoreFailures = false
    
    // Configure parallel execution
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    
    // Finalize test task on exit
    // finalizedBy(tasks.findByName("jacocoTestReport")) // Enable when jacoco plugin is added
}