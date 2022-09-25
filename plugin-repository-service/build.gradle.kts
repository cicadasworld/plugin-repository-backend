plugins {
    `java-library`
    id("spring-web-dependencies")
}

dependencies {
    api(project(":plugin-repository-dao"))
    api(project(":plugin-repository-result"))
    implementation("com.squareup.okhttp3:okhttp:${okhttpVersion}")
    implementation("org.apache.commons:commons-lang3:${commonsVersion}")
    implementation("org.bouncycastle:bcprov-jdk15on:${bouncyCastleVersion}")
}
