package com.voxxr.io.file.purge.exception

import com.voxxr.io.file.purge.request.FilePurgeRequest

class FilePurgeFailedException(request: FilePurgeRequest, reason: String?) :
    FilePurgeException("File purge failed (reason: $reason) (request: $request)")
