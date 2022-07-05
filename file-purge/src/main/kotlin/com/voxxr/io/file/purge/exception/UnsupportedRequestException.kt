package com.voxxr.io.file.purge.exception

import com.voxxr.io.file.purge.request.FilePurgeRequest

class UnsupportedRequestException(request: FilePurgeRequest, reason: String? = null) :
    FilePurgeException("Cannot process unsupported request (${request.javaClass.simpleName}) ($reason)")
