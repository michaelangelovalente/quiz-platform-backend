rootProject.name = "quiz-platform-backend"

// Enable Gradle features (Type-safe project accessors are stable in Gradle 8.1+)
// enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS") // No longer needed


include(
    "quiz-modules:gateway-service",
    "quiz-modules:quiz-core-services:common-lib",
    "quiz-modules:quiz-core-services:quiz-service",
//    "quiz-modules:session-service",
//    "quiz-modules:results-service",
)

// Configure build cache for faster builds
buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, ".gradle/build-cache")
    }
}