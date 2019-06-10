// SPDX-License-Identifier: LGPL-3.0-or-later

import groovy.util.Node

plugins {
    kotlin("multiplatform")
    //id("kotlinx-serialization")
    jacoco
    id("maven-publish")
    signing
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.10.0")
                implementation("org.jetbrains.kotlin:kotlin-reflect")
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }
        jvm("jvm").compilations["test"].defaultSourceSet {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }
    }
}

jacoco {
    toolVersion = Versions.JACOCO
}

tasks {
    val coverage = register<JacocoReport>("jacocoJVMTestReport") {
        group = "Reporting"
        description = "Generate Jacoco coverage report."
        classDirectories.setFrom(fileTree("$buildDir/classes/kotlin/jvm/main"))
        val coverageSourceDirs = listOf("src/commonMain/kotlin", "src/jvmMain/kotlin")
        additionalSourceDirs.setFrom(files(coverageSourceDirs))
        sourceDirectories.setFrom(files(coverageSourceDirs))
        executionData.setFrom(files("$buildDir/jacoco/jvmTest.exec"))
        @Suppress("UnstableApiUsage")
        reports {
            html.isEnabled = true
            xml.isEnabled = true
            csv.isEnabled = false
        }
    }
    named("jvmTest") {
        finalizedBy(coverage)
    }
}

// Publishing code.

// Notes:
//
// 1) This publishing code has been derived from snippets found on GitHub attributable to:
// Keven Galligan (Stately project)
// Jake Wharton and Alec Strong (SQLDelight project)
// Russell Wolf (multiplatform-settings project)
// Sergey Igushkin (k-new-mpp-samples project)
// Mike Sinkovsky (libui project)
//
// 2) By all rights this code should be applied from a separate file, however, this is not possible due to a short term
// Gradle limitation with type-save model accessors.
//
// 3) Publishing is done to Maven Central since that allows for the artifacts to be found using either jcenter or Maven
// Central.
//
// 4) It is the case that the Javadoc sources do not work properly. Empty Javadoc is provided to satisfy Maven Central
// constraints.

group = Publish.GROUP
version = Versions.ARGUS_STORE

val releaseRepositoryUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
val snapshotRepositoryUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
val javadocJar by tasks.creating(Jar::class) { archiveClassifier.value("javadoc") }
val sourcesJar by tasks.creating(Jar::class) { archiveClassifier.value("sources") }

publishing {
    publications.withType<MavenPublication>().all {
        fun customizeForMavenCentral(pom: org.gradle.api.publish.maven.MavenPom) = pom.withXml {
            fun Node.add(key: String, value: String) { appendNode(key).setValue(value) }
            fun Node.node(key: String, content: Node.() -> Unit) { appendNode(key).also(content) }
            fun addToNode(node: Node) {
                node.add("description", Publish.POM_DESCRIPTION)
                node.add("name", Publish.POM_NAME)
                node.add("url", Publish.POM_URL)
            }
            fun addOrganizationSubNode(node: Node) {
                node.node("organization") {
                    add("name", Publish.POM_ORGANIZATION_NAME)
                    add("url", Publish.POM_ORGANIZATION_URL)
                }
            }
            fun addIssuesSubNode(node: Node) {
                node.node("issueManagement") {
                    add("system", "github")
                    add("url", "https://github.com/h0tk3y/k-new-mpp-samples/issues")
                }
            }
            fun addLicensesSubNode(node: Node) {
                node.node("licenses") {
                    node("license") {
                        add("name", Publish.POM_LICENSE_NAME)
                        add("url", Publish.POM_LICENSE_URL)
                        add("distribution", Publish.POM_LICENSE_DIST)
                    }
                }
            }
            fun addSCMSubNode(node: Node) {
                node.node("scm") {
                    add("url", Publish.POM_SCM_URL)
                    add("connection", Publish.POM_SCM_CONNECTION)
                    add("developerConnection", Publish.POM_SCM_DEV_CONNECTION)
                }
            }
            fun addDevelopersSubNode(node: Node) {
                node.node("developers") {
                    node("developer") {
                        add("name", Publish.POM_DEVELOPER_NAME)
                        add("id", Publish.POM_DEVELOPER_ID)
                    }
                }
            }

            asNode().run {
                addToNode(this)
                addOrganizationSubNode(this)
                addIssuesSubNode(this)
                addLicensesSubNode(this)
                addSCMSubNode(this)
                addDevelopersSubNode(this)
            }
        }
        fun isReleaseBuild(): Boolean = !Versions.ARGUS_STORE.endsWith("-SNAPSHOT")
        fun getRepositoryUrl(): String = if (isReleaseBuild()) releaseRepositoryUrl else snapshotRepositoryUrl

        artifact(javadocJar)
        customizeForMavenCentral(pom)
        @Suppress("UnstableApiUsage")
        if (isReleaseBuild()) signing.sign(this@all)

        repositories {
            maven {
                url = uri(getRepositoryUrl())
                credentials {
                    username = "${project.property("SONATYPE_NEXUS_USERNAME")}"
                    password = "${project.property("SONATYPE_NEXUS_PASSWORD")}"
                }
            }
        }
    }
}
