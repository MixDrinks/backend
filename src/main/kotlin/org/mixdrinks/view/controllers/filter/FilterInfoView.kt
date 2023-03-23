package org.mixdrinks.view.controllers.filter

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.filterMetaInfo(filterSource: FilterSource) {
    routing {
        /**
         * Return the meta info about the filters.
         */
        get("v2/filters") {
            call.respond(filterSource.getMetaInfo())
        }
    }
}
