rootProject.name = "quiz-platform-backend"

// Enable modern Gradle features
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Include buildSrc for shared build logic
includeBuild("buildSrc")

// Core services
include(
    "gateway-service"
    // TODO: other modules will go here
)

// Configure build cache for faster builds
buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, ".gradle/build-cache")
    }
}