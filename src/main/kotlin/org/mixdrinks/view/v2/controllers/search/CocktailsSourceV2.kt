package org.mixdrinks.view.v2.controllers.search

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.view.cocktail.ItemType
import org.mixdrinks.view.v2.controllers.filter.FilterModels

class CocktailsSourceV2 {

    @JvmInline
    value class CocktailId(val value: Int)

    private val cache: Map<CocktailId, Map<FilterModels.FilterGroupId, List<FilterModels.FilterId>>>

    val filterIds: Map<FilterModels.Filters, List<FilterModels.FilterId>> = transaction {
        return@transaction mapOf(
            FilterModels.Filters.TAGS to TagsTable.selectAll().map { FilterModels.FilterId(it[TagsTable.id]) },
            FilterModels.Filters.GOODS to ItemsTable.select { ItemsTable.relation eq ItemType.GOOD.relation }
                .map { FilterModels.FilterId(it[ItemsTable.id]) },
            FilterModels.Filters.TOOLS to ItemsTable.select { ItemsTable.relation eq ItemType.TOOL.relation }
                .map { FilterModels.FilterId(it[ItemsTable.id]) },
        )

    }

    init {
        cache = transaction {
            val cocktailIds =
                CocktailsTable.slice(CocktailsTable.id).selectAll().map { CocktailId(it[CocktailsTable.id]) }

            return@transaction cocktailIds.associateWith { cocktailId ->
                val tagIds = CocktailToTagTable.select { CocktailToTagTable.cocktailId eq cocktailId.value }
                    .map { FilterModels.FilterId(it[CocktailToTagTable.tagId]) }

                val goodIds = getItemIds(cocktailId, ItemType.GOOD)
                val toolIds = getItemIds(cocktailId, ItemType.TOOL)

                return@associateWith mapOf(
                    FilterModels.Filters.TAGS.id to tagIds,
                    FilterModels.Filters.GOODS.id to goodIds,
                    FilterModels.Filters.TOOLS.id to toolIds,
                )
            }
        }
    }

    private fun getItemIds(cocktailId: CocktailId, itemType: ItemType) =
        CocktailsToItemsTable.slice(CocktailsToItemsTable.cocktailId, CocktailsToItemsTable.goodId)
            .select { (CocktailsToItemsTable.cocktailId eq cocktailId.value) and (CocktailsToItemsTable.relation eq itemType.relation) }
            .map { FilterModels.FilterId(it[CocktailsToItemsTable.goodId]) }

    fun getCocktailsBySearch(searchParam: SearchParam): List<CocktailId> {
        return cache
            .filter { (_, meta) ->
                return@filter searchParam.filters.all { (groupId, filterIds) ->
                    meta[groupId]!!.containsAll(filterIds)
                }
            }
            .map { it.key }
    }
}
