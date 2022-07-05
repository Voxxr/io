package com.voxxr.io.file.purge

import com.voxxr.io.file.purge.event.FilePurgeUnScheduledEvent
import com.voxxr.io.file.purge.exception.FileAlreadyScheduledException
import com.voxxr.io.file.purge.exception.FileNotScheduledException
import com.voxxr.io.file.purge.exception.PurgeSchedulingFailedException
import com.voxxr.io.file.purge.handler.ScheduleHandlerSet
import com.voxxr.io.file.purge.request.FilePurgeRequest
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.TaskScheduler
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

/**
 * File purger.
 *
 * Manages the scheduling and un-scheduling the purge of a file or directory.
 *
 * @param scheduleHandlers a set of scheduling handlers
 * @param scheduler handles the execution of scheduled tasks
 * @param eventPublisher Springs application event publisher
 */
class FilePurger(
    private val scheduleHandlers: ScheduleHandlerSet,
    private val scheduler: TaskScheduler,
    private val eventPublisher: ApplicationEventPublisher
) {

    private val logger = KotlinLogging.logger {}

    private val scheduledPurges = ConcurrentHashMap<FilePurgeRequest, ScheduledFuture<*>>()


    /**
     * Schedule a file, directory, or directory contents to be purged.
     *
     * @param request the purge request
     * @return true if the scheduling completed successfully, false otherwise
     */
    fun schedule(request: FilePurgeRequest): CompletableFuture<FilePurgeRequest> {
        logger.info { "Received request to schedule purge (request: $request)" }
        errorIfAlreadyScheduled(request)

        try {
            val result = CompletableFuture<FilePurgeRequest>()
            val scheduledTask = scheduleHandlers.schedule(request, result, scheduler)

            scheduledPurges[request] = scheduledTask
            result.thenAccept(this::removeTask)

            return result
        } catch (e: Exception) {
            throw PurgeSchedulingFailedException(request, e.message)
        }
    }

    /**
     * Un-schedule a file for purge
     *
     * @param path the file to un-schedule
     * @throws FileNotScheduledException if the given file is not scheduled for purge
     */
    fun unSchedule(path: Path, interruptIfRunning: Boolean = false): Boolean {
        val scheduled = getScheduledEntry(path) ?: throw FileNotScheduledException(path)

        scheduled.value.cancel(interruptIfRunning)
        scheduledPurges.remove(scheduled.key)

        logger.info { "File ($path) unscheduled for purge (${scheduled.key}" }
        eventPublisher.publishEvent(FilePurgeUnScheduledEvent(scheduled.key))

        return true
    }

    /**
     * Attempt to locate an existing scheduled purge
     *
     * @param file the file to locate a scheduled purge for
     * @return a scheduled entry if the provided file was already scheduled,
     *      otherwise null
     */
    private fun getScheduledEntry(file: Path): MutableMap.MutableEntry<FilePurgeRequest, ScheduledFuture<*>>? {
        return try {
            scheduledPurges.entries.first { it.key.path == file }
        } catch (e: NoSuchElementException) {
            null
        }
    }

    /**
     * Determines if the provided file is currently scheduled for purge
     *
     * @param file the file for which to check current scheduling
     * @return true if the given file is currently scheduled for purge,
     *      otherwise false
     */
    fun isScheduled(file: Path): Boolean = getScheduledEntry(file) != null

    /**
     * Checks if the purge request is for a file that is already scheduled, and if
     * it is, throw an exception.
     *
     * @param request contains the file for which to check if it is currently scheduled
     * @throws FileAlreadyScheduledException if the request is for a file that is
     *      already scheduled
     */
    private fun errorIfAlreadyScheduled(request: FilePurgeRequest) {
        getScheduledEntry(request.path) ?: return

        logger.info { "Attempted to schedule a purge that was already scheduled - $request" }
        throw FileAlreadyScheduledException(request)
    }

    /**
     * Remove a task from the scheduler.
     *
     * @param request the request to cancel and remove
     */
    private fun removeTask(request: FilePurgeRequest) {
        val scheduled = getScheduledEntry(request.path)

        scheduled?.let {
            logger.debug { "removing task for request ($request)" }
            scheduled.value.cancel(true)
            scheduledPurges.remove(scheduled.key, scheduled.value)
        }
    }

}
