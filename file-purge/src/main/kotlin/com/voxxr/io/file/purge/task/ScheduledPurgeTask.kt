package com.voxxr.io.file.purge.task

import com.ponhenge.core.extension.isEmpty
import com.ponhenge.core.io.file.purge.exception.UnsupportedRequestException
import com.ponhenge.core.io.file.purge.request.ScheduledPurgeRequest
import org.apache.tomcat.util.http.fileupload.FileUtils
import org.springframework.context.ApplicationEventPublisher
import org.springframework.util.FileSystemUtils
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.CompletableFuture

/**
 * Task that executes a given [ScheduledPurgeRequest]
 *
 * @param request the purge request
 * @param result a future that is completed by the task when the task is complete
 * @param eventPublisher Springs application event publisher
 */
open class ScheduledPurgeTask(
    request: ScheduledPurgeRequest,
    result: CompletableFuture<ScheduledPurgeRequest>,
    eventPublisher: ApplicationEventPublisher
) : FilePurgeTask<ScheduledPurgeRequest>(request, result, eventPublisher), Runnable {

    override fun run() {
        if (!request.fileExists()) {
            error("file does not exist")
        }

        when (true) {
            request.isToPurgeFile() -> purgeFile()
            request.isToPurgeDirectory() -> purgeDirectory()
            request.isToPurgeDirectoryContents() -> purgeDirectoryContents()
            else -> throw UnsupportedRequestException(request)
        }

        success()
    }

    /**
     * Purge a single file.
     */
    private fun purgeFile() {
        try {
            Files.delete(request.path)
        } catch (e: SecurityException) {
            error(e.message)
        }
    }

    /**
     * Purge a directory with all its contents.
     */
    private fun purgeDirectory() {
        try {
            if (directoryNotEmptyAndNotForced()) {
                error("directory not empty, not forced")
                return
            }

            val result = FileSystemUtils.deleteRecursively(request.path)

            if (!result) {
                error("Unable to recursively delete (${request.path}). ")
            }
        } catch (e: SecurityException) {
            error(e.message)
        }
    }

    /**
     * Purge a directory's contents, but not the directory itself.
     */
    private fun purgeDirectoryContents() {
        try {
            FileUtils.cleanDirectory(request.path.toFile())
        } catch (e: IOException) {
            error(e.message)
        }
    }

    private fun directoryNotEmptyAndNotForced() = !request.path.isEmpty() && !request.force

}
