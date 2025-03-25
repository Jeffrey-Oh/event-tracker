plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":event-core"))
    implementation(project(":event-port"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("io.projectreactor:reactor-core")
}