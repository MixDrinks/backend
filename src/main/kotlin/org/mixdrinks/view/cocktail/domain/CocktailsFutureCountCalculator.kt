package org.mixdrinks.view.cocktail.domain

import org.mixdrinks.view.cocktail.data.CocktailsSource

data class FutureCocktailsCounts(
    val tagCounts: Map<Int, Int>,
    val goodCounts: Map<Int, Int>,
    val toolCounts: Map<Int, Int>,
)

class CocktailsFutureCountCalculator(private val cocktailsSource: CocktailsSource) {

    fun getFutureCounts(existSearchParam: CocktailsFilterSearchParam): FutureCocktailsCounts {

        val tagsMap = cocktailsSource.allTagIds.associateWith {
            cocktailsSource.cocktails.filterBySearch(
                existSearchParam.copy(
                    tags = existSearchParam.tags.orEmpty().plus(it)
                )
            ).count()
        }

        val goodsMap = cocktailsSource.allGoodIds.associateWith {
            cocktailsSource.cocktails.filterBySearch(
                existSearchParam.copy(
                    goods = existSearchParam.goods.orEmpty().plus(it)
                )
            ).count()
        }

        val toolsMap = cocktailsSource.allToolIds.associateWith {
            cocktailsSource.cocktails.filterBySearch(
                existSearchParam.copy(
                    tools = existSearchParam.tools.orEmpty().plus(it)
                )
            ).count()
        }

        return FutureCocktailsCounts(tagsMap, goodsMap, toolsMap)
    }
}
