plugins {
    id("java-library-conventions")
}

dependencies {
    implementation(project(":plugin-repository-domain"))
    implementation(project(":plugin-repository-result"))
    implementation(project(":plugin-repository-service"))
    implementation(platform("gtcloud.plugin.repository:platform"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.lettuce:lettuce-core")
    implementation("com.auth0:java-jwt")
}