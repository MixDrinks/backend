package org.mixdrinks.view.controllers.score

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.mixdrinks.view.controllers.search.getSearchParam
import org.mixdrinks.view.controllers.search.paggination.Page
import org.mixdrinks.view.controllers.search.paggination.getPage
import org.mixdrinks.view.controllers.settings.AppSettings

fun Application.rattingSearchView(rattingBuilder: RattingBuilder, appSettings: AppSettings) {
    routing {
        get("v2/search/ratings") {
            val searchRequest = call.getSearchParam()
            val page: Page? = call.getPage(appSettings.pageSize)
            call.respond(rattingBuilder.getRattingSearchResponse(searchRequest, page).associateBy { it.cocktailId })
        }
    }
}
