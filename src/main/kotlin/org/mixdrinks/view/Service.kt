package org.mixdrinks.view

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

fun Application.service(appVersion: String) {
    routing {
        get("/version") {
            call.respond(Version(appVersion))
        }
    }
}

@Serializable
data class Version(
    @SerialName("version_name") val version: String,
    /**
     * Version code is generated from version name.
     * Formula: remove dots, and remove all after minus sign,
     * and convert it to integer with multiplication for each digit.
     * Patch version must be less than 1000.
     * Minor version must be less than 100.
     * Math formula: major + minor * 1000 + patch
     */
    @SerialName("version_code") val code: Int = version
        .substringBefore('-')
        .split('.').map { it.toInt() }.let {
            require(it.size == VERSION_SIZE)
            require(it[1] < MAX_MINOR_VERSION)
            require(it[2] < MAX_PATCH_VERSION)
            it[0] * MAJOR_VERSION_MULTIPLIER + it[1] * MINOR_VERSION_MULTIPLIER + it[2]
        }
) {
    companion object {
        const val VERSION_SIZE = 3
        const val MAX_MINOR_VERSION = 100
        const val MAX_PATCH_VERSION = 1000
        const val MINOR_VERSION_MULTIPLIER = 1000
        const val MAJOR_VERSION_MULTIPLIER = 100_000
    }
}
