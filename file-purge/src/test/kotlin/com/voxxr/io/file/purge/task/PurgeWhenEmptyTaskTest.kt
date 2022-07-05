package com.voxxr.io.file.purge.task

import com.voxxr.io.file.purge.event.FilePurgeFailedEvent
import com.voxxr.io.file.purge.exception.FilePurgeFailedException
import com.voxxr.io.file.purge.isEmpty
import com.voxxr.io.file.purge.request.PurgeWhenEmptyRequest
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import kotlin.io.path.exists

class PurgeWhenEmptyTaskTest : AbstractPurgeTaskTest() {

    /**
     * Given a purge task assigned to purge a directory, when the directory exists, and is not empty,
     * then the directory should not be purged.
     */
    @Test
    fun givenValidNonEmptyDirectory_whenPurgeTaskIsRun_thenDirectoryIsNotPurged() {
        val directory = createTempDirectory()
        createTempFile(directory)

        assertThat(directory.exists() && !directory.isEmpty())
            .describedAs("directory should exist, and not be empty before it is purged")
            .isTrue

        val task = createTask(directory, defaultDate)

        try {
            task.run()
        } catch (e: Exception) {
            // trap the exception
        }

        assertThat(directory.exists())
            .describedAs("directory should not exist after it is purged")
            .isTrue
    }

    /**
     * Given a directory forced purge task, when the directory exist, and is not empty,
     * then the directory should be purged.
     */
    @Test
    fun givenValidNonEmpty_whenPurgeTaskIsRunForced_thenDirectoryIsPurged() {
        val directory = createTempDirectory()
        createTempFile(directory)

        assertThat(directory.exists() && !directory.isEmpty())
            .describedAs("directory should exist, and not be empty before it is purged")
            .isTrue

        val task = createTask(directory, defaultDate, true)

        task.run()

        Thread.sleep(1000)

        assertThat(directory.exists())
            .describedAs("directory should not exist after it is purged")
            .isFalse
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

        Assertions.assertThrows(FilePurgeFailedException::class.java) {
            task.run()
        }
    }


    /**
     * Given a purge task assigned to purge a directory, when the directory exists, but is empty,
     * then the directory should be purged
     */
    @Test
    fun givenValidEmptyDirectory_whenPurgeTaskIsRun_thenDirectoryIsPurged() {
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

    private fun createTask(
        path: Path = this.path,
        date: LocalDateTime = defaultDate,
        force: Boolean = false,
        result: CompletableFuture<PurgeWhenEmptyRequest> = CompletableFuture<PurgeWhenEmptyRequest>(),
    ) = PurgeWhenEmptyTask(PurgeWhenEmptyRequest(path, date, force = force), result, eventPublisher)

}
