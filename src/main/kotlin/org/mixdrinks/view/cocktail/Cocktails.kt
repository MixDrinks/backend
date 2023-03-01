package org.mixdrinks.view.cocktail

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.CocktailsToTastesTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.TastesTable
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages
import org.mixdrinks.view.rating.getRating
import org.mixdrinks.view.v2.data.TagId

fun Application.cocktails() {
    routing {
        get("cocktails/all") {
            call.respond(transaction {
                CocktailsTable.selectAll().map { row ->
                    SimpleCocktailVM(
                        row[CocktailsTable.id].value,
                        row[CocktailsTable.name],
                    )
                }
            })
        }
        get("v2/cocktails/full") {
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

        val cocktailId = cocktail[CocktailsTable.id].value

        val rating: Float? = cocktail.getRating()

        return@transaction FullCocktailVM(
            id = cocktailId,
            name = cocktail[CocktailsTable.name],
            visitCount = cocktail[CocktailsTable.visitCount],
            rating = rating,
            ratingCount = cocktail[CocktailsTable.ratingCount],
            images = buildImages(cocktailId, ImageType.COCKTAIL),
            receipt = cocktail[CocktailsTable.steps].toList(),
            goods = getFullIngredients(cocktailId, ItemType.GOOD),
            tools = getFullIngredients(cocktailId, ItemType.TOOL),
            tags = getCocktailTags(cocktailId),
            tastes = getTastes(cocktailId),
        )
    }
}

private fun getTastes(id: Int) =
    CocktailsToTastesTable.join(TastesTable, JoinType.INNER, TastesTable.id, CocktailsToTastesTable.tasteId)
        .select { CocktailsToTastesTable.cocktailId eq id }.map { tasteRow ->
            TagVM(
                TagId(tasteRow[TastesTable.id].value), tasteRow[TastesTable.name]
            )
        }

private fun getCocktailTags(id: Int) =
    CocktailToTagTable.join(TagsTable, JoinType.INNER, TagsTable.id, CocktailToTagTable.tagId)
        .select { CocktailToTagTable.cocktailId eq id }.map { tagRow ->
            TagVM(
                TagId(tagRow[TagsTable.id].value), tagRow[TagsTable.name]
            )
        }

private fun getFullIngredients(id: Int, relation: ItemType): List<FullIngredient> {
    return CocktailsToItemsTable.join(ItemsTable, JoinType.INNER, ItemsTable.id, CocktailsToItemsTable.itemId)
        .select { CocktailsToItemsTable.cocktailId eq id and (CocktailsToItemsTable.relation eq relation.relation) }
        .map { itemRow ->
            FullIngredient(
                id = itemRow[ItemsTable.id].value,
                name = itemRow[ItemsTable.name],
                images = buildImages(itemRow[ItemsTable.id].value, ImageType.ITEM),
                amount = itemRow[CocktailsToItemsTable.amount],
                unit = itemRow[CocktailsToItemsTable.unit],
            )
        }
}


enum class ItemType(val relation: Int) {
    GOOD(1), TOOL(2)
}
