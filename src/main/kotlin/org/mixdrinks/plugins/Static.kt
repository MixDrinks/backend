package org.mixdrinks.plugins

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticFiles
import io.ktor.server.routing.routing
import java.io.File

fun Application.static() {
    routing {
        staticFiles("/robots.txt", File("static/robots.txt"), index = "")
        staticFiles("/docs/index.html", File("static/docs/index.html"))
    }
}
