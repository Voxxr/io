package com.voxxr.io.file.purge.exception

import java.nio.file.Path

class FileNotScheduledException(file: Path) :
    FilePurgeException("Attempt to unSchedule file for purge ($file), but it is not scheduled")
