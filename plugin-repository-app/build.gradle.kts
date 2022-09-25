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
apply(from = "pack.gradle.kts")

tasks.withType<BootJar> {
    archiveBaseName.set("plugin-repository")
    archiveVersion.set("1.0.0")
}

plugins {
    application
    id("spring-web-dependencies")
}

dependencies {
    implementation(project(":plugin-repository-api"))
    //implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    runtimeOnly("org.xerial:sqlite-jdbc:3.36.0.3")
    //runtimeOnly("com.dameng:DmJdbcDriver16:${dmJdbcDriverVersion}")
    //runtimeOnly("gt3rdlibs.mysql:mysql-connector-java:${mysqlJdbcDriverVersion}")
}
