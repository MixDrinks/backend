package org.mixdrinks.view.cocktail.domain

import org.mixdrinks.view.cocktail.CocktailFilter
import org.mixdrinks.view.cocktail.OffsetToBig

data class CocktailFilterFull(
    val list: List<CocktailFilter>,
    val totalCount: Int,
)

fun filterCocktails(
    cocktails: List<CocktailFilter>,
    search: String?,
    tags: List<Int>?,
    goods: List<Int>?,
    tools: List<Int>?,
    offset: Int?,
    limit: Int?,
): CocktailFilterFull {
    var result = cocktails

    if (search != null) {
        result = result.filter { it.name.contains(search, ignoreCase = true) }
    }

    if (tags != null) {
        result = result.filter { it.tagIds.containsAll(tags) }
    }

    if (goods != null) {
        result = result.filter { it.goodIds.containsAll(goods) }
    }

    if (tools != null) {
        result = result.filter { it.toolIds.containsAll(tools) }
    }

    val count = result.count()

    if (offset != null) {
        if (offset > result.size) {
            throw OffsetToBig(listSize = result.size, offset = offset)
        }
        result = result.subList(offset, result.size)
    }

    if (limit != null) {
        result = result.subList(0, limit.coerceAtMost(result.size))
    }

    return CocktailFilterFull(result, count)
}