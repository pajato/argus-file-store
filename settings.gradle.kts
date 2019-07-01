pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlin-multiplatform") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            } 
            if (requested.id.id == "kotlinx-serialization") {
                useModule("org.jetbrains.kotlin:kotlin-serialization:${requested.version}")
            }
        }
    }
    repositories {
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}
rootProject.name = "argus-file-store"

include(":argus-file-store")

enableFeaturePreview("GRADLE_METADATA")
