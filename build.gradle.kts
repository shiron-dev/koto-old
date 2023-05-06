import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    application
    kotlin("plugin.jpa") version "1.8.21"

    id("com.palantir.git-version") version "3.0.0"
}

group = "dev.shiron"

val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
val details = versionDetails()
version = details.lastTag
val implementationVersion = "$version-#${details.gitHash.substring(0, 7)}"

repositories {
    mavenCentral()

    maven {
        setUrl("https://jitpack.io")
    }
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    implementation("org.xerial:sqlite-jdbc:3.41.2.1")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.2.2.Final")
    implementation("org.hibernate:hibernate-core:6.2.2.Final")

    implementation("org.slf4j:slf4j-log4j12:2.0.7")
    implementation("net.dv8tion:JDA:5.0.0-beta.9")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0")

    implementation("com.github.walkyst:lavaplayer-fork:1.4.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "19"
}

application {
    mainClass.set("MainKt")
}

tasks.withType<Jar> {
    archiveFileName.set("${project.name}.jar")

    manifest {
        attributes["Main-Class"] = "MainKt"
        attributes["Implementation-Version"] = implementationVersion
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    exclude(
        "META-INF/versions/9/module-info.class",
        "META-INF/LICENSE",
        "META-INF/NOTICE",
        "META-INF/NOTICE.md",
        "META-INF/NOTICE.txt",
        "module-info.class",
        "META-INF/LICENSE.txt",
        "META-INF/LICENSE.md",
        "META-INF/DEPENDENCIES"
    )
}
