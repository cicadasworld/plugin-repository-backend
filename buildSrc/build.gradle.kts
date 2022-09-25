buildscript {
    repositories {
        maven(url = uri("../../../mvn-repo"))
    }

    dependencies {
        classpath("org.gradle.kotlin.kotlin-dsl:org.gradle.kotlin.kotlin-dsl.gradle.plugin:1.2.9")
    }
}

apply(plugin = "org.gradle.kotlin.kotlin-dsl")

repositories {
   maven(url = uri("../../../mvn-repo"))
}
