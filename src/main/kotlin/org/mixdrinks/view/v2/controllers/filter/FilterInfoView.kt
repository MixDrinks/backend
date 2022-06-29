package org.mixdrinks.view.v2.controllers.filter

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.filterMetaInfo(filterSource: FilterSource) {
    routing {
        get("v2/filters") {
            call.respond(filterSource.getMetaInfo())
        }
    }
}
