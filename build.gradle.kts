
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
