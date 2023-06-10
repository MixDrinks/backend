package org.mixdrinks.view.controllers.search.slug

import org.jetbrains.exposed.sql.exposedLogger
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.controllers.filter.FilterCache
import org.mixdrinks.view.controllers.search.SearchResponse
import org.mixdrinks.view.controllers.search.SearchResponseBuilder
import org.mixdrinks.view.controllers.search.paggination.Page

class SearchSlugResponseBuilder(
    private val filterCache: FilterCache,
    private val searchResponseBuilder: SearchResponseBuilder,
) {

    fun getCocktailsSearchBySlugs(
        searchParamsSlug: SearchParamsSlugs,
        page: Page?,
        sortType: SortType,
    ): SearchResponse {
        exposedLogger.info("searchParams: ${searchParamsSlug.filters}")

        val searchParams = SearchParams(
            filters = searchParamsSlug.filters.map { (group, slugs) ->
                val groupId = filterCache.fullFilterGroupBackend.keys.first { it.queryName.value == group }.id
                val filterGroup = filterCache.fullFilterGroupBackend
                    .mapKeys { (key, _) -> key.id }

                groupId to filterGroup[groupId].orEmpty().filter { it.slug in slugs }.map { it.id }
            }.toMap(),
        )

        return searchResponseBuilder.getCocktailsBySearch(searchParams, page, sortType, true)
    }
}

data class SearchParams(
    val filters: Map<FilterGroupId, List<FilterId>> = mapOf(),
)
