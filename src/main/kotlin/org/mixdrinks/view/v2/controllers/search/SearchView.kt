package org.mixdrinks.view.v2.controllers.search

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.error.SortTypeNotFound
import org.mixdrinks.view.v2.controllers.filter.FilterModels
import org.mixdrinks.view.v2.controllers.settings.AppSettings

fun Application.searchView(searchResponseBuilder: SearchResponseBuilder, appSettings: AppSettings) {
    routing {
        /**
         * Return the cocktails that match the search request.
         * The search request is a map of filter group id to a list of filter id.
         * The search request is optional, if not provided, all cocktails are returned.
         * The page is optional, if not provided, all cocktails are returned.
         * The sort key is optional, if not provided, the cocktails are sorted by most popular.
         * The sort key is a string that can be one of the following:
         * - "most-popular"
         * - "biggest-rate"
         * Filter from the same group are combined with an AND operator.
         */
        get("v2/search/cocktails") {
            val searchRequest = call.getSearchParam()
            val page: Page? = call.getPage(appSettings.pageSize)
            val sortKey: SortType = call.getSortType()

            call.respond(searchResponseBuilder.getCocktailsBySearch(searchRequest, page, sortKey))
        }
    }
}

data class Page(
    val offset: Int,
    val limit: Int,
)

data class SearchParams(
    val filters: Map<FilterGroupId, List<FilterId>> = mapOf(),
)

fun ApplicationCall.getSortType(): SortType {
    val rawSortType = this.request.queryParameters["sort"]

    return if (rawSortType != null) {
        SortType.values().firstOrNull { it.key == rawSortType } ?: throw SortTypeNotFound()
    } else {
        SortType.MOST_POPULAR
    }
}

fun ApplicationCall.getSearchParam(): SearchParams {
    return SearchParams(buildMap {
        FilterModels.Filters.values().forEach { filterGroup ->
            this@getSearchParam.getSearchParam(filterGroup.queryName)?.let { this[filterGroup.id] = it }
        }
    })
}

fun ApplicationCall.getSearchParam(queryName: FilterModels.FilterQueryName): List<FilterId>? {
    return this.request.queryParameters[queryName.value]?.split(",")?.mapNotNull {
        it.toIntOrNull()?.let { it1 -> FilterId(it1) }
    }
}

fun ApplicationCall.getPage(pageSize: Int): Page? {
    val pageIndex = this.request.queryParameters["page"]?.toIntOrNull() ?: return null
    return Page(
        offset = pageIndex * pageSize, limit = pageSize
    )
}
