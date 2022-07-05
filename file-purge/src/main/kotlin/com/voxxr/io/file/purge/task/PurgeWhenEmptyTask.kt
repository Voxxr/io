package com.voxxr.io.file.purge.task

import com.voxxr.io.file.purge.isEmpty
import com.voxxr.io.file.purge.request.PurgeWhenEmptyRequest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.util.FileSystemUtils
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture


/**
 * A scheduled task for purging a directory, when it has become empty.
 *
 * If the directory has not become empty by the time the [PurgeWhenEmptyRequest.expires]
 * has lapsed, the [PurgeWhenEmptyRequest.force] property will be evaluated. If `force`
 * is true, the directory will be forcibly purged, otherwise an exception will be thrown.
 *
 * @param request the purge request
 * @param result a future that is completed by the task when the task is complete
 * @param eventPublisher Springs application event publisher
 */
class PurgeWhenEmptyTask(
    request: PurgeWhenEmptyRequest,
    result: CompletableFuture<PurgeWhenEmptyRequest>,
    eventPublisher: ApplicationEventPublisher,
) : FilePurgeTask<PurgeWhenEmptyRequest>(request, result, eventPublisher) {

    override fun run() {
        if (!request.fileExists()) {
            error("file does not exist")
        }

        try {
            if (!request.path.isEmpty()) {
                if (request.force) return forcePurge()
                if (isExpired()) return handleExpiration()
            }
        } catch (e: IllegalArgumentException) {
            return
        }

        try {
            Files.delete(request.path)
        } catch (e: SecurityException) {
            error(e.message)
        }

        success()
    }

    /**
     * Determines if the task has expired
     */
    private fun isExpired() = request.expires.isBefore(LocalDateTime.now())

    /**
     * In the event of expiration, force purge if configured to do so,
     * otherwise error out.
     */
    private fun handleExpiration() {
        if (request.force) return forcePurge()
        error("Directory never became empty")
    }

    private fun forcePurge() {
        try {
            val result = FileSystemUtils.deleteRecursively(request.path)

            if (!result) {
                error("Unable to recursively delete (${request.path}). ")
            }

            success()
        } catch (e: SecurityException) {
            error(e)
        }
    }

}
