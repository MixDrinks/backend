package org.mixdrinks.view.cocktail.domain

import org.mixdrinks.view.cocktail.data.CocktailsSource
import org.mixdrinks.view.cocktail.data.FullCocktailData
import org.mixdrinks.view.error.OffsetToBig

data class CocktailFilterFull(
    val list: List<FullCocktailData>,
    val totalCount: Int,
    val counts: FutureCocktailsCounts,
)

enum class SortType(val key: String) {
    MOST_POPULAR("most-popular"), BIGGEST_RATE("biggest-rate"),
}

data class CocktailsFilterSearchParam(
    val search: String?,
    val tags: List<Int>?,
    val goods: List<Int>?,
    val tools: List<Int>?,
)

class CocktailsAggregator(
    private val cocktailsSource: CocktailsSource,
    private val futureCountCalculator: CocktailsFutureCountCalculator,
) {

    fun getCompactCocktail(
        searchParam: CocktailsFilterSearchParam,
        offset: Int?,
        limit: Int?,
        sortType: SortType,
    ): CocktailFilterFull {
        return filterCocktails(
            cocktailsSource.cocktails,
            searchParam,
            offset,
            limit,
            sortType,
        )
    }

    private fun filterCocktails(
        cocktails: List<FullCocktailData>,
        searchParam: CocktailsFilterSearchParam,
        offset: Int?,
        limit: Int?,
        sortType: SortType
    ): CocktailFilterFull {
        val comparator: Comparator<FullCocktailData> = when (sortType) {
            SortType.MOST_POPULAR -> {
                compareBy<FullCocktailData> { it.visitCount }.reversed()
            }
            SortType.BIGGEST_RATE -> {
                compareBy<FullCocktailData> { it.rating }.reversed()
            }
        }

        var result = cocktails.filterBySearch(searchParam).sortedWith(comparator)

        val count = result.count()

        if (offset != null) {
            if (offset > count) {
                throw OffsetToBig(listSize = count, offset = offset)
            }
            result = result.subList(offset, count)
        }

        if (limit != null) {
            result = result.subList(0, limit.coerceAtMost(result.size))
        }

        return CocktailFilterFull(result, count, futureCountCalculator.getFutureCounts(searchParam))
    }


}