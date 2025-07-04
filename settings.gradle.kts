rootProject.name = "quiz-platform-backend"

// Enabl Gradle features
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


include(
    "gateway-service",
    "quiz-core-services:common-lib",
    "quiz-core-services:quiz-service",
//    "session-service",
//    "results-service",
)

// Configure build cache for faster builds
buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, ".gradle/build-cache")
    }
}