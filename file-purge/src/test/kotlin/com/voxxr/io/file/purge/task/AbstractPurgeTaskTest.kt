package com.voxxr.io.file.purge.task

import com.voxxr.io.file.purge.AbstractPurgeTest
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile

abstract class AbstractPurgeTaskTest : AbstractPurgeTest() {

    @TempDir
    protected lateinit var tempDir: Path


    /**
     * Create a single temporary file
     *
     * @return a single temporary file
     */
    protected fun createTempFile(dir: Path = tempDir) =
        createTempFile(dir, "purge-test_", ".txt")

    /**
     * Create a single temporary directory
     *
     * @return a single temporary file
     */
    protected fun createTempDirectory(): Path {
        return createTempDirectory(tempDir, "${this::class.simpleName}")
    }

}
