rootProject.name = "quiz-platform-backend"



include(
    "quiz-modules:gateway-service",
    "quiz-modules:quiz-core-services:common-lib",
    "quiz-modules:quiz-core-services:quiz-service",
    "quiz-modules:results-service"
)

// Configure build cache for faster builds
buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, ".gradle/build-cache")
    }
}