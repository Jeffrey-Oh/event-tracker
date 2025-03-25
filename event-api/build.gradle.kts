dependencies {
    implementation(project(":event-core"))
    implementation(project(":event-storage"))
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
}
