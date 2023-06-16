package org.mixdrinks.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.response.respondRedirect
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.RedirectsTable

fun Application.configureRedirectMiddleWare() {
    val redirectMap = transaction {
        RedirectsTable.selectAll().map { Pair(it[RedirectsTable.from], it[RedirectsTable.to]) }
    }.toMap()

    val redirect = createRouteScopedPlugin(name = "RedirectRequestPlugin") {
        onCall { call ->
            call.request.headers["x-user-path"]?.let { xUserPath ->
                val to = redirectMap[xUserPath]
                if (to != null) {
                    call.respondRedirect(permanent = true, url = to)
                }
            }
        }
    }

    install(redirect)
}

