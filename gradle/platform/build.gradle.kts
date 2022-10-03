plugins {
    id("java-platform")
}

group = "gtcloud.plugin.repository"

javaPlatform {
    allowDependencies()
}

dependencies {
    api(platform("org.junit:junit-bom:5.8.2"))
    api(platform("com.fasterxml.jackson:jackson-bom:2.13.3"))
    api(platform("org.springframework.boot:spring-boot-dependencies:2.7.2"))
    api(platform("com.squareup.okhttp3:okhttp-bom:4.9.3"))
}

dependencies {
    constraints {
        api("org.apache.commons:commons-lang3:3.12.0")
        api("org.slf4j:slf4j-api:1.7.36")
        api("org.slf4j:slf4j-simple:1.7.36")
        api("org.springdoc:springdoc-openapi-ui:1.6.11")
        api("org.bouncycastle:bcprov-jdk15on:1.70")
        api("com.auth0:java-jwt:4.0.0")
        api("org.xerial:sqlite-jdbc:3.36.0.3")
        api("io.lettuce:lettuce-core:6.1.9.RELEASE")
    }
}
