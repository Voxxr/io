package com.voxxr.io.file.purge.request

import java.nio.file.Path
import java.time.LocalDateTime

/**
 * A request to delete a file, directory, or directory contents
 * at a given date/time.
 *
 * @param file the file or directory to purge
 * @param date the date/time to purge the file, directory, or directory contents
 * @param contentsOnly denotes if the entire directory should be purged,
 *      or just its contents
 * @param force denotes if the a directory should be forcibly purged
 *      in the event it is not empty
 */
class ScheduledPurgeRequest(
    file: Path,
    val date: LocalDateTime,
    contentsOnly: Boolean = false,
    force: Boolean = false,
) : FilePurgeRequest(file, contentsOnly, force) {

    override fun toString(): String {
        return "PurgeRequest(file=$path, date=$date, contentsOnly=$contentsOnly, force=$force)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScheduledPurgeRequest) return false
        if (!super.equals(other)) return false

        if (date != other.date) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }

}
