package com.voxxr.io.file.purge.handler

import com.voxxr.io.file.purge.event.FilePurgeScheduledEvent
import com.voxxr.io.file.purge.request.FilePurgeRequest
import com.voxxr.io.file.purge.request.PurgeWhenEmptyRequest
import com.voxxr.io.file.purge.task.PurgeWhenEmptyTask
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.support.PeriodicTrigger
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


/**
 * Handles the scheduling of a [PurgeWhenEmptyRequest].
 *
 * @param eventPublisher Spring's application event publisher
 */
class PurgeWhenEmptyScheduleHandler(
    private val eventPublisher: ApplicationEventPublisher
) : ScheduleHandler<PurgeWhenEmptyRequest> {

    private val logger = KotlinLogging.logger {}


    override fun handles(request: FilePurgeRequest) = request is PurgeWhenEmptyRequest

    override fun schedule(
        request: PurgeWhenEmptyRequest,
        result: CompletableFuture<PurgeWhenEmptyRequest>,
        scheduler: TaskScheduler
    ): ScheduledFuture<*> {
        val task = PurgeWhenEmptyTask(request, result, eventPublisher)
        val trigger = PeriodicTrigger(request.pollingInterval.toMillis(), TimeUnit.MILLISECONDS)
        val scheduledTask = scheduler.schedule(task, trigger)

        logger.info { "File (${request.path}) scheduled for purge when empty" }
        eventPublisher.publishEvent(FilePurgeScheduledEvent(request))

        return scheduledTask!!
    }

}
