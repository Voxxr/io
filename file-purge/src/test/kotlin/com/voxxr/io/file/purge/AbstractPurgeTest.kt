package com.voxxr.io.file.purge

import com.voxxr.io.file.purge.handler.ScheduleHandlerSet
import com.voxxr.io.file.purge.request.FilePurgeRequest
import com.voxxr.io.file.purge.request.ScheduledPurgeRequest
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkClass
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture
import kotlin.io.path.createTempFile

@TestInstance(PER_CLASS)
abstract class AbstractPurgeTest {

    protected val path = Path.of("foobar.txt")

    protected open val defaultDate: LocalDateTime = LocalDateTime.now().plusDays(30)

    protected lateinit var purger: FilePurger

    protected lateinit var eventPublisher: ApplicationEventPublisher

    protected lateinit var taskScheduler: ThreadPoolTaskScheduler

    protected lateinit var scheduleHandlerSet: ScheduleHandlerSet


    @BeforeAll
    fun beforeAll() {
        eventPublisher = mockEventPublisher()
        taskScheduler = mockTaskScheduler()
        scheduleHandlerSet = mockScheduleHandlers()
        purger = createPurger()
    }

    protected fun createDefaultRequest(date: LocalDateTime = defaultDate) =
        ScheduledPurgeRequest(createTempFile(), date)

    private fun createPurger(): FilePurger {
        val eventPublisher = mockEventPublisher()
        return FilePurger(scheduleHandlerSet, taskScheduler, eventPublisher)
    }

    private fun mockTaskScheduler() = mockk<ThreadPoolTaskScheduler> {
        every {
            schedule(ofType(Runnable::class), ofType(Date::class))
        } answers {
            mockkClass(ScheduledFuture::class) {
                every { cancel(any()) } returns true
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun mockScheduleHandlers() = mockk<ScheduleHandlerSet> {
        every {
            schedule(
                ofType(FilePurgeRequest::class),
                ofType(CompletableFuture::class) as CompletableFuture<FilePurgeRequest>,
                ofType(TaskScheduler::class)
            )
        } answers {
            mockkClass(ScheduledFuture::class) {
                every { cancel(any()) } returns true
            }
        }
    }

    private fun mockEventPublisher() = mockk<ApplicationEventPublisher> {
        justRun { publishEvent(ofType(Any::class)) }
    }

}
