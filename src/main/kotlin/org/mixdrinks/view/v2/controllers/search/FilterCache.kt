package org.mixdrinks.view.v2.controllers.search

import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.AlcoholVolumes
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.Good
import org.mixdrinks.data.Tag
import org.mixdrinks.data.Taste
import org.mixdrinks.data.Tool
import org.mixdrinks.domain.Filter
import org.mixdrinks.domain.FilterGroup
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.view.v2.controllers.filter.FilterModels

class FilterCache {

    data class FullFilter(
        val id: FilterId,
        val name: String,
        val cocktailIds: Set<CocktailId>,
    )

    val fullFilters: Map<FilterModels.Filters, List<FullFilter>> = transaction {
        return@transaction mapOf(
            FilterModels.Filters.TAGS to Tag.all().with(Tag::cocktails)
                .map(::toFullFilter),
            FilterModels.Filters.GOODS to Good.all().with(Good::cocktails)
                .map(::toFullFilter),
            FilterModels.Filters.TOOLS to Tool.all().with(Tool::cocktails)
                .map(::toFullFilter),
            FilterModels.Filters.TASTE to Taste.all().with(Taste::cocktails)
                .map(::toFullFilter),
            FilterModels.Filters.ALCOHOL_VOLUME to AlcoholVolumes.all().with(AlcoholVolumes::cocktails)
                .map(::toFullFilter),
            FilterModels.Filters.GLASSWARE to Glassware.all().with(Glassware::cocktail)
                .map(::toFullFilter),
        )
    }

    val filterIds: Map<FilterModels.Filters, List<FilterId>> = fullFilters.mapValues { (_, filters) ->
        filters.map { it.id }
    }

    val filterGroups: List<FilterGroup> = fullFilters.map { (filter, filters) ->
        FilterGroup(
            id = filter.id,
            name = filter.name,
            filters = filters.map { Filter(it.id, it.cocktailIds) },
        )
    }

    private fun toFullFilter(tag: Tag) = FullFilter(
        id = FilterId(tag.id.value),
        name = tag.name,
        cocktailIds = tag.cocktails.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
    )

    private fun toFullFilter(good: Good) = FullFilter(
        id = FilterId(good.id.value),
        name = good.name,
        cocktailIds = good.cocktails.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
    )

    private fun toFullFilter(tool: Tool) = FullFilter(
        id = FilterId(tool.id.value),
        name = tool.name,
        cocktailIds = tool.cocktails.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
    )

    private fun toFullFilter(taste: Taste) = FullFilter(
        id = FilterId(taste.id.value),
        name = taste.name,
        cocktailIds = taste.cocktails.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
    )

    private fun toFullFilter(alcoholVolumes: AlcoholVolumes) = FullFilter(
        id = FilterId(alcoholVolumes.id.value),
        name = alcoholVolumes.name,
        cocktailIds = alcoholVolumes.cocktails.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
    )

    private fun toFullFilter(glassware: Glassware) = FullFilter(
        id = FilterId(glassware.id.value),
        name = glassware.name,
        cocktailIds = glassware.cocktail.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
    )
}
