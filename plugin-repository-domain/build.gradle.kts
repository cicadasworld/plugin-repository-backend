plugins {
    id("java-library-conventions")
}

dependencies {
    implementation(platform("gtcloud.plugin.repository:platform"))
    implementation("com.fasterxml.jackson.core:jackson-annotations")
}