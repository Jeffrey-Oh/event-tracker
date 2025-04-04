plugins {
	kotlin("plugin.spring")
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

dependencies {
	implementation(project(":user-service:user-core"))
	implementation(project(":user-service:user-port"))

	implementation("org.springframework.boot:spring-boot-starter")
	implementation("io.projectreactor:reactor-core")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.13.9")
	testImplementation("org.assertj:assertj-core:3.25.3")
	testImplementation("io.projectreactor:reactor-test")
}