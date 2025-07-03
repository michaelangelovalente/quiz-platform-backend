//plugins {
//    id("java")
//}
//
//group = "com.quizplatform"
//version = "1.0-SNAPSHOT"
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    testImplementation(platform("org.junit:junit-bom:5.10.0"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
//}
//
//tasks.test {
//    useJUnitPlatform()
//}

plugins {
    base
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