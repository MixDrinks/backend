package org.mixdrinks.view.v2.controllers.filter

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.AlcoholVolumesTable
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsToAlcoholVolumesTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.CocktailsToTastesTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.TastesTable
import org.mixdrinks.view.cocktail.ItemType

class FilterSource {

    /**
     * Return the list of FilterGroup, and sort each filter list by count of cocktails.
     */
    fun getMetaInfo(): List<FilterModels.FilterGroup> {
        return transaction {
            val alcoholVolume = AlcoholVolumesTable.selectAll().map { alcoholVolume ->
                val alcoholVolumeId = alcoholVolume[AlcoholVolumesTable.id].value
                FilterModels.FilterItem(
                    id = FilterModels.FilterId(alcoholVolumeId),
                    name = alcoholVolume[AlcoholVolumesTable.name],
                    cocktailCount = CocktailsToAlcoholVolumesTable
                        .select { CocktailsToAlcoholVolumesTable.alcoholVolumeId eq alcoholVolumeId }
                        .count(),
                )
            }
            val tags = TagsTable.selectAll().map { tagRow ->
                val tagId = tagRow[TagsTable.id].value
                FilterModels.FilterItem(
                    id = FilterModels.FilterId(tagId),
                    name = tagRow[TagsTable.name],
                    cocktailCount = CocktailToTagTable.select { CocktailToTagTable.tagId eq tagId }.count()
                )
            }

            val tastes = TastesTable.selectAll().map { tasteRow ->
                val tasteId = tasteRow[TastesTable.id].value
                FilterModels.FilterItem(
                    id = FilterModels.FilterId(tasteId),
                    name = tasteRow[TastesTable.name],
                    cocktailCount = CocktailsToTastesTable.select { CocktailsToTastesTable.tasteId eq tasteId }.count()
                )
            }

            listOf(
                FilterModels.FilterGroup(
                    FilterModels.Filters.ALCOHOL_VOLUME, alcoholVolume.sortedBy { it.cocktailCount }.reversed()
                ),
                FilterModels.FilterGroup(
                    FilterModels.Filters.TASTE, tastes.sortedBy { it.cocktailCount }.reversed()
                ),
                FilterModels.FilterGroup(
                    FilterModels.Filters.GOODS, getItemList(ItemType.GOOD).sortedBy { it.cocktailCount }.reversed()
                ),
                FilterModels.FilterGroup(
                    FilterModels.Filters.TAGS, tags.sortedBy { it.cocktailCount }.reversed()
                ),
                FilterModels.FilterGroup(
                    FilterModels.Filters.TOOLS, getItemList(ItemType.TOOL).sortedBy { it.cocktailCount }.reversed()
                )
            )
        }
    }

    private fun getItemList(itemType: ItemType): List<FilterModels.FilterItem> {
        return ItemsTable.select { ItemsTable.relation eq itemType.relation }.map { itemRow ->
            val goodId = itemRow[ItemsTable.id].value
            FilterModels.FilterItem(
                id = FilterModels.FilterId(goodId),
                name = itemRow[ItemsTable.name],
                cocktailCount = CocktailsToItemsTable.select { CocktailsToItemsTable.itemId eq goodId }.count()
            )
        }
    }
}
