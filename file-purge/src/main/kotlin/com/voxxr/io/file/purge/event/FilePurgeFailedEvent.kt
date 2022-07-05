package com.voxxr.io.file.purge.event

import com.voxxr.io.file.purge.request.FilePurgeRequest

/**
 * An event to signify that a purge has failed
 */
data class FilePurgeFailedEvent(val request: FilePurgeRequest, val reason: String? = "unknown")
