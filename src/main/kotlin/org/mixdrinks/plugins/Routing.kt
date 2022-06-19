package org.mixdrinks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.mixdrinks.view.error.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<OffsetToBig> { call, offsetToBig ->
            call.respond(HttpStatusCode.BadRequest, offsetToBig.toString())
        }
        exception<QueryRequire> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.toString())
        }
        exception<CocktailNotFound> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.toString())
        }
        exception<VoteError> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.toString())
        }
        exception<SortTypeNotFound> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.toString())
        }
        exception<Exception> { call, cause ->
            this@configureRouting.log.error(cause.stackTraceToString())
            call.respond(HttpStatusCode.ServiceUnavailable, cause.toString())
        }
    }
}
