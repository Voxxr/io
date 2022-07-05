package com.voxxr.io.file.purge.exception

import com.voxxr.io.file.purge.request.FilePurgeRequest

class PurgeSchedulingFailedException(request: FilePurgeRequest, reason: String?) :
    FilePurgeException("File purge scheduling failed (reason: $reason) (request: $request)")
