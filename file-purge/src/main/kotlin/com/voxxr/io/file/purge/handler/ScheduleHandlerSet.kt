package com.voxxr.io.file.purge.handler

import com.google.common.collect.ForwardingSet
import com.voxxr.io.file.purge.exception.UnsupportedRequestException
import com.voxxr.io.file.purge.request.FilePurgeRequest
import org.springframework.scheduling.TaskScheduler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture

/**
 * Forwarding set of [ScheduleHandler]
 *
 * @param handlers a set of [ScheduleHandler] to forward
 */
class ScheduleHandlerSet(
    private val handlers: Set<ScheduleHandler<FilePurgeRequest>> = emptySet(),
) : ForwardingSet<ScheduleHandler<FilePurgeRequest>>(), ScheduleHandler<FilePurgeRequest> {

    override fun handles(request: FilePurgeRequest) = true

    override fun schedule(
        request: FilePurgeRequest,
        result: CompletableFuture<FilePurgeRequest>,
        scheduler: TaskScheduler
    ): ScheduledFuture<*> {
        try {
            val handler = handlers.first { it.handles(request) }
            return handler.schedule(request, result, scheduler)
        } catch (e: NoSuchElementException) {
            throw UnsupportedRequestException(request)
        }
    }

    override fun delegate() = handlers

}
