plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven {
        name = "SpringMilestones"
        url = uri("https://repo.spring.io/milestone")
        mavenContent {
            includeGroupByRegex("""org\.springframework.*""")
        }
    }
}

dependencies {
    // Plugin dependencies for buildSrc
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.2.0")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.4")
    implementation("com.bmuschko:gradle-docker-plugin:9.4.0")
}