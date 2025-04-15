plugins {
	kotlin("plugin.spring")
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

dependencies {
	implementation(project(":common"))
	implementation(project(":user-service:user-core"))
	implementation(project(":user-service:user-application"))

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.postgresql:r2dbc-postgresql")
	implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
	implementation("org.redisson:redisson-spring-boot-starter:3.24.3")

	implementation("io.projectreactor:reactor-core")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.13.9")
	testImplementation("org.assertj:assertj-core:3.25.3")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("com.github.codemonstur:embedded-redis:1.4.3")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}