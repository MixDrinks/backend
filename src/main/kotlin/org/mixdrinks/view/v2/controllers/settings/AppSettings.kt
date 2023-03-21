package org.mixdrinks.view.v2.controllers.settings

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.view.v2.controllers.settings.AppSettings

fun Application.appSetting(appSettings: AppSettings) {
    routing {
        /**
         * Return the app settings
         */
        get("v2/settings") {
            call.respond(appSettings)
        }
    }
}

@Serializable
data class AppSettings(
    @SerialName("minVote")
    val minVote: Int,
    @SerialName("maxVote")
    val maxVote: Int,
    @SerialName("pageSize")
    val pageSize: Int,
)
