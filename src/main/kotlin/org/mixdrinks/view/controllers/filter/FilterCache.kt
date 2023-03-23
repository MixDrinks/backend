package org.mixdrinks.view.controllers.filter

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

class FilterCache {

    data class FullFilter(
        val id: FilterId,
        val name: String,
        val cocktailIds: Set<CocktailId>,
        val slug: String,
    )

    val fullFilterGroupBackend: Map<FilterModels.FilterGroupBackend, List<FullFilter>> = transaction {
        return@transaction mapOf(
            FilterModels.FilterGroupBackend.ALCOHOL_VOLUME to AlcoholVolumes.all().with(AlcoholVolumes::cocktails)
                .map(::toFullFilter),
            FilterModels.FilterGroupBackend.TASTE to Taste.all().with(Taste::cocktails)
                .map(::toFullFilter),
            FilterModels.FilterGroupBackend.GLASSWARE to Glassware.all().with(Glassware::cocktail)
                .map(::toFullFilter),
            FilterModels.FilterGroupBackend.GOODS to Good.all().with(Good::cocktails)
                .map(::toFullFilter),
            FilterModels.FilterGroupBackend.TAGS to Tag.all().with(Tag::cocktails)
                .map(::toFullFilter),
            FilterModels.FilterGroupBackend.TOOLS to Tool.all().with(Tool::cocktails)
                .map(::toFullFilter),
        )
    }

    val filterIds: Map<FilterModels.FilterGroupBackend, List<FilterId>> =
        fullFilterGroupBackend.mapValues { (_, filters) ->
            filters.map { it.id }
        }

    val filterGroups: List<FilterGroup> = fullFilterGroupBackend.map { (filter, filters) ->
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
        slug = tag.slug,
    )

    private fun toFullFilter(good: Good) = FullFilter(
        id = FilterId(good.id.value),
        name = good.name,
        cocktailIds = good.cocktails.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
        slug = good.slug,
    )

    private fun toFullFilter(tool: Tool) = FullFilter(
        id = FilterId(tool.id.value),
        name = tool.name,
        cocktailIds = tool.cocktails.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
        slug = tool.slug,
    )

    private fun toFullFilter(taste: Taste) = FullFilter(
        id = FilterId(taste.id.value),
        name = taste.name,
        cocktailIds = taste.cocktails.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
        slug = taste.slug,
    )

    private fun toFullFilter(alcoholVolumes: AlcoholVolumes) = FullFilter(
        id = FilterId(alcoholVolumes.id.value),
        name = alcoholVolumes.name,
        cocktailIds = alcoholVolumes.cocktails.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
        slug = alcoholVolumes.slug,
    )

    private fun toFullFilter(glassware: Glassware) = FullFilter(
        id = FilterId(glassware.id.value),
        name = glassware.name,
        cocktailIds = glassware.cocktail.map { cocktail -> CocktailId(cocktail.id.value) }.toSet(),
        slug = glassware.slug,
    )
}
