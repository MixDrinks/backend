package org.mixdrinks.admin

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.bearer
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.transactions.transaction

private const val KEY_SUPPER_ADMIN_AUTH = "supper-admin-auth"

fun Application.configSupperAdmin(
    supperAdminToken: String,
    slatPrefix: String
) {
    authentication {
        bearer(KEY_SUPPER_ADMIN_AUTH) {
            realm = "Access to the '/supper-admin/' path"
            authenticate { tokenCredential ->
                if (tokenCredential.token == supperAdminToken) {
                    UserIdPrincipal("supper_admin")
                } else {
                    null
                }
            }
        }
    }

    val digestFunction = getHashFunction(slatPrefix)

    routing {
        authenticate(KEY_SUPPER_ADMIN_AUTH) {
            post("/supper-admin/add-admin") {
                val adminRequest = call.receive<AdminRequest>()
                val result = transaction {
                    Admin.new {
                        login = adminRequest.login
                        password = digestFunction(adminRequest.password)
                    }
                }

                call.respond(HttpStatusCode.Created, "Hello, ${result.login}!")
            }
        }
    }
}
