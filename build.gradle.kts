
plugins {
    base
//    alias(libs.plugins.spring.boot) apply false
//    alias(libs.plugins.spring.dependency.management) apply false
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false

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