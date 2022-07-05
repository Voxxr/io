package com.voxxr.io.file.purge.request

import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

/**
 * A request to purge a file, directory, or directory contents.
 *
 * @param path the file or directory to purge
 * @param contentsOnly denotes if the entire directory should be purged,
 *      or just its contents
 * @param force denotes if the a directory should be forcibly purged
 *      in the event it is not empty
 */
abstract class FilePurgeRequest(
    val path: Path,
    val contentsOnly: Boolean = false,
    val force: Boolean = false
) {

    /**
     * Determines if this request is for purging a file.
     *
     * @return true if this request is for purging a file, otherwise false
     */
    fun isToPurgeFile() = path.isRegularFile()

    /**
     * Determines if this request is for purging a directory.
     *
     * @return true if this request is for purging a directory, otherwise false
     */
    fun isToPurgeDirectory() = path.isDirectory() && !contentsOnly

    /**
     * Determines if this request is for purging a directory's contents.
     *
     * @return true if this request is for purging a directory's contents, otherwise false
     */
    fun isToPurgeDirectoryContents() = path.isDirectory() && contentsOnly

    fun fileExists() = path.exists()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FilePurgeRequest) return false

        if (path != other.path) return false
        if (contentsOnly != other.contentsOnly) return false
        if (force != other.force) return false

        return true
    }

    override fun hashCode(): Int {
        var result = path.hashCode()
        result = 31 * result + contentsOnly.hashCode()
        result = 31 * result + force.hashCode()
        return result
    }

}
