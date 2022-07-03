package org.mixdrinks.view.v2.controllers.filter

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.view.cocktail.ItemType

class FilterSource {

    /**
     * Return the list of FilterGroup, and sort each filter list by count of cocktails.
     */
    fun getMetaInfo(): List<FilterModels.FilterGroup> {
        return transaction {
            val tags = TagsTable.selectAll().map { tagRow ->
                val tagId = tagRow[TagsTable.id]
                FilterModels.FilterItem(
                    id = FilterModels.FilterId(tagId),
                    name = tagRow[TagsTable.name],
                    cocktailCount = CocktailToTagTable.select { CocktailToTagTable.tagId eq tagId }.count()
                )
            }

            listOf(
                FilterModels.FilterGroup(
                    FilterModels.Filters.GOODS, getItemList(ItemType.GOOD).sortedBy { it.cocktailCount }.reversed()
                ),
                FilterModels.FilterGroup(FilterModels.Filters.TAGS, tags.sortedBy { it.cocktailCount }.reversed()),
                FilterModels.FilterGroup(
                    FilterModels.Filters.TOOLS, getItemList(ItemType.TOOL).sortedBy { it.cocktailCount }.reversed()
                ),
            )
        }
    }

    private fun getItemList(itemType: ItemType): List<FilterModels.FilterItem> {
        return ItemsTable.select { ItemsTable.relation eq itemType.relation }.map { itemRow ->
            val goodId = itemRow[ItemsTable.id]
            FilterModels.FilterItem(
                id = FilterModels.FilterId(goodId),
                name = itemRow[ItemsTable.name],
                cocktailCount = CocktailsToItemsTable.select { CocktailsToItemsTable.itemId eq goodId }.count()
            )
        }
    }
}
