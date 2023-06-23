package org.mixdrinks.admin

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.transactions.transaction

const val KEY_ADMIN_AUTH = "admin-auth"
const val KEY_SUPPER_ADMIN_AUTH = "supper-admin-auth"

fun Application.configureAdminController(adminPasswordsSlat: String) {
    val digestFunction = getHashFunction(adminPasswordsSlat)

    routing {
        authenticate(KEY_ADMIN_AUTH) {
            get("admin-api/admin") {
                val adminName = requireNotNull(call.principal<UserIdPrincipal>()).name
                call.respond(HttpStatusCode.OK, AdminResponse(adminName))
            }
        }

        authenticate(KEY_SUPPER_ADMIN_AUTH) {
            post("supper-admin/add-admin") {
                val adminRequest = call.receive<AdminRequest>()
                val result = transaction {
                    Admin.new {
                        login = adminRequest.login
                        password = digestFunction(adminRequest.password)
                    }
                }

                call.respond(HttpStatusCode.Created, "Admin added ${result.login}!")
            }
        }
    }
}
