package com.voxxr.io.file.purge.handler

import com.voxxr.io.file.purge.event.FilePurgeScheduledEvent
import com.voxxr.io.file.purge.request.FilePurgeRequest
import com.voxxr.io.file.purge.request.ScheduledPurgeRequest
import com.voxxr.io.file.purge.task.ScheduledPurgeTask
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.TaskScheduler
import java.time.ZoneId
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture

/**
 * Handles the scheduling of a [ScheduledPurgeRequest].
 *
 * @param eventPublisher Spring's application event publisher
 */
class ScheduledPurgeScheduleHandler(
    private val eventPublisher: ApplicationEventPublisher
) : ScheduleHandler<ScheduledPurgeRequest> {

    private val logger = KotlinLogging.logger {}


    override fun handles(request: FilePurgeRequest) = request is ScheduledPurgeRequest

    override fun schedule(
        request: ScheduledPurgeRequest,
        result: CompletableFuture<ScheduledPurgeRequest>,
        scheduler: TaskScheduler
    ): ScheduledFuture<*> {
        val task = ScheduledPurgeTask(request, result, eventPublisher)
        val scheduledTask = scheduler.schedule(
            task,
            Date.from(request.date.atZone(ZoneId.systemDefault()).toInstant())
        )


        eventPublisher.publishEvent(FilePurgeScheduledEvent(request))

        return scheduledTask
    }

}
