
plugins {
    id("spring-boot-conventions")
    id("aop-conventions")
}

description = "API Gateway service with routing and cross-cutting concerns"

dependencies {
    // Gateway-specific dependencies
    implementation(libs.bundles.spring.gateway)
    
    // Testing
    testImplementation(libs.spring.cloud.starter.contract.stub.runner)
}

// Gateway-specific configuration - test config and bootJar config handled by conventions