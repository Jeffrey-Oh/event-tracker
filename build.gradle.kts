plugins {
    // Kotlin 기본 플러그인
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25" apply false

    // Spring Boot는 필요한 모듈에서만
    id("org.springframework.boot") version "3.4.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.jeffreyoh"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        // Kotlin 공통 의존성
        implementation(kotlin("stdlib"))
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        // 공통 로깅 (모든 모듈에서 사용)
        implementation("io.github.oshai:kotlin-logging-jvm:7.0.5")
    }

    // 컴파일러 설정
    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }

    // 공통 테스트 설정
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}