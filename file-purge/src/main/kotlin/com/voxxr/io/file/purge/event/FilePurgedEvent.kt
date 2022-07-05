package com.voxxr.io.file.purge.event

import com.voxxr.io.file.purge.request.FilePurgeRequest

/**
 * An event to signify that a successful purge has taken place
 */
data class FilePurgedEvent(val request: FilePurgeRequest)
