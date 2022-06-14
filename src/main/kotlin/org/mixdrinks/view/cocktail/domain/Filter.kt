package org.mixdrinks.view.cocktail.domain

import org.mixdrinks.view.cocktail.CocktailFilter
import org.mixdrinks.view.cocktail.SortType
import org.mixdrinks.view.error.OffsetToBig

data class CocktailFilterFull(
    val list: List<CocktailFilter>,
    val totalCount: Int,
    val tagMaps: Map<Int, Int>,
    val goodMaps: Map<Int, Int>,
    val toolMaps: Map<Int, Int>,
)

fun filterCocktails(
    cocktails: List<CocktailFilter>,
    search: String?,
    tags: List<Int>?,
    goods: List<Int>?,
    tools: List<Int>?,
    offset: Int?,
    limit: Int?,
    sortType: SortType,
    allTags: List<Int> = emptyList()
): CocktailFilterFull {
    val comparator: Comparator<CocktailFilter> = when (sortType) {
        SortType.MOST_POPULAR -> {
            compareBy<CocktailFilter> { it.visitCount }.reversed()
        }
        SortType.BIGGEST_RATE -> {
            compareBy<CocktailFilter> { it.rating }.reversed()
        }
    }
    var result = commonFilter(cocktails, search, tags, goods, tools)
        .sortedWith(comparator)

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

    val tagsMap = allTags.associateWith {
        commonFilter(cocktails, search, tags.orEmpty().plus(it), goods, tools).count()
    }

    val goodsMap = allTags.associateWith {
        commonFilter(cocktails, search, tags, goods.orEmpty().plus(it), tools).count()
    }

    val toolsMap = allTags.associateWith {
        commonFilter(cocktails, search, tags, goods, tools.orEmpty().plus(it)).count()
    }

    return CocktailFilterFull(result, count, tagsMap, goodsMap, toolsMap)
}

private fun commonFilter(
    cocktails: List<CocktailFilter>,
    search: String?,
    tags: List<Int>?,
    goods: List<Int>?,
    tools: List<Int>?,
): List<CocktailFilter> {
    var result = cocktails.asSequence()

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

    return result.toList()
}