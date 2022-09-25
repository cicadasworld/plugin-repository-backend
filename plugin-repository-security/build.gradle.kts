plugins {
    `java-library`
    id("spring-web-dependencies")
}

dependencies {
    api(project(":plugin-repository-service"))
    implementation("io.lettuce:lettuce-core:${lettuceVersion}")
    implementation("com.auth0:java-jwt:${javaJwtVersion}")
}
