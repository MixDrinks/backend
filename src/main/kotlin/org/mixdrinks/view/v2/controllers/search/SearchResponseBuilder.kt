package org.mixdrinks.view.v2.controllers.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.view.cocktail.CompactCocktailVM
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages
import org.mixdrinks.view.v2.controllers.filter.FilterModels

class SearchResponseBuilder(private val cocktailsSourceV2: CocktailsSourceV2) {

    @Serializable
    data class SearchResponse(
        @SerialName("totalCount") val totalCount: Int,
        @SerialName("cocktails") val cocktails: List<CompactCocktailVM>,
        @SerialName("futureCounts") val futureCounts: Map<FilterModels.FilterGroupId, List<FilterCount>>,
    )

    @Serializable
    data class FilterCount(
        @SerialName("id") val id: FilterModels.FilterId,
        @SerialName("count") val count: Int,
    )

    fun getCocktailsBySearch(searchParam: SearchParam, page: Page?, sortType: SortType): SearchResponse {
        val cocktailsIds: List<Int> = cocktailsSourceV2.getCocktailsBySearch(searchParam).map { it.value }

        val sortColumn : Expression<*> = when (sortType) {
            SortType.MOST_POPULAR -> CocktailsTable.visitCount
            SortType.BIGGEST_RATE -> CocktailsTable.ratingValue
        }

        return transaction {
            val query =
                CocktailsTable.select { CocktailsTable.id inList cocktailsIds }.orderBy(sortColumn, SortOrder.DESC)

            val totalCount = query.count().toInt()

            val cocktails: List<CompactCocktailVM> = if (page != null) {
                query.copy().limit(page.limit, page.offset.toLong())
            } else {
                query
            }.map(::createCocktails)

            val test: Map<FilterModels.Filters, List<FilterCount>> =
                FilterModels.Filters.values().associateWith { filter ->
                    val filterIds: List<FilterModels.FilterId> = cocktailsSourceV2.filterIds[filter]!!

                    filterIds.map { filterId ->
                        val futureSearchParam: MutableMap<FilterModels.FilterGroupId, List<FilterModels.FilterId>> =
                            searchParam.filters.toMutableMap()
                        futureSearchParam[filter.id] = futureSearchParam[filter.id].orEmpty().plus(filterId)

                        FilterCount(
                            id = filterId,
                            count = cocktailsSourceV2.getCocktailsBySearch(SearchParam(futureSearchParam)).count()
                        )
                    }
                }

            return@transaction SearchResponse(
                totalCount = totalCount,
                cocktails = cocktails,
                futureCounts = test.map { (key, value) -> Pair(key.id, value) }.toMap(),
            )
        }
    }

    private fun createCocktails(row: ResultRow): CompactCocktailVM {
        val id = row[CocktailsTable.id]
        val ratingValue = row[CocktailsTable.ratingValue]

        val rating = ratingValue?.let {
            it.toFloat() / row[CocktailsTable.ratingCount].toFloat()
        }

        return CompactCocktailVM(
            id = id,
            name = row[CocktailsTable.name],
            rating = rating,
            visitCount = row[CocktailsTable.visitCount],
            images = buildImages(id, ImageType.COCKTAIL)
        )
    }
}