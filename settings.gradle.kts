plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "event-tracker-build"
include(
    "event-tracker:event-api",
    "event-tracker:event-core",
    "event-tracker:event-application",
    "event-tracker:event-storage",
    "user-service:user-api",
    "user-service:user-core",
    "user-service:user-application",
    "user-service:user-storage",
)
include("common")
include("common")
