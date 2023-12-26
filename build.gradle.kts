import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val githubRepo = "maxbossing/Kotlin-Discord-Webhook"

plugins {
    kotlin("jvm") version "1.9.21"

    `java-library`
    `maven-publish`
    signing
}

group = "de.maxbossing"
version = 1
description = "Kotlin DSL to build and send Discord Webhooks"

repositories {
    mavenCentral()
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "21"
    }

    register("release") {
        group = "publishing"
        dependsOn("publish")
    }
}

kotlin {
    jvmToolchain(21)
}


java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

publishing {
    repositories {
        maven {
            name = "ossrh"
            credentials(PasswordCredentials::class)
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            this.groupId = project.group.toString()
            this.artifactId = project.name.toLowerCase()
            this.version = project.version.toString()

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://maxbossing.de")

                developers {
                    developer {
                        id.set("maxbossing")
                        name.set("Max Bossing")
                        email.set("info@maxbossing.de")
                    }
                }

                licenses {
                    license {
                        name.set("GNU Lesser General Public License, Version 2.1")
                        url.set("https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html")
                    }
                }

                url.set("https://github.com/${githubRepo}")

                scm {
                    connection.set("scm:git:git://github.com/${githubRepo}.git")
                    url.set("https://github.com/${githubRepo}")
                }
            }
        }
    }
}