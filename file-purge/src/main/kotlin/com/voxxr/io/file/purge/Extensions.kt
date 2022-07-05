package com.voxxr.io.file.purge

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

fun Path.isEmpty(): Boolean {
    if (!Files.isDirectory(this)) return false
    if (!this.exists()) return false
    Files.newDirectoryStream(this).use { directory ->
        return !directory.iterator().hasNext()
    }
}
