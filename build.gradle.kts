plugins {
    kotlin("jvm") version "2.2.21"
    id("com.gradleup.shadow") version "9.3.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    id("org.jetbrains.dokka") version "2.1.0"
}

group = "dev.m1sk9"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    register("buildPlugin") {
        dependsOn("ktlintFormat", "ktlintCheck", "shadowJar")
    }
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

dokka {
    moduleName.set("RuneCore")
    dokkaPublications {
        html {
            suppressInheritedMembers = true
            failOnWarning = true
        }
    }
    pluginsConfiguration {
        html {
            footerMessage.set("© 2025 Sho Sakuma and Rune of Mer DevTeam")
        }
    }
}
