package org.mixdrinks.view.cocktail.domain

import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.view.cocktail.CompactCocktailVM
import org.mixdrinks.view.cocktail.FilterFutureCounts
import org.mixdrinks.view.cocktail.FilterResultVMV2
import org.mixdrinks.view.cocktail.data.CocktailsSource
import org.mixdrinks.view.cocktail.data.FullCocktailData
import org.mixdrinks.view.error.OffsetToBig
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

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
    ): FilterResultVMV2 {
        return transaction {
            val result = filterCocktails(
                cocktailsSource.cocktails,
                searchParam,
                offset,
                limit,
                sortType,
            )

            val resultCocktails = result.list.map { cocktailFilter ->
                CompactCocktailVM(
                    cocktailFilter.id,
                    cocktailFilter.name,
                    cocktailFilter.rating,
                    cocktailFilter.visitCount,
                    buildImages(cocktailFilter.id, ImageType.COCKTAIL),
                )
            }

            FilterResultVMV2(
                result.totalCount,
                resultCocktails,
                FilterFutureCounts(result.counts.tagCounts, result.counts.goodCounts, result.counts.toolCounts)
            )
        }
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