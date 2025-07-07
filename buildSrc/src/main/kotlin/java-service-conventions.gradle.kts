plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("test-conventions")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
        mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    
    withSourcesJar()
    withJavadocJar()
}

// Use stable configuration API instead of incubating configurations block
configurations.named("compileOnly") {
    extendsFrom(configurations.getByName("annotationProcessor"))
}

dependencies {
    // Common dependencies for all Java services
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // MapStruct for DTO mapping (commented out until needed)
    // implementation("org.mapstruct:mapstruct:1.5.5.Final")
    // annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Testing defaults
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
    options.compilerArgs.addAll(listOf(
        "-parameters",
        "-Xlint:deprecation",
        "-Xlint:unchecked",
        "-Xlint:preview"
    ))
    
    // Performance optimizations
    options.isIncremental = true
    options.isFork = true
    options.forkOptions.jvmArgs?.addAll(listOf(
        "-Xmx2g",
        "-XX:+UseParallelGC"
    ))
}

// Test configuration is provided by test-conventions plugin
tasks.named<Jar>("jar") {
    enabled = false
}

// Spring Boot configuration
springBoot {
    buildInfo {
        properties {
            additional.putAll(mapOf(
                "spring.boot.version" to "3.2.0",
                "java.version" to "21"
            ))
        }
    }
}