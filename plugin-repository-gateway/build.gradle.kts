import org.springframework.boot.gradle.tasks.bundling.BootJar

buildscript {
    repositories {
        maven(url = uri("../../../mvn-repo"))
    }

    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}

apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")

tasks.withType<BootJar> {
    archiveBaseName.set("plugin-repository-gateway")
    archiveVersion.set("1.0.0")
}

plugins {
    application
}

repositories {
    maven(url = uri("../../../mvn-repo"))
}

dependencies {
    implementation(platform("org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"))
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
}
