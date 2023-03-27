package org.mixdrinks.view.controllers.search

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.view.cocktail.CompactCocktailVM
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.controllers.filter.FilterCache
import org.mixdrinks.view.controllers.filter.FilterModels
import org.mixdrinks.view.controllers.search.paggination.Page
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

class SearchResponseBuilder(
    private val filterCache: FilterCache,
    private val cocktailSelector: CocktailSelector,
    private val descriptionBuilder: DescriptionBuilder,
) {

    fun getCocktailsBySearch(searchParams: SearchParams, page: Page?, sortType: SortType): SearchResponse {
        exposedLogger.info("searchParams: ${searchParams.filters}")
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
            }.map(::createCocktails)

            val futureCounts: Map<FilterModels.FilterGroupBackend, List<FilterCount>> =
                FilterModels.FilterGroupBackend.values().associateWith { filter ->
                    val filterIds: List<FilterId> = filterCache.filterIds[filter]!!

                    filterIds.map { filterId ->
                        val futureSearchParam: MutableMap<FilterGroupId, List<FilterId>> =
                            searchParams.filters.toMutableMap()
                        futureSearchParam[filter.id] = futureSearchParam[filter.id].orEmpty().plus(filterId)

                        FilterCount(
                            id = filterId,
                            count = cocktailSelector.getCocktailIds(futureSearchParam).count(),
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

    private fun createCocktails(row: ResultRow): CompactCocktailVM {
        val id = row[CocktailsTable.id].value
        val ratingValue = row[CocktailsTable.ratingValue]

        val rating = ratingValue?.let {
            it.toFloat() / row[CocktailsTable.ratingCount].toFloat()
        }

        return CompactCocktailVM(
            id = CocktailId(id),
            name = row[CocktailsTable.name],
            rating = rating,
            visitCount = row[CocktailsTable.visitCount],
            images = buildImages(id, ImageType.COCKTAIL),
            slug = row[CocktailsTable.slug],
        )
    }
}
