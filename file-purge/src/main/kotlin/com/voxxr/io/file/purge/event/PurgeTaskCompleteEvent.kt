package com.voxxr.io.file.purge.event

import com.voxxr.io.file.purge.request.FilePurgeRequest

data class PurgeTaskCompleteEvent(val request: FilePurgeRequest)
