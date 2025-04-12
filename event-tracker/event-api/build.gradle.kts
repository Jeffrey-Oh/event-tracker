import com.google.cloud.tools.jib.gradle.PlatformParameters
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib") version "3.4.5"
}

dependencies {
    implementation(project(":event-tracker:event-core"))
    implementation(project(":event-tracker:event-port"))
    implementation(project(":event-tracker:event-application"))
    implementation(project(":event-tracker:event-storage"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor:reactor-core")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("io.projectreactor:reactor-test")
}

val os = OperatingSystem.current()
val arch = System.getProperty("os.arch")

val resolvedPlatform = when {
    os.isMacOsX && arch == "aarch64" -> {
        println("🔧 Platform: macOS ARM64")
        objects.newInstance(PlatformParameters::class.java).apply {
            architecture = "arm64"
            os = "linux"
        }
    }
    else -> {
        println("🔧 Platform: Default AMD64")
        objects.newInstance(PlatformParameters::class.java).apply {
            architecture = "amd64"
            os = "linux"
        }
    }
}

jib {
    from {
        image = "eclipse-temurin:17-jdk"
        platforms.set(listOf(resolvedPlatform))
    }
    to {
        image = "event-tracker:latest"
    }
    container {
        ports = listOf("8080")
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
}

plugins.withId("com.google.cloud.tools.jib") {
    val os = org.gradle.internal.os.OperatingSystem.current()

    the<com.google.cloud.tools.jib.gradle.JibExtension>().apply {
        dockerClient {
            executable = when {
                os.isMacOsX -> "/usr/local/bin/docker"
                os.isWindows -> "docker" // 윈도우는 PATH에 자동 포함되므로 이름만
                os.isLinux -> "/usr/bin/docker"
                else -> "docker" // fallback
            }
        }
    }
}