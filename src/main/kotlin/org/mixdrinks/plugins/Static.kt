package org.mixdrinks.plugins

import io.ktor.server.application.Application
import io.ktor.server.http.content.files
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticRootFolder
import io.ktor.server.routing.routing
import java.io.File

fun Application.static() {
    routing {
        static("/") {
            staticRootFolder = File("static")
            files(".")
        }
    }
}
