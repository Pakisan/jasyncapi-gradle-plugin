plugins {
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "0.12.0"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    `maven-publish`
    signing
    id("idea")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "com.asyncapi"
version = "1.0.0-SNAPSHOT"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(gradleApi())

    implementation("com.asyncapi:jasyncapi-plugin-core:1.0.0-SNAPSHOT")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
}

tasks {
    test {
        useJUnitPlatform()
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

/*
    Publishing configuration for: https://plugins.gradle.org
 */

gradlePlugin {
    plugins {
        create("asyncapiPlugin") {
            id = "${project.group}.${rootProject.name}"
            implementationClass = "com.asyncapi.plugin.gradle.AsyncAPIPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/asyncapi/jasyncapi"
    vcsUrl = "https://github.com/asyncapi/jasyncapi.git"
    description = "Gradle plugin for AsyncAPI.\nHelps to generate AsyncAPI specification from hand-crafted AsyncAPI class at choosed build cycle step."
    tags = listOf("async-api", "async api", "asyncapi", "java", "kotlin")
    (plugins) {
        "asyncapiPlugin" {
            id = "${project.group}.${rootProject.name}"
            version = "${project.version}"
            displayName = "AsyncAPI gradle plugin"
            description = "Gradle plugin for AsyncAPI. Helps to generate asyncapi schemas from sources"
        }
    }
    mavenCoordinates {
        groupId = "${project.group}"
        artifactId = "${rootProject.name}"
        version = "${project.version}"
    }
}

/*
    Publishing configuration for: central maven repository
 */

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("pluginMaven") {
            groupId = "${project.group}"
            artifactId = "${rootProject.name}"
            version = "${project.version}"

            pom {
                name.set("AsyncAPI Gradle plugin")
                inceptionYear.set("2020")
                url.set("https://github.com/asyncapi/jasyncapi")
                description.set("Gradle plugin for AsyncAPI.\n" +
                        "Helps to generate AsyncAPI specification from hand-crafted AsyncAPI class at choosed build cycle step.")
                organization {
                    name.set("AsyncAPI Initiative")
                    url.set("https://www.asyncapi.com/")
                }
                developers {
                    developer {
                        name.set("Pavel Bodiachevskii")
                        url.set("https://github.com/Pakisan")
                    }
                }
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                        comments.set("A business-friendly OSS license")
                    }
                }
                scm {
                    url.set("https://github.com/asyncapi/jasyncapi")
                    connection.set("scm:git:https://github.com/asyncapi/jasyncapi.git")
                    tag.set("HEAD")
                }
                packaging = "jar"
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = project.property("ossrhUsername") as String?
                password = project.property("ossrhPassword") as String?
            }
        }
    }
}

signing {
    sign(publishing.publications["pluginMaven"])
}