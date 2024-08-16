plugins {
    id("com.modrinth.minotaur") version "2.+"
    id("fabric-loom") version "1.6-SNAPSHOT"
    id("maven-publish")
    id("java")
}

version = project.extra["mod_version"] as String
group = project.extra["maven_group"] as String

base {
    archivesName.set(project.extra["archives_base_name"] as String)
}

dependencies {
    minecraft("com.mojang:minecraft:${project.extra["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${project.extra["yarn_mappings"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.extra["loader_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.extra["fabric_version"]}")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    compileOnly("org.projectlombok:lombok:1.18.34")
}

loom {
    accessWidenerPath.set(file("src/main/resources/hide.accesswidener"))
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.extra["minecraft_version"])
    inputs.property("loader_version", project.extra["loader_version"])
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.extra["minecraft_version"],
            "loader_version" to project.extra["loader_version"]
        )
    }
}

val targetJavaVersion = 21
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("hide")
    versionNumber.set(project.version as String)
    versionName.set("Version ${project.version}")
    versionType.set("release")
    uploadFile.set(tasks.named("remapJar").get().outputs.files.singleFile)
    dependencies {
        required.project("fabric-api")
    }
}