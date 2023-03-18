package org.mixdrinks.view.cocktail

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Cocktail
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToGoodsTable
import org.mixdrinks.data.FullCocktail
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages
import org.mixdrinks.view.v2.controllers.filter.FilterModels

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
                id = CocktailId(cocktail.id.value),
                name = cocktail.name,
                visitCount = cocktail.visitCount,
                rating = cocktail.ratting,
                ratingCount = cocktail.ratingCount,
                images = buildImages(cocktail.id.value, ImageType.COCKTAIL),
                receipt = CocktailsTable.select { CocktailsTable.id eq id }.first()[CocktailsTable.steps].toList(),
                goods = getCocktailGoods(cocktail),
                tools = getFullTools(cocktail),
                tags = getCocktailTags(cocktail),// + getTastes(cocktail),
            )
        } ?: throw NotFoundException("Cocktail with id $id not found")
    }
}

@Suppress("UnusedPrivateMember")
private fun getTastes(cocktail: FullCocktail): List<TagVM> {
    return cocktail.tastes.map {
        return@map buildTagVM(it.id, it.name, FilterModels.Filters.TASTE)
    }
}

private fun getCocktailTags(cocktail: FullCocktail): List<TagVM> {
    return cocktail.tags.map {
        return@map buildTagVM(it.id, it.name, FilterModels.Filters.TAGS)
    }
}

private fun buildTagVM(id: EntityID<Int>, name: String, filter: FilterModels.Filters): TagVM {
    return TagVM(
        id = TagId(id.value),
        name = name,
        path = "${filter.queryName.value}/${id.value}",
    )
}

private fun getCocktailGoods(cocktail: FullCocktail): List<FullGood> {
    return CocktailsToGoodsTable.join(GoodsTable, JoinType.INNER, GoodsTable.id, CocktailsToGoodsTable.goodId)
        .select { CocktailsToGoodsTable.cocktailId eq cocktail.id.value }
        .map { itemRow ->
            val id = itemRow[GoodsTable.id].value
            FullGood(
                id = id,
                name = itemRow[GoodsTable.name],
                images = buildImages(itemRow[GoodsTable.id].value, ImageType.ITEM),
                amount = itemRow[CocktailsToGoodsTable.amount],
                unit = itemRow[CocktailsToGoodsTable.unit],
                path = "${FilterModels.Filters.GOODS.queryName.value}/${itemRow[GoodsTable.id].value}",
            )
        }
}

private fun getFullTools(cocktail: FullCocktail): List<ToolVM> {
    return buildList {
        cocktail.glassware.first().let {
            add(buildToolVM(it.id, it.name, FilterModels.Filters.GLASSWARE))
        }
        addAll(cocktail.tools
            .map { buildToolVM(it.id, it.name, FilterModels.Filters.TOOLS) }
        )
    }
}

private fun buildToolVM(id: EntityID<Int>, name: String, filter: FilterModels.Filters): ToolVM {
    return ToolVM(
        id = id.value,
        name = name,
        path = "${filter.queryName.value}/${id.value}",
        images = buildImages(id.value, ImageType.ITEM),
    )
}
