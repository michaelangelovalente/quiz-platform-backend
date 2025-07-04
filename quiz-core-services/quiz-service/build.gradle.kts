plugins {
    id("spring-boot-conventions")
    id("aop-conventions")
}

description = "Quiz management service with JPA persistence"

dependencies {
    // Internal dependencies
    implementation(project(":quiz-core-services:common-lib"))
    
    // Spring Boot Starters
    implementation(libs.bundles.spring.web)
    implementation(libs.bundles.spring.data)
    
    // Database
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2) // For testing
    
    // Documentation
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    
    // JSON processing (additional modules)
    implementation(libs.jackson.module.parameter.names)
    implementation(libs.jackson.datatype.jdk8)
    
    // Testing
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.testcontainers.postgresql)
}


// Quiz-service specific configuration - test config is handled by test-conventions plugin

// Enable bootJar
tasks.bootJar {
    enabled = true
    archiveClassifier.set("")
    launchScript()
}

// Disable plain jar
tasks.jar {
    enabled = false
}

// Integration testing
configurations {
    create("integrationTestImplementation") {
        extendsFrom(configurations.testImplementation.get())
    }
}

sourceSets {
    create("integrationTest") {
        java.srcDir("src/integrationTest/java")
        resources.srcDir("src/integrationTest/resources")
        compileClasspath += sourceSets.main.get().output + configurations.testRuntimeClasspath.get()
        runtimeClasspath += output + compileClasspath
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs integration tests"
    group = "verification"
    
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    
    systemProperty("spring.profiles.active", "integration-test")
    shouldRunAfter("test")
}

tasks.check {
    dependsOn("integrationTest")
}