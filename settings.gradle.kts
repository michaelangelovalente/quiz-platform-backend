rootProject.name = "quiz-platform-backend"

// Enable modern Gradle features
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


// Core services
include(
    "gateway-service",
    "quiz-service",
    "session-service", 
    "results-service",
    "common-lib"
)

// Configure build cache for faster builds
buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, ".gradle/build-cache")
    }
}