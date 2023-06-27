package org.mixdrinks.users

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.mixdrinks.auth.FIREBASE_AUTH
import org.mixdrinks.auth.FirebasePrincipalUser

fun Application.userController() {
    routing {
        authenticate(FIREBASE_AUTH) {
            get("user-api/check") {
                val user: FirebasePrincipalUser =
                    call.principal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(HttpStatusCode.OK, user)
            }
        }
    }
}
