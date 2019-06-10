// SDPX-License-Identifier: LGPL-3.0-or-later

plugins {
    kotlin("multiplatform") version Versions.KOTLIN apply false
    //id("kotlinx-serialization") version Versions.KOTLIN apply false
}

allprojects {
    repositories {
        jcenter()
    }

    tasks.register("cleanProject") {
        delete(project.buildDir)
    }
}
