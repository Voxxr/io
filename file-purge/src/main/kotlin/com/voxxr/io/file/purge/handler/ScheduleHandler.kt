package com.voxxr.io.file.purge.handler

import com.voxxr.io.file.purge.request.FilePurgeRequest
import org.springframework.scheduling.TaskScheduler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture

/**
 * Interface for handling the scheduling of a [FilePurgeTask]
 */
interface ScheduleHandler<REQUEST : FilePurgeRequest> {

    /**
     * Determines if the handler supports the given request
     *
     * @param request the request to determine the handling of
     * @return true if this handler will handle the given request, otherwise false
     */
    fun handles(request: FilePurgeRequest): Boolean

    /**
     * Schedule a task for the given request
     *
     * @param request the request to schedule
     * @param result a future that is completed by the task when the task is complete
     * @param scheduler handles the execution of scheduled tasks
     * @return a scheduled future task for the given purge request
     */
    fun schedule(request: REQUEST, result: CompletableFuture<REQUEST>, scheduler: TaskScheduler): ScheduledFuture<*>

}
