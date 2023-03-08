package org.mixdrinks.view.cocktail

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Cocktail
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.CocktailsToTastesTable
import org.mixdrinks.data.CocktailsToToolsTable
import org.mixdrinks.data.FullCocktail
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.TastesTable
import org.mixdrinks.data.ToolsTable
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages
import org.mixdrinks.view.v2.data.CocktailId
import org.mixdrinks.view.v2.data.TagId

fun Application.cocktails() {
    routing {
        get("cocktails/all") {
            call.respond(transaction {
                Cocktail.all().map {
                    SimpleCocktailVM(
                        CocktailId(it.id.value),
                        it.name
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
        return@transaction FullCocktail.findById(id)?.let { cocktail ->
            FullCocktailVM(
                id = cocktail.id.value,
                name = cocktail.name,
                visitCount = cocktail.visitCount,
                rating = cocktail.ratting,
                ratingCount = cocktail.ratingCount,
                images = buildImages(cocktail.id.value, ImageType.COCKTAIL),
                receipt = CocktailsTable.select { CocktailsTable.id eq id }.first()[CocktailsTable.steps].toList(),
                goods = getFullIngredients(cocktail.id.value),
                tools = getFullTools(cocktail.id.value),
                tags = getCocktailTags(cocktail.id.value),
                tastes = getTastes(cocktail.id.value),
            )
        } ?: throw NotFoundException("Cocktail with id $id not found")
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

private fun getFullIngredients(id: Int): List<FullIngredient> {
    return CocktailsToItemsTable.join(ItemsTable, JoinType.INNER, ItemsTable.id, CocktailsToItemsTable.itemId)
        .select {
            CocktailsToItemsTable.cocktailId eq id and
                    (CocktailsToItemsTable.relation eq ItemType.GOOD.relation)
        }
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

private fun getFullTools(id: Int): List<FullIngredient> {
    return CocktailsToToolsTable.join(ToolsTable, JoinType.INNER, ToolsTable.id, CocktailsToToolsTable.toolId)
        .select { CocktailsToToolsTable.cocktailId eq id }
        .map { itemRow ->
            FullIngredient(
                id = itemRow[ToolsTable.id].value,
                name = itemRow[ToolsTable.name],
                images = buildImages(itemRow[ToolsTable.id].value, ImageType.ITEM),
                amount = 0,
                unit = "",
            )
        }
}


enum class ItemType(val relation: Int) {
    GOOD(1)
}
