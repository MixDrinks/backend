package org.mixdrinks.plugins

import io.ktor.http.URLBuilder
import io.ktor.http.path
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
                val origin = call.request.headers["Origin"]
                val to = redirectMap[xUserPath]
                if (to != null) {
                    val url = if (origin != null) {
                        val urlBuilder = URLBuilder(origin)
                        urlBuilder.path(to)
                        urlBuilder.buildString()
                    } else {
                        to
                    }
                    call.respondRedirect(permanent = true, url = url)
                }
            }
        }
    }

    install(redirect)
}

