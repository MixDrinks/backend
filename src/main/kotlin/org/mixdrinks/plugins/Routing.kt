package org.mixdrinks.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.mixdrinks.view.cocktail.OffsetToBig

fun Application.configureRouting() {
    install(StatusPages) {
        exception<OffsetToBig> { call, offsetToBig ->
            call.respond(HttpStatusCode.BadRequest, offsetToBig.toString())
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
