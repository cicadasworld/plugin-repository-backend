plugins {
    id("java-library-conventions")
}

dependencies {
    implementation(project(":plugin-repository-domain"))
    implementation(platform("gtcloud.plugin.repository:platform"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
}