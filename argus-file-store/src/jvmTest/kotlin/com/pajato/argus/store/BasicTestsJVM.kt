package com.pajato.argus.store

import kotlinx.serialization.UnstableDefault
import java.io.File

@UnstableDefault
actual fun getBuildDir() = "${File(BasicTests::class.java.classLoader.getResource("").path).parent}/test"
