package org.mixdrinks.view.v2.controllers.filter

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.AlcoholVolumesTable
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsToAlcoholVolumesTable
import org.mixdrinks.data.CocktailsToGlasswareTable
import org.mixdrinks.data.CocktailsToGoodsTable
import org.mixdrinks.data.CocktailsToTastesTable
import org.mixdrinks.data.CocktailsToToolsTable
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.Good
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.TastesTable
import org.mixdrinks.data.ToolsTable

class FilterSource {

    /**
     * Return the list of FilterGroup, and sort each filter list by count of cocktails.
     */
    fun getMetaInfo(): List<FilterModels.FilterGroup> {
        return transaction {
            val alcoholVolume = getAlcoholVolume()
            val tags = getTags()
            val tastes = getTastes()
            val tools = getTools()
            val goods = getGoods()

            listOf(
                FilterModels.FilterGroup(
                    FilterModels.Filters.ALCOHOL_VOLUME, alcoholVolume.sortedBy { it.cocktailCount }.reversed()
                ),
                FilterModels.FilterGroup(
                    FilterModels.Filters.TASTE, tastes.sortedBy { it.cocktailCount }.reversed()
                ),
                FilterModels.FilterGroup(
                    FilterModels.Filters.GLASSWARE, getGlassware().sortedBy { it.cocktailCount }.reversed()
                ),
                FilterModels.FilterGroup(
                    FilterModels.Filters.GOODS, goods.sortedBy { it.cocktailCount }.reversed()
                ),
                FilterModels.FilterGroup(
                    FilterModels.Filters.TAGS, tags.sortedBy { it.cocktailCount }.reversed()
                ),
                FilterModels.FilterGroup(
                    FilterModels.Filters.TOOLS, tools.sortedBy { it.cocktailCount }.reversed()
                )
            )
        }
    }

    private fun getGlassware() = Glassware.all().map {
        FilterModels.FilterItem(
            id = FilterModels.FilterId(it.id.value),
            name = it.name,
            cocktailCount = CocktailsToGlasswareTable.select { CocktailsToGlasswareTable.glasswareId eq it.id.value }
                .count()
        )
    }

    private fun getGoods() = Good.all().map {
        FilterModels.FilterItem(
            id = FilterModels.FilterId(it.id.value),
            name = it.name,
            cocktailCount = CocktailsToGoodsTable.select { CocktailsToGoodsTable.goodId eq it.id.value }.count()
        )
    }

    private fun getTools() = ToolsTable.selectAll().map { toolRow ->
        val toolId = toolRow[ToolsTable.id].value
        FilterModels.FilterItem(
            id = FilterModels.FilterId(toolId),
            name = toolRow[ToolsTable.name],
            cocktailCount = CocktailsToToolsTable.select { CocktailsToToolsTable.toolId eq toolId }.count()
        )
    }

    private fun getTastes() = TastesTable.selectAll().map { tasteRow ->
        val tasteId = tasteRow[TastesTable.id].value
        FilterModels.FilterItem(
            id = FilterModels.FilterId(tasteId),
            name = tasteRow[TastesTable.name],
            cocktailCount = CocktailsToTastesTable.select { CocktailsToTastesTable.tasteId eq tasteId }.count()
        )
    }

    private fun getTags() = TagsTable.selectAll().map { tagRow ->
        val tagId = tagRow[TagsTable.id].value
        FilterModels.FilterItem(
            id = FilterModels.FilterId(tagId),
            name = tagRow[TagsTable.name],
            cocktailCount = CocktailToTagTable.select { CocktailToTagTable.tagId eq tagId }.count()
        )
    }

    private fun getAlcoholVolume() = AlcoholVolumesTable.selectAll().map { alcoholVolume ->
        val alcoholVolumeId = alcoholVolume[AlcoholVolumesTable.id].value
        FilterModels.FilterItem(
            id = FilterModels.FilterId(alcoholVolumeId),
            name = alcoholVolume[AlcoholVolumesTable.name],
            cocktailCount = CocktailsToAlcoholVolumesTable
                .select { CocktailsToAlcoholVolumesTable.alcoholVolumeId eq alcoholVolumeId }
                .count(),
        )
    }
}
