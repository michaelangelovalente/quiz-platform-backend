
plugins {
    base
}

allprojects {
    group = "com.quizplatform"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

// Root level tasks
tasks.register("cleanAll") {
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("clean") })
    description = "Clean all subprojects"
    group = "build"
}

tasks.register("buildAll") {
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("build") })
    description = "Build all subprojects"
    group = "build"
}

tasks.register("testAll") {
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("test") })
    description = "Run unit tests for all subprojects"
    group = "verification"
}

tasks.register("integrationTestAll") {
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("integrationTest") })
    description = "Run integration tests for all subprojects"
    group = "verification"
}

tasks.register("checkAll") {
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("check") })
    description = "Run all checks for all subprojects"
    group = "verification"
}

// Service-specific task groups
tasks.register("buildServices") {
    val serviceProjects = subprojects.filter { it.name.endsWith("-service") }
    dependsOn(serviceProjects.mapNotNull { it.tasks.findByName("build") })
    description = "Build all microservices (excluding common-lib)"
    group = "build"
}

tasks.register("startAllServices") {
    val serviceProjects = subprojects.filter { it.name.endsWith("-service") }
    dependsOn(serviceProjects.mapNotNull { it.tasks.findByName("bootRun") })
    description = "Start all microservices"
    group = "application"
}

// Documentation task
tasks.register("generateAllDocs") {
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("javadoc") })
    description = "Generate JavaDoc for all subprojects"
    group = "documentation"
}

// Additional help tasks
tasks.register("projectOverview") {
    group = "help"
    description = "Show overview of all projects and their purposes"
    doLast {
        println("Quiz Platform Backend - Multi-Project Overview:")
        println("├── common-lib        - Shared utilities and domain objects")
        println("├── gateway-service   - API Gateway (Port 8080)")
        println("├── quiz-service      - Quiz management (Port 8081)")
        println("├── session-service   - Real-time sessions (Port 8082)")
        println("└── results-service   - Analytics & reporting (Port 8083)")
    }
}

subprojects {
    // Apply common settings to all subprojects
    apply(plugin = "java")
    
    // Common JVM settings
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-parameters"))
    }
    
    // Common test settings
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}