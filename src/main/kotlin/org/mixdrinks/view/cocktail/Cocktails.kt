package org.mixdrinks.view.cocktail

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Cocktail
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToGoodsTable
import org.mixdrinks.data.CocktailsToTastesTable
import org.mixdrinks.data.FullCocktail
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.TastesTable
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
        return@transaction FullCocktail.findById(id)?.load(FullCocktail::goods, FullCocktail::glassware)
            ?.let { cocktail ->
                FullCocktailVM(
                    id = cocktail.id.value,
                    name = cocktail.name,
                    visitCount = cocktail.visitCount,
                    rating = cocktail.ratting,
                    ratingCount = cocktail.ratingCount,
                    images = buildImages(cocktail.id.value, ImageType.COCKTAIL),
                    receipt = CocktailsTable.select { CocktailsTable.id eq id }.first()[CocktailsTable.steps].toList(),
                    goods = getFullIngredients(cocktail.id.value),
                    tools = getFullTools(cocktail),
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
    return CocktailsToGoodsTable.join(GoodsTable, JoinType.INNER, GoodsTable.id, CocktailsToGoodsTable.goodId)
        .select { CocktailsToGoodsTable.cocktailId eq id }
        .map { itemRow ->
            FullIngredient(
                id = itemRow[GoodsTable.id].value,
                name = itemRow[GoodsTable.name],
                images = buildImages(itemRow[GoodsTable.id].value, ImageType.ITEM),
                amount = itemRow[CocktailsToGoodsTable.amount],
                unit = itemRow[CocktailsToGoodsTable.unit],
            )
        }
}

private fun getFullTools(cocktail: FullCocktail): List<FullTool> {
    return buildList {
        cocktail.glassware.first().let {
            add(
                FullTool(
                    id = it.id.value,
                    name = it.name,
                    images = buildImages(it.id.value, ImageType.ITEM),
                )
            )
        }

        addAll(cocktail.tools
            .map {
                FullTool(
                    id = it.id.value,
                    name = it.name,
                    images = buildImages(it.id.value, ImageType.ITEM),
                )
            }
        )
    }.distinctBy { it.id }
}
