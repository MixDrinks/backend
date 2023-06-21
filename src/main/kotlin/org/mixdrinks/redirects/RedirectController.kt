package org.mixdrinks.redirects

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.admin.KEY_ADMIN_AUTH
import org.mixdrinks.data.RedirectsTable

fun Application.redirectController() {
    routing {
        authenticate(KEY_ADMIN_AUTH) {
            get("admin-api/redirects") {
                call.respond(
                    transaction {
                        RedirectsTable.selectAll()
                            .map {
                                Redirect(
                                    from = it[RedirectsTable.from],
                                    to = it[RedirectsTable.to]
                                )
                            }
                    }
                )
            }
        }
    }
}

@Serializable
data class Redirect(
    @SerialName("from")
    val from: String,
    @SerialName("to")
    val to: String
)
