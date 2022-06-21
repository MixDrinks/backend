package org.mixdrinks.view.v2.filter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.view.cocktail.ItemType

class FilterSource {

    enum class Filters(val id: Int, val queryName: String, val translation: String) {
        TAGS(
            id = 0, queryName = "tags", translation = "Інше",
        ),
        GOODS(
            id = 1, queryName = "goods", translation = "Інгрідієнти"
        ),
        TOOLS(
            id = 2,
            queryName = "tools",
            translation = "Приладдя",
        ),
    }

    @Serializable
    data class FilterGroup(
        @SerialName("id") val id: Int,
        @SerialName("queryName") val queryName: String,
        @SerialName("name") val name: String,
        @SerialName("items") val items: List<FilterItem>,
    ) {
        constructor(filters: Filters, items: List<FilterItem>) : this(
            filters.id, filters.queryName, filters.translation, items
        )
    }

    @Serializable
    data class FilterItem(
        @SerialName("id") val id: Int,
        @SerialName("name") val name: String,
        @SerialName("cocktailCount") val cocktailCount: Long,
    )

    /**
     * Return the list of FilterGroup, and sort each filter list by count of cocktails.
     */
    fun getMetaInfo(): List<FilterGroup> {
        return transaction {
            val tags = TagsTable.selectAll().map { tagRow ->
                val tagId = tagRow[TagsTable.id]
                FilterItem(
                    id = tagId,
                    name = tagRow[TagsTable.name],
                    cocktailCount = CocktailToTagTable.select { CocktailToTagTable.tagId eq tagId }.count()
                )
            }

            listOf(
                FilterGroup(Filters.GOODS, getItemList(ItemType.GOOD).sortedBy { it.cocktailCount }.reversed()),
                FilterGroup(Filters.TOOLS, getItemList(ItemType.TOOL).sortedBy { it.cocktailCount }.reversed()),
                FilterGroup(Filters.TAGS, tags.sortedBy { it.cocktailCount }.reversed()),
            )
        }
    }

    private fun getItemList(itemType: ItemType): List<FilterItem> {
        return ItemsTable.select { ItemsTable.relation eq itemType.relation }.map { itemRow ->
            val goodId = itemRow[ItemsTable.id]
            FilterItem(
                id = goodId,
                name = itemRow[ItemsTable.name],
                cocktailCount = CocktailsToItemsTable.select { CocktailsToItemsTable.goodId eq goodId }.count()
            )
        }
    }
}
