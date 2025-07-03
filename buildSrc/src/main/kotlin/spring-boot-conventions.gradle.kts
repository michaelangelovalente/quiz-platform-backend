plugins {
    id("java-service-conventions")
}

springBoot {
    buildInfo()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
//    implementation("io.micrometer:micrometer-registry-prometheus")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}