plugins {
    `java-library`
    id("spring-web-dependencies")
}

dependencies {
    api(project(":plugin-repository-service"))
    api(project(":plugin-repository-security"))
    implementation("org.springdoc:springdoc-openapi-ui:${openapiUiVersion}")
}
