package org.mixdrinks.view.controllers.search.slug

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.controllers.search.getSortType
import org.mixdrinks.view.controllers.search.paggination.Page
import org.mixdrinks.view.controllers.search.paggination.getPage
import org.mixdrinks.view.controllers.settings.AppSettings

fun Application.filterSlugs(searchResponseBuilder: SearchSlugResponseBuilder, appSettings: AppSettings) {
    routing {
        /**
         * Get all cocktails by filter slugs
         * Filter format: v2/filter/{filter},
         * {filter} -> filter group separated by / and filter slugs separated by comma
         * Format of {filter} ->
         * {filter group 1}={filter slugs separated by comma}/{filter group 2}={filter slugs separated by comma}
         * Format of {filter slugs separated by comma} -> {filter slug 1},{filter slug 2},{filter slug 3}
         * Full format for path -> v2/filter/{filter} ->
         * v2/filter/{filter group id}={filter slug 1},{filter slug 2}/{filter group id}={filter slug 1},{filter slug 2}
         * Filter format example v2/filter/taste=miatni,solodki/goods=syrop-makadamiia,syr-salers
         */
        get("v2/filter/{params...}") {
            val filters = call.parameters.getAll("params")
                ?.flatMap { it.split("/") }
                ?.map { it.split("=") }?.associate { it[0] to it[1].split(",") }
                .orEmpty()

            val searchRequest = SearchParamsSlugs(filters)
            val page: Page? = call.getPage(appSettings.pageSize)
            val sortKey: SortType = call.getSortType()

            val result = searchResponseBuilder.getCocktailsSearchBySlugs(searchRequest, page, sortKey)

            call.respond(result)
        }
    }
}

data class SearchParamsSlugs(
    /**
     * keys - filter group slug (query name)
     * values - filter slugs
     */
    val filters: Map<String, List<String>> = mapOf(),
)

