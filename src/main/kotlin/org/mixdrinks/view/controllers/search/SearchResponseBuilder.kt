package org.mixdrinks.view.controllers.search

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.cocktails.CocktailMapper
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SelectionType
import org.mixdrinks.view.cocktail.CompactCocktailVM
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.controllers.filter.FilterCache
import org.mixdrinks.view.controllers.search.paggination.Page
import org.mixdrinks.view.controllers.search.slug.SearchParams

class SearchResponseBuilder(
    private val filterCache: FilterCache,
    private val cocktailSelector: CocktailSelector,
    private val descriptionBuilder: DescriptionBuilder,
    private val cocktailMapper: CocktailMapper,
) {

    fun getCocktailsBySearch(
        searchParams: SearchParams,
        page: Page?,
        sortType: SortType,
        applyFilterType: Boolean = false
    ): SearchResponse {
        val cocktailsIds = if (searchParams.filters.isNotEmpty()) {
            cocktailSelector.getCocktailIds(searchParams.filters).map { it.id }
        } else {
            transaction {
                CocktailsTable.slice(CocktailsTable.id).selectAll().map { it[CocktailsTable.id].value }
            }
        }

        val sortColumn: Expression<*> = when (sortType) {
            SortType.MOST_POPULAR -> CocktailsTable.visitCount
            SortType.BIGGEST_RATE -> CocktailsTable.ratingValue
        }

        return transaction {
            val query =
                CocktailsTable.select { CocktailsTable.id inList cocktailsIds }
                    .orderBy(sortColumn, SortOrder.DESC_NULLS_LAST)

            val totalCount = query.count().toInt()

            val cocktails: List<CompactCocktailVM> = if (page != null) {
                query.copy().limit(page.limit, page.offset.toLong())
            } else {
                query
            }.map(cocktailMapper::createCocktails)

            val futureCounts: Map<FilterGroups, List<FilterCount>> =
                FilterGroups.values().associateWith { filterGroupBackend ->
                    val filterIds: List<FilterId> = filterCache.filterIds[filterGroupBackend]!!

                    filterIds.map { filterId ->
                        val futureSearchParam: MutableMap<FilterGroupId, List<FilterId>> =
                            searchParams.filters.toMutableMap()
                        if (applyFilterType && filterGroupBackend.selectionType == SelectionType.SINGLE) {
                            futureSearchParam[filterGroupBackend.id] = listOf(filterId)
                        } else {
                            futureSearchParam[filterGroupBackend.id] = futureSearchParam[filterGroupBackend.id]
                                .orEmpty().plus(filterId)
                        }
                        FilterCount(
                            id = filterId,
                            count = cocktailSelector.getCocktailIds(futureSearchParam).count(),
                            query = buildNextQuery(
                                filterGroupBackend.id,
                                filterId,
                                futureSearchParam,
                                searchParams.filters
                            ),
                            isActive = searchParams.filters[filterGroupBackend.id].orEmpty().contains(filterId),
                        )
                    }
                }

            return@transaction SearchResponse(
                totalCount = totalCount,
                cocktails = cocktails,
                futureCounts = futureCounts.map { (key, value) -> Pair(key.id, value) }.toMap(),
                description = descriptionBuilder.buildDescription(searchParams),
            )
        }
    }

    private fun buildNextQuery(
        currentFilterGroupId: FilterGroupId,
        filterId: FilterId,
        futureSearchOption: Map<FilterGroupId, List<FilterId>>,
        currentSearch: Map<FilterGroupId, List<FilterId>>
    ): String {
        exposedLogger.debug("buildNextQuery: $futureSearchOption")
        return futureSearchOption
            .mapNotNull { (groupId, filterIds) ->
                val group = FilterGroups.values().first { it.id == groupId }
                Pair(
                    group,
                    filterCache.fullFilterGroupBackend[group]
                        .orEmpty()
                        .filterNot {
                            it.id == filterId
                                && groupId == currentFilterGroupId
                                && filterId in currentSearch[group.id].orEmpty()
                        }
                        .filter { it.id in filterIds }
                        .map { it.slug }
                        .takeIf { it.isNotEmpty() } ?: return@mapNotNull null)
            }
            .sortedBy { (group, _) -> group.sortOrder }
            .joinToString(separator = "/") { (group, filterSlugs) ->
                "${group.queryName.value}=${filterSlugs.joinToString(",")}"
            }
    }
}
