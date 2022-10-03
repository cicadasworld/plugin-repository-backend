plugins {
    id("java-library-conventions")
}

dependencies {
    implementation(project(":plugin-repository-domain"))
    implementation(project(":plugin-repository-result"))
    implementation(project(":plugin-repository-dao"))
    implementation(platform("gtcloud.plugin.repository:platform"))
    implementation("org.apache.commons:commons-lang3")
    implementation("org.bouncycastle:bcprov-jdk15on")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}