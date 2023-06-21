package org.mixdrinks.admin

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.auth.bearer
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.transactions.transaction

private const val KEY_ADMIN_AUTH = "admin-auth"
private const val KEY_SUPPER_ADMIN_AUTH = "supper-admin-auth"

fun Application.configureAdminAuth(supperAdminToken: String, slatPrefix: String) {
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
        bearer(KEY_SUPPER_ADMIN_AUTH) {
            authenticate { tokenCredential ->
                if (tokenCredential.token == supperAdminToken) {
                    UserIdPrincipal("supper_admin")
                } else {
                    null
                }
            }
        }
    }

    routing {
        authenticate(KEY_ADMIN_AUTH) {
            get("admin") {
                call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
            }
        }

        authenticate(KEY_SUPPER_ADMIN_AUTH) {
            post("supper-admin/add-admin") {
                println("post run ")
                println(call.request.headers)
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
