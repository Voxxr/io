package com.voxxr.io.file.purge.task

import com.voxxr.io.file.purge.event.FilePurgeFailedEvent
import com.voxxr.io.file.purge.event.FilePurgedEvent
import com.voxxr.io.file.purge.exception.FilePurgeFailedException
import com.voxxr.io.file.purge.request.FilePurgeRequest
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import java.util.concurrent.CompletableFuture

/**
 * A scheduled task for purging a file, directory, or directory contents.
 *
 * A task is `complete` when it has either
 *  - successfully completed the task
 *  - has errored and will throw an exception
 *
 * @param request the purge request
 * @param result a future that is completed by the task when the task is complete
 * @param eventPublisher Springs application event publisher
 */
abstract class FilePurgeTask<REQUEST : FilePurgeRequest>(
    val request: REQUEST,
    private val result: CompletableFuture<REQUEST>,
    private val eventPublisher: ApplicationEventPublisher
) : Runnable {

    private val logger = KotlinLogging.logger {}


    /**
     * Signal to listeners that the task is complete
     */
    private fun onComplete() {
        result.complete(request)
    }

    /**
     * Encapsulated success handling
     */
    protected fun success() {
        logger.info { "File purged ($request)" }
        eventPublisher.publishEvent(FilePurgedEvent(request))
        onComplete()
    }

    /**
     * Encapsulated error handling
     */
    protected fun error(reason: String?) {
        logger.error { "File purge failed (reason: ${reason}) (request: $request)" }
        eventPublisher.publishEvent(FilePurgeFailedEvent(request, reason))
        onComplete()
        throw FilePurgeFailedException(request, reason)
    }

}
