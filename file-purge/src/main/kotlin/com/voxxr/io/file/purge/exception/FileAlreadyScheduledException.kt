package com.voxxr.io.file.purge.exception

import com.voxxr.io.file.purge.request.FilePurgeRequest

class FileAlreadyScheduledException(request: FilePurgeRequest) :
    FilePurgeException("Attempt to schedule file for purge ($request.file), but is already scheduled ")
