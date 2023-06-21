package org.mixdrinks.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.response.respond
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.RedirectsTable

fun Application.configureRedirectMiddleWare() {
    val redirectMap = transaction {
        RedirectsTable.selectAll().map { Pair(it[RedirectsTable.from], it[RedirectsTable.to]) }
    }.toMap()

    install(createRouteScopedPlugin(name = "RedirectRequestPlugin") {
        onCall { call ->
            call.request.headers["x-user-path"]?.let { xUserPath ->
                val to = redirectMap[xUserPath]
                if (to != null) {
                    call.respond<SingleRedirectResponse>(HttpStatusCode.OK, SingleRedirectResponse(to))
                }
            }
        }
    })
}

@Serializable
data class SingleRedirectResponse(
    @SerialName("redirect")
    val redirect: String
)

