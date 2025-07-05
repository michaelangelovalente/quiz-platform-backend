plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("test-conventions")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()
    withJavadocJar()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
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
    options.compilerArgs.addAll(listOf(
        "-parameters"
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
            additional.set(mapOf(
                "spring.boot.version" to "3.2.0",
                "java.version" to JavaVersion.VERSION_21.toString()
            ))
        }
    }
}