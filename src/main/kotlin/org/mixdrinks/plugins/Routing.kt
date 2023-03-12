package org.mixdrinks.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import org.mixdrinks.view.error.CocktailNotFound
import org.mixdrinks.view.error.QueryRequireException
import org.mixdrinks.view.error.SortTypeNotFound
import org.mixdrinks.view.error.VoteError

fun Application.configureRouting() {
    install(StatusPages) {
        exception<QueryRequireException> { call, cause ->
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
