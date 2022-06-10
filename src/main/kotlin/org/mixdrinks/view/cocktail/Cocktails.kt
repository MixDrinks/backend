package org.mixdrinks.view.cocktail

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.*
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages
import org.mixdrinks.view.tag.TagVM

const val DEFAULT_PAGE_SIZE = 10

fun Application.cocktails() {
    val filter = Filter()
    routing {
        get("cocktails/all") {
            call.respond(transaction {
                CocktailsTable.selectAll().map { row ->
                    SimpleCocktailVM(
                        row[CocktailsTable.id],
                        row[CocktailsTable.name],
                    )
                }
            })
        }
        filter.filter(this)
        get("cocktails/full") {
            val id = call.request.queryParameters["id"]?.toIntOrNull()

            if (id != null) {
                call.respond(getFullCocktail(id))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Query id is require, and must be integer")
            }
        }
    }
}

private fun getFullCocktail(id: Int): FullCocktailVM {
    return transaction {
        val cocktail = CocktailsTable.select { CocktailsTable.id eq id }.first()

        val cocktailId = cocktail[CocktailsTable.id]

        return@transaction FullCocktailVM(
            id = cocktailId,
            name = cocktail[CocktailsTable.name],
            images = buildImages(cocktailId, ImageType.COCKTAIL),
            receipt = cocktail[CocktailsTable.steps].toList(),
            goods = getFullIngredients(cocktailId, ItemType.GOOD),
            tools = getFullIngredients(cocktailId, ItemType.TOOL),
            tags = getCocktailTags(cocktailId),
        )
    }
}

private fun getCocktailTags(id: Int) =
    CocktailToTagTable.join(TagsTable, JoinType.INNER, TagsTable.id, CocktailToTagTable.tagId)
        .select { CocktailToTagTable.cocktailId eq id }
        .map { tagRow ->
            TagVM(
                tagRow[TagsTable.id],
                tagRow[TagsTable.name]
            )
        }

private fun getFullIngredients(id: Int, relation: ItemType): List<FullIngredient> {
    return CocktailsToItemsTable.join(ItemsTable, JoinType.INNER, ItemsTable.id, CocktailsToItemsTable.goodId)
        .select { CocktailsToItemsTable.cocktailId eq id and (CocktailsToItemsTable.relation eq relation.relation) }
        .map { itemRow ->
            FullIngredient(
                id = itemRow[ItemsTable.id],
                name = itemRow[ItemsTable.name],
                images = buildImages(itemRow[ItemsTable.id], ImageType.ITEM),
                amount = itemRow[CocktailsToItemsTable.amount],
                unit = itemRow[CocktailsToItemsTable.unit],
            )
        }
}


enum class ItemType(val relation: Int) {
    GOOD(1), TOOL(2)
}

