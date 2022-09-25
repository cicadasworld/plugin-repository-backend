buildscript {
    repositories {
        maven(url = uri("../../../mvn-repo"))
    }

    dependencies {
        classpath("org.openapitools:openapi-generator-gradle-plugin:${openapiGeneratorVersion}")
    }
}

apply(plugin = "org.openapi.generator")

tasks.named<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerate") {
     generatorName.set("html")
     inputSpec.set("$rootDir/plugin-repository-api-docs/plugin-repository.yaml")
     outputDir.set("$rootDir/plugin-repository-api-docs/html")
}

