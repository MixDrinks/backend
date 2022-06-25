package org.mixdrinks.view.cocktail.data

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
import org.mixdrinks.view.rating.getRating

data class FullCocktailData(
    val id: Int,
    val name: String,
    val rating: Float?,
    val visitCount: Int,
    val goodIds: List<Int>,
    val toolIds: List<Int>,
    val tagIds: List<Int>,
)

/**
 * The in memory snapshot of database. Provide full data for quick read and combine information about cocktails.
 */
class CocktailsSource {

    val cocktails: List<FullCocktailData>

    val allTagIds: List<Int>
    val allGoodIds: List<Int>
    val allToolIds: List<Int>

    init {
        println("Start create filter")
        cocktails = transaction {
            return@transaction CocktailsTable.slice(
                CocktailsTable.id,
                CocktailsTable.name,
                CocktailsTable.ratingCount,
                CocktailsTable.ratingValue,
                CocktailsTable.visitCount
            ).selectAll().map { cocktailRow ->
                val cocktailId = cocktailRow[CocktailsTable.id]

                val goodIds = CocktailsToItemsTable.slice(CocktailsToItemsTable.goodId).select {
                        CocktailsToItemsTable.cocktailId eq cocktailId and (CocktailsToItemsTable.relation eq ItemType.GOOD.relation)
                    }.map { it[CocktailsToItemsTable.goodId] }

                val toolIds = CocktailsToItemsTable.slice(CocktailsToItemsTable.goodId).select {
                        CocktailsToItemsTable.cocktailId eq cocktailId and (CocktailsToItemsTable.relation eq ItemType.TOOL.relation)
                    }.map { it[CocktailsToItemsTable.goodId] }

                val tagIds = CocktailToTagTable.slice(CocktailToTagTable.tagId)
                    .select { CocktailToTagTable.cocktailId eq cocktailId }.map { it[CocktailToTagTable.tagId] }

                return@map FullCocktailData(
                    id = cocktailId,
                    name = cocktailRow[CocktailsTable.name],
                    rating = cocktailRow.getRating(),
                    visitCount = cocktailRow[CocktailsTable.visitCount],
                    goodIds = goodIds,
                    toolIds = toolIds,
                    tagIds = tagIds,
                )
            }
        }

        allTagIds = transaction {
            TagsTable.slice(TagsTable.id).selectAll().orderBy(TagsTable.id).map { it[TagsTable.id] }
        }
        allGoodIds = transaction {
            ItemsTable.slice(ItemsTable.id).select { ItemsTable.relation eq ItemType.GOOD.relation }
                .orderBy(ItemsTable.id).map { it[ItemsTable.id] }
        }

        allToolIds = transaction {
            ItemsTable.slice(ItemsTable.id).select { ItemsTable.relation eq ItemType.TOOL.relation }
                .orderBy(ItemsTable.id).map { it[ItemsTable.id] }
        }

        println("End create filter")
    }
}
