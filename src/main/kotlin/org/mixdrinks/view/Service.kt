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
    @SerialName("version_code") val code: Int = version.replace(".", "").toInt(),
)
