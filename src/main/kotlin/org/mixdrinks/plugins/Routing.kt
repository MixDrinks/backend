package org.mixdrinks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.mixdrinks.view.error.CocktailNotFound
import org.mixdrinks.view.error.OffsetToBig
import org.mixdrinks.view.error.QueryRequire
import org.mixdrinks.view.error.VoteError

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
        exception<Exception> { call, cause ->
            println(cause.stackTraceToString())
            call.respond(HttpStatusCode.BadRequest, cause.toString())
        }
        exception<AuthenticationException> { call, cause ->
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<AuthorizationException> { call, cause ->
            call.respond(HttpStatusCode.Forbidden)
        }
    }
}

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()
