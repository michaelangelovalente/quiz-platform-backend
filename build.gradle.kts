
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
}

tasks.register("buildAll") {
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("build") })
    description = "Build all subprojects"
}