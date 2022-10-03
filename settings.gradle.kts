pluginManagement {
    repositories {
        maven(url = uri("../mvn-repo"))
    }

    includeBuild("gradle/plugins")
}

dependencyResolutionManagement {
    repositories {
        maven(url = uri("../mvn-repo"))
    }

    includeBuild("gradle/platform")
}

rootProject.name = "plugin-repository"
include("plugin-repository-api")
include("plugin-repository-app")
include("plugin-repository-dao")
include("plugin-repository-domain")
include("plugin-repository-result")
include("plugin-repository-security")
include("plugin-repository-service")
