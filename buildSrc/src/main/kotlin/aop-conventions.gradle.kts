plugins {
    id("java-service-conventions")
}

dependencies {
    // AOP dependencies
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.aspectj:aspectjweaver:1.9.20.1")
}



// AOP-specific configuration
tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "-Xlint:deprecation",
        "-Xlint:unchecked"
    ))
}

// AspectJ weaving configuration
configurations.all {
    resolutionStrategy {
        // Ensure consistent AspectJ version
        force("org.aspectj:aspectjweaver:1.9.20.1")
        force("org.aspectj:aspectjrt:1.9.20.1")
    }
}