package org.fullness

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.TastesTable
import org.mixdrinks.view.cocktail.ItemType

fun prepareData(
    cocktails: List<CocktailData>
) {
    transaction {
        SchemaUtils.drop(
            CocktailsTable,
            CocktailsToItemsTable,
            CocktailToTagTable,
            TagsTable,
            ItemsTable,
            TastesTable,
            TagsTable
        )
        SchemaUtils.create(
            CocktailsTable,
            CocktailsToItemsTable,
            CocktailToTagTable,
            TagsTable,
            ItemsTable,
            TastesTable,
            TagsTable
        )

        insertDependencies(cocktails)

        insertCocktails(cocktails)
    }
}

data class CocktailData(
    val id: Int,
    val tagIds: List<Int> = emptyList(),
    val goodIds: List<Int> = emptyList(),
    val toolIds: List<Int> = emptyList(),
)

private fun insertCocktails(cocktails: List<CocktailData>) {
    cocktails.forEach { cocktail ->
        CocktailsTable.insert {
            it[id] = cocktail.id
            it[name] = ""
            it[steps] = arrayOf()
            it[visitCount] = 0
            it[ratingCount] = 1
            it[ratingValue] = 4
        }

        cocktail.goodIds.forEach { newGoodId ->
            CocktailsToItemsTable.insert {
                it[cocktailId] = cocktail.id
                it[itemId] = newGoodId
                it[unit] = ""
                it[amount] = 10
                it[relation] = ItemType.GOOD.relation
            }
        }
        cocktail.toolIds.forEach { newToolIds ->
            CocktailsToItemsTable.insert {
                it[cocktailId] = cocktail.id
                it[itemId] = newToolIds
                it[unit] = ""
                it[amount] = 10
                it[relation] = ItemType.TOOL.relation
            }
        }
        cocktail.tagIds.forEach { newTagId ->
            CocktailToTagTable.insert {
                it[cocktailId] = cocktail.id
                it[tagId] = newTagId
            }
        }
    }
}

private fun insertDependencies(cocktails: List<CocktailData>) {
    cocktails.flatMap { it.tagIds }.distinct().forEach { tagId ->
        TagsTable.insert {
            it[id] = tagId
            it[name] = ""
        }
    }

    cocktails.flatMap { it.toolIds }.distinct().forEach { toolId ->
        ItemsTable.insert {
            it[id] = toolId
            it[name] = ""
            it[about] = ""
            it[visitCount] = 0
            it[relation] = ItemType.TOOL.relation
        }
    }

    cocktails.flatMap { it.goodIds }.distinct().forEach { goodId ->
        ItemsTable.insert {
            it[id] = goodId
            it[name] = ""
            it[about] = ""
            it[visitCount] = 0
            it[relation] = ItemType.GOOD.relation
        }
    }
}
