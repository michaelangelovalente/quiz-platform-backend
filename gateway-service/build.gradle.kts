plugins {
    id("spring-boot-conventions")
}

dependencies {
    // Gateway specific dependencies
    implementation(libs.spring.cloud.starter.gateway)
    implementation(libs.spring.cloud.starter.circuitbreaker.reactor.resilience4j)
    
    // Testing
    testImplementation(libs.bundles.testing)
}