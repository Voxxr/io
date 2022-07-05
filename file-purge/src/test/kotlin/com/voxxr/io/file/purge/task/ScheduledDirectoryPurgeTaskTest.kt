package com.voxxr.io.file.purge.task

import com.voxxr.io.file.purge.event.FilePurgeFailedEvent
import com.voxxr.io.file.purge.event.FilePurgedEvent
import com.voxxr.io.file.purge.exception.FilePurgeFailedException
import com.voxxr.io.file.purge.request.ScheduledPurgeRequest
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import kotlin.io.path.exists

class ScheduledDirectoryPurgeTaskTest : AbstractPurgeTaskTest() {

    /**
     * Given a purge task assigned to purge a directory, when the directory exists, then
     * the directory should be purged.
     */
    @Test
    fun givenValidDirectory_whenPurgeTaskIsRun_thenDirectoryIsPurged() {
        val directory = createTempDirectory()
        assertThat(directory.exists())
            .describedAs("directory should exist before it is purged")
            .isTrue

        val task = createTask(directory, defaultDate)

        task.run()

        assertThat(directory.exists())
            .describedAs("directory should not exist after it is purged")
            .isFalse
    }

    /**
     * Given a purge task assigned to purge a directory, when the directory exists, then
     * the task should publish a [FilePurgedEvent].
     */
    @Test
    fun givenValidDirectory_whenPurgeTaskIsRun_thenFilePurgedEventIsPublished() {
        val directory = createTempDirectory()
        assertThat(directory.exists())
            .describedAs("directory should not exist")
            .isTrue

        val task = createTask(directory, defaultDate)

        task.run()

        verify { eventPublisher.publishEvent(ofType(FilePurgedEvent::class)) }
    }

    /**
     * Given a purge task assigned to purge a directory, when the directory does not
     * exist, then the task should throw a [FilePurgeFailedException].
     */
    @Test
    fun givenNonExistingDirectory_whenPurgeTaskIsRun_thenFilePurgeFailedExceptionIsThrown() {
        val directory = Path.of("/tmp/does/not/exist")
        assertThat(directory.exists())
            .describedAs("directory should not exist")
            .isFalse

        val task = createTask(directory, defaultDate)

        assertThrows(FilePurgeFailedException::class.java) {
            task.run()
        }
    }

    /**
     * Given a purge task assigned to purge a directory, when the directory does not
     * exist, then the task should publish a [FilePurgeFailedEvent].
     */
    @Test
    fun givenNonExistingDirectory_whenPurgeTaskIsRun_thenFilePurgeFailedEventIsPublished() {
        val directory = Path.of("/tmp/does/not/exist")
        assertThat(directory.exists())
            .describedAs("directory should not exist")
            .isFalse

        val task = createTask(directory, defaultDate)

        try {
            task.run()
        } catch (e: Exception) {
            // trap the exception
        }

        verify { eventPublisher.publishEvent(ofType(FilePurgeFailedEvent::class)) }
    }

    private fun createTask(
        path: Path = this.path,
        date: LocalDateTime = defaultDate,
        result: CompletableFuture<ScheduledPurgeRequest> = CompletableFuture<ScheduledPurgeRequest>()
    ) = ScheduledPurgeTask(ScheduledPurgeRequest(path, date), result, eventPublisher)

}
