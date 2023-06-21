package org.mixdrinks.admin

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.auth.principal
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.transactions.transaction

private const val KEY_ADMIN_AUTH = "admin-auth"

fun Application.configureAdminAuth(slatPrefix: String) {
    val digestFunction = getHashFunction(slatPrefix)

    install(Authentication) {
        basic(KEY_ADMIN_AUTH) {
            realm = "Access to the '/admin/' path"
            validate { credentials ->
                val user = transaction {
                    Admin.find { AdminTable.name eq credentials.name }.firstOrNull()
                }

                return@validate if (user != null && user.password.contentEquals(digestFunction(credentials.password))) {
                    UserIdPrincipal(user.login)
                } else {
                    null
                }
            }
        }
    }

    routing {
        authenticate(KEY_ADMIN_AUTH) {
            get("/admin") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }
    }
}
