package com.voxxr.io.file.purge.request

import java.nio.file.Path
import java.time.Duration
import java.time.LocalDateTime

/**
 * A request to purge a directory when it becomes empty.
 *
 * @param path the directory to purge when it is empty
 * @param expires the date/time to purge the directory, even if it is not empty
 * @param pollingInterval the interval duration for checking if the directory is empty
 * @param force determines if the directory should be forcibly purged, even if it has not
 *      become empty by its expiry
 */
class PurgeWhenEmptyRequest(
    path: Path,
    val expires: LocalDateTime,
    val pollingInterval: Duration = Duration.ofMinutes(5),
    force: Boolean = false
) : FilePurgeRequest(path, false, force) {

    override fun toString(): String {
        return "PurgeWhenEmptyRequest(file=$path, expires=$expires, pollingInterval=$pollingInterval)"
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + expires.hashCode()
        result = 31 * result + pollingInterval.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PurgeWhenEmptyRequest) return false
        if (!super.equals(other)) return false

        if (expires != other.expires) return false
        if (pollingInterval != other.pollingInterval) return false

        return true
    }

}
