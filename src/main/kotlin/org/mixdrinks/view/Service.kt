package org.mixdrinks.view

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.plugins.openapi.openAPI
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
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
    }
}

@Serializable
data class Version(
    @SerialName("version")
    val version: String
)
