package org.fullness

import org.createDataBase
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToGoodsTable
import org.mixdrinks.data.CocktailsToToolsTable
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.ToolsTable

fun prepareData(
    cocktails: List<CocktailData>
) {
    transaction {
        createDataBase()
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
            CocktailsToGoodsTable.insert {
                it[cocktailId] = cocktail.id
                it[goodId] = newGoodId
                it[unit] = ""
                it[amount] = 10
            }
        }
        cocktail.toolIds.forEach { newToolIds ->
            CocktailsToToolsTable.insert {
                it[cocktailId] = cocktail.id
                it[toolId] = newToolIds
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
        ToolsTable.insert {
            it[id] = toolId
            it[name] = ""
            it[about] = ""
            it[visitCount] = 0
        }
    }

    cocktails.flatMap { it.goodIds }.distinct().forEach { goodId ->
        GoodsTable.insert {
            it[id] = goodId
            it[name] = ""
            it[about] = ""
            it[visitCount] = 0
        }
    }
}
