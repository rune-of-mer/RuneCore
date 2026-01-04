plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    id("org.jetbrains.dokka") version "2.1.0"
}

group = "org.lyralis"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.xenondevs.xyz/releases") {
        name = "InvUI"
    }
}

val exposedVersion = "0.61.0"

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    // InvUI は Paper の library loader を通してロードするため compileOnly
    compileOnly("xyz.xenondevs.invui:invui:1.49")
    compileOnly("xyz.xenondevs.invui:invui-kotlin:1.49")

    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.7")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.zaxxer:HikariCP:7.0.2")

    // Test dependencies
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("io.mockk:mockk:1.14.7")
    testImplementation("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

tasks.test {
    useJUnitPlatform()
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
    dokkaSourceSets {
        named("main") {
            includes.from(project.layout.projectDirectory.file("docs/module.md"))
        }
    }
}
