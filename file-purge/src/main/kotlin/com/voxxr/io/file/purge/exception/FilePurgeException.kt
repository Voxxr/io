package com.voxxr.io.file.purge.exception

import com.voxxr.io.IOException

open class FilePurgeException : IOException {

    constructor(message: String, ex: Exception?) : super(message, ex)
    constructor(message: String) : super(message)
    constructor(ex: Exception) : super(ex)

}
