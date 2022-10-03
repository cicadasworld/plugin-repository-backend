import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    id("spring-web-application")
}

tasks.bootJar {
    archiveBaseName.set("plugin-repository")
    archiveVersion.set("1.0.0")
}

dependencies {
    implementation(project(":plugin-repository-api"))
    implementation(platform("gtcloud.plugin.repository:platform"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("org.xerial:sqlite-jdbc")
}

tasks.register("bundle") {
    doLast {
        group = "release"
        description = "package boot jar file for release"

        val appProjectDir = File(project.rootDir, "plugin-repository-app")
        project.copy {
            from(project.buildFile)
            into(appProjectDir)
            rename { "build.gradle.kts.bak" }
        }

        val lines = project.buildFile.readLines()
        project.buildFile.printWriter().use { out ->
            lines.forEach {
                out.println(it)
            }
            out.println("")
            out.println("sourceSets {")
            out.println("    main {")
            out.println("        resources {")
            out.println("            exclude(\"**/*\")")
            out.println("        }")
            out.println("    }")
            out.println("}")
        }

        project.exec {
            workingDir(appProjectDir)
            commandLine(listOf("gradle.bat", "clean", "bootJar"))
        }

        val buildFileBak = File(appProjectDir, "build.gradle.kts.bak")
        project.copy {
            from(buildFileBak)
            into(appProjectDir)
            rename { "build.gradle.kts" }
        }
        project.delete(buildFileBak)

        val bootJarDir = File(appProjectDir, "build/libs")

        // windowx_x64
        val destDir = File(project.rootDir, "release/windows_x64/Plugin-Repository-1.0.0/pluginrepo/xapps/plugin-repository")
        project.copy {
            from(bootJarDir)
            into(destDir)
        }
        val date = LocalDate.now()
        val currentDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val baseName = "plugin_repository_1.0.0-windows_x64"
        val archiveName = "${baseName}-${currentDate}.7z"
        val packDir = File(project.rootDir, "release/windows_x64")
        File(packDir, archiveName).delete()
        val fileName = "Plugin-Repository-1.0.0"
        project.exec {
            workingDir(packDir)
            commandLine(listOf("7z", "a", "-t7z", archiveName, fileName))
        }
    }
}
