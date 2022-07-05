package com.voxxr.io.file.purge.event

import com.voxxr.io.file.purge.request.FilePurgeRequest

/**
 * An event to signify that a purge has ben un-scheduled
 */
data class FilePurgeUnScheduledEvent(val request: FilePurgeRequest)
