package org.mixdrinks.view.v2.controllers.search

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.mixdrinks.settings.AppSettings
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.error.SortTypeNotFound
import org.mixdrinks.view.v2.controllers.filter.FilterModels

fun Application.searchView(searchResponseBuilder: SearchResponseBuilder, appSettings: AppSettings) {
    routing {
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
    val filters: Map<FilterModels.FilterGroupId, List<FilterModels.FilterId>> = mapOf(),
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
            this@getSearchParam.getSearchParam(filterGroup)?.let { this[filterGroup.id] = it }
        }
    })
}

fun ApplicationCall.getSearchParam(filter: FilterModels.Filters): List<FilterModels.FilterId>? {
    return this.request.queryParameters[filter.queryName.value]?.split(",")?.mapNotNull {
        it.toIntOrNull()?.let { it1 -> FilterModels.FilterId(it1) }
    }
}

fun ApplicationCall.getPage(pageSize: Int): Page? {
    val pageIndex = this.request.queryParameters["page"]?.toIntOrNull() ?: return null
    return Page(
        offset = pageIndex * pageSize, limit = pageSize
    )
}
