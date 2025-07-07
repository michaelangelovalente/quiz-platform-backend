plugins {
    base
}

allprojects {
    group = "com.quizplatform"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven {
            name = "SpringMilestones"
            url = uri("https://repo.spring.io/milestone")
            mavenContent {
                // Only look for Spring artifacts in Spring repository
                includeGroupByRegex("""org\.springframework.*""")
            }
        }
    }
}

// Root level tasks for multi-project coordination
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

// Performance optimization for multi-project builds
subprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.isIncremental = true
        options.isFork = true
        options.forkOptions.jvmArgs?.addAll(listOf(
            "-Xmx2g",
            "-XX:+UseParallelGC"
        ))
    }
    
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
        systemProperty("junit.jupiter.execution.parallel.enabled", "true")
        systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    }
}
