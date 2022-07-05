package com.voxxr.io.file.purge

import com.voxxr.io.file.purge.exception.FileAlreadyScheduledException
import com.voxxr.io.file.purge.exception.FileNotScheduledException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class FilePurgerSchedulingTest : AbstractPurgeTest() {

    /**
     * Given a purge request, when the request is scheduled, then the purger
     * identifies the request as scheduled.
     */
    @Test
    fun givenPurgeRequest_whenScheduled_thenPurgerIdentifiesItAsScheduled() {
        val request = createDefaultRequest()

        purger.schedule(request)

        assertThat(purger.isScheduled(request.path))
            .describedAs("The request should identify as scheduled")
            .isTrue
    }

    /**
     * Given a file that is already scheduled, when un-scheduling the file, then
     * the purger should not identify the file as scheduled.
     */
    @Test
    fun givenFileIsAlreadyScheduled_whenUnScheduled_thenPurgerDoesNotIdentifyItAsScheduled() {
        val request = createDefaultRequest()

        purger.schedule(request)
        purger.unSchedule(request.path)

        assertThat(purger.isScheduled(request.path))
            .describedAs("The file should not identify as scheduled")
            .isFalse
    }

    /**
     * Given a file that is already scheduled, when scheduling the same file, then
     * a [FileAlreadyScheduledException] should be thrown.
     */
    @Test
    fun givenFileIsAlreadyScheduled_whenScheduledAgain_thenFileAlreadyScheduledExceptionIsThrown() {
        val request = createDefaultRequest()

        assertThrows(FileAlreadyScheduledException::class.java) {
            purger.schedule(request)
            purger.schedule(request)
        }
    }

    /**
     * Given a file that is not scheduled, when un-scheduling the same file, then a
     * [FileNotScheduledException] should be thrown.
     */
    @Test
    fun givenFileIsNotScheduled_whenUnScheduled_thenFileNotScheduledExceptionIsThrown() {
        assertThrows(FileNotScheduledException::class.java) {
            purger.unSchedule(path)
        }
    }

    /**
     * Given a purge request, when the purge is complete, then the purger
     * should not identify the request as scheduled.
     */
    @Test
    fun givenRequest_whenPurgeIsComplete_thenRequestIsRemovedFromSchedule() {
        val request = createDefaultRequest(LocalDateTime.now().plusNanos(1))

        val future = purger.schedule(request)

        // Manually complete the future. This would normally be dont INSIDE the [FilePurger]
        // but due to the nature of the mocks, and not wanting to create multiple different
        // mock configurations for the various tests, this was the path of least resistance
        future.complete(request)

        assertThat(purger.isScheduled(request.path))
            .describedAs("the purge request should no longer be scheduled once it is completed")
            .isFalse
    }

}
