rootProject.name = "event-tracker-build"
include(
    "event-tracker:event-api",
    "event-tracker:event-core",
    "event-tracker:event-application",
    "event-tracker:event-port",
    "event-tracker:event-storage",
    "user-service:user-api",
    "user-service:user-core",
    "user-service:user-application",
    "user-service:user-port",
    "user-service:user-storage",
)