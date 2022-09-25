tasks.register("pack") {
    doLast {
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
        if (true) {
            val destDir = File(project.rootDir, "release/windows_x64/Plugin-Repository-1.0.0/pluginrepo/xapps/plugin-repository")
            project.copy {
                from(bootJarDir)
                into(destDir)
            }
            val date = java.time.LocalDate.now()
            val currentDate = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
            val baseName = "plugin_repository_1.0.0-windows_x64"
            val archiveName = "${baseName}-${currentDate}.7z"
            val packDir = File(project.rootDir, "release/windows_x64")
            java.io.File(packDir, archiveName).delete()
            val fileName = "Plugin-Repository-1.0.0"
            project.exec {
                workingDir(packDir)
                commandLine(listOf("7z", "a", "-t7z", archiveName, fileName))
            }
        }

        // linux_amd64
        if (false) {
            val destDir = File(project.rootDir, "release/linux_amd64/Plugin-Repository-1.0.0/pluginrepo/xapps/plugin-repository")
            project.copy {
                from(bootJarDir)
                into(destDir)
            }
            val date = java.time.LocalDate.now()
            val currentDate = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
            val baseName = "plugin_repository_1.0.0-linux_amd64"
            val tarFileName = "${baseName}-${currentDate}.tar"
            val gzFileName = "${tarFileName}.gz";
            val packDir = File(project.rootDir, "release/linux_amd64")
            java.io.File(packDir, tarFileName).delete()
            java.io.File(packDir, gzFileName).delete()
            val fileName = "Plugin-Repository-1.0.0"
            project.exec {
                workingDir(packDir)
                commandLine(listOf("7z", "a", "-ttar", tarFileName, fileName))
            }
            project.exec {
                workingDir(packDir)
                commandLine(listOf("7z", "a", "-tgzip", gzFileName, tarFileName))
            }
            java.io.File(packDir, tarFileName).delete()
        }

        // linux_aarch64
        if (false) {
            val destDir = File(project.rootDir, "release/linux_aarch64/Plugin-Repository-1.0.0/pluginrepo/xapps/plugin-repository")
            project.copy {
                from(bootJarDir)
                into(destDir)
            }
            val date = java.time.LocalDate.now()
            val currentDate = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))
            val baseName = "plugin_repository_1.0.0-linux_aarch64"
            val tarFileName = "${baseName}-${currentDate}.tar"
            val gzFileName = "${tarFileName}.gz";
            val packDir = File(project.rootDir, "release/linux_aarch64")
            java.io.File(packDir, tarFileName).delete()
            java.io.File(packDir, gzFileName).delete()
            val fileName = "Plugin-Repository-1.0.0"
            project.exec {
                workingDir(packDir)
                commandLine(listOf("7z", "a", "-ttar", tarFileName, fileName))
            }
            project.exec {
                workingDir(packDir)
                commandLine(listOf("7z", "a", "-tgzip", gzFileName, tarFileName))
            }
            java.io.File(packDir, tarFileName).delete()
        }
    }
}
