package org.mixdrinks.view.cocktail.domain

import org.mixdrinks.view.cocktail.data.FullCocktailData


fun List<FullCocktailData>.filterBySearch(searchParam: CocktailsFilterSearchParam): List<FullCocktailData> {
    var result = this.asSequence()

    if (searchParam.search != null) {
        result = result.filter { it.name.contains(searchParam.search, ignoreCase = true) }
    }

    if (searchParam.tags != null) {
        result = result.filter { it.tagIds.containsAll(searchParam.tags) }
    }

    if (searchParam.goods != null) {
        result = result.filter { it.goodIds.containsAll(searchParam.goods) }
    }

    if (searchParam.tools != null) {
        result = result.filter { it.toolIds.containsAll(searchParam.tools) }
    }

    return result.toList()
}
