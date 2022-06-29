package org.mixdrinks.view.settings

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.mixdrinks.settings.AppSettings

fun Application.appSetting(appSettings: AppSettings) {
    routing {
        get("settings") {
            call.respond(appSettings)
        }
        get("v2/settings") {
            call.respond(appSettings)
        }
    }
}
