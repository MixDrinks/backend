package org.mixdrinks.view

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.service(appVersion: String) {
    routing {
        get("/version") {
            call.respond("version" to appVersion)
        }
    }
}
