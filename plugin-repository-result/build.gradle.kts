plugins {
    id("java-library-conventions")
}

dependencies {
    implementation(platform("gtcloud.plugin.repository:platform"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}