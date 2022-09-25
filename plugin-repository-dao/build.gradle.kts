plugins {
    `java-library`
    id("spring-web-dependencies")
}

dependencies {
    api(project(":plugin-repository-domain"))
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
}