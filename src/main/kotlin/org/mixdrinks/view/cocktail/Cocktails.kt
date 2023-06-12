@file:Suppress("TooManyFunctions")

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
import org.mixdrinks.view.controllers.filter.FilterModels
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

fun Application.cocktails() {
    routing {
        /**
         * Return the list of all cocktails
         */
        get("cocktails/all") {
            call.respond(transaction {
                Cocktail.all().map {
                    SimpleCocktailVM(
                        CocktailId(it.id.value),
                        it.name,
                        it.slug,
                    )
                }
            })
        }

        get("v2/cocktail/{slug}") {
            val slug = call.parameters["slug"]

            if (slug != null) {
                call.respond(getFullCocktail(slug))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Query slug is require")
            }
        }

        /**
         * Return the full cocktail info based on id
         */
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

private fun getFullCocktail(slug: String): FullCocktailV2VM {
    return transaction {
        return@transaction FullCocktail.find { CocktailsTable.slug eq slug }.firstOrNull()?.let { cocktail ->
            FullCocktailV2VM(
                id = CocktailId(cocktail.id.value),
                name = cocktail.name,
                visitCount = cocktail.visitCount,
                rating = cocktail.ratting,
                ratingCount = cocktail.ratingCount,
                images = buildImages(cocktail.id.value, ImageType.COCKTAIL),
                receipt = CocktailsTable.select { CocktailsTable.slug eq slug }.first()[CocktailsTable.steps].toList(),
                goods = getCocktailGoodsV2(cocktail),
                tools = getFullToolsV2(cocktail),
                tags = getTagsV2(cocktail),
                slug = cocktail.slug,
            )
        } ?: throw NotFoundException("Cocktail with id $id not found")
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
                slug = cocktail.slug,
            )
        } ?: throw NotFoundException("Cocktail with id $id not found")
    }
}

private fun getTagsV2(cocktail: FullCocktail): List<TagVM> {
    return cocktail.tastes.map {
        return@map TagVM(
            id = TagId(it.id.value),
            name = it.name,
            path = "${FilterModels.FilterGroupBackend.TASTE.queryName.value}=${it.slug}",
            slug = it.slug,
        )
    } + cocktail.tags.map {
        return@map TagVM(
            id = TagId(it.id.value),
            name = it.name,
            path = "${FilterModels.FilterGroupBackend.TAGS.queryName.value}=${it.slug}",
            slug = it.slug,
        )
    }
}

private fun getCocktailTags(cocktail: FullCocktail): List<TagVM> {
    return cocktail.tags.map {
        return@map buildTagVM(it.id, it.name, it.slug, FilterModels.FilterGroupBackend.TAGS)
    }
}

private fun buildTagVM(id: EntityID<Int>, name: String, slug: String, filter: FilterModels.FilterGroupBackend): TagVM {
    return TagVM(
        id = TagId(id.value),
        name = name,
        path = "${filter.queryName.value}/${id.value}",
        slug = slug,
    )
}

@Deprecated("Use v2")
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
                path = "${FilterModels.FilterGroupBackend.GOODS.queryName.value}/${itemRow[GoodsTable.id].value}",
                slug = itemRow[GoodsTable.slug],
            )
        }
}

private fun getCocktailGoodsV2(cocktail: FullCocktail): List<FullGoodV2VM> {
    return CocktailsToGoodsTable.join(GoodsTable, JoinType.INNER, GoodsTable.id, CocktailsToGoodsTable.goodId)
        .select { CocktailsToGoodsTable.cocktailId eq cocktail.id.value }
        .map { itemRow ->
            val id = itemRow[GoodsTable.id].value
            FullGoodV2VM(
                id = id,
                name = itemRow[GoodsTable.name],
                images = buildImages(itemRow[GoodsTable.id].value, ImageType.ITEM),
                amount = itemRow[CocktailsToGoodsTable.amount],
                unit = itemRow[CocktailsToGoodsTable.unit],
                path = "${FilterModels.FilterGroupBackend.GOODS.queryName.value}/${itemRow[GoodsTable.slug]}",
                slug = itemRow[GoodsTable.slug],
            )
        }
}

@Deprecated("Use v2")
private fun getFullTools(cocktail: FullCocktail): List<ToolVM> {
    return buildList {
        cocktail.glassware.first().let {
            add(buildToolVM(it.id, it.name, it.slug, FilterModels.FilterGroupBackend.GLASSWARE))
        }
        addAll(cocktail.tools
            .map { buildToolVM(it.id, it.name, it.slug, FilterModels.FilterGroupBackend.TOOLS) }
        )
    }
}

private fun getFullToolsV2(cocktail: FullCocktail): List<ToolV2VM> {
    return buildList {
        cocktail.glassware.first().let {
            add(
                ToolV2VM(
                    id = it.id.value,
                    slug = it.slug,
                    path = "${FilterModels.FilterGroupBackend.GLASSWARE.queryName.value}/${it.slug}",
                    name = it.name,
                    images = buildImages(it.id.value, ImageType.ITEM),
                )
            )
        }
        addAll(cocktail.tools
            .map {
                ToolV2VM(
                    id = it.id.value,
                    slug = it.slug,
                    path = "${FilterModels.FilterGroupBackend.TOOLS.queryName.value}/${it.slug}",
                    name = it.name,
                    images = buildImages(it.id.value, ImageType.ITEM),
                )
            }
        )
    }
}


private fun buildToolVM(
    id: EntityID<Int>,
    name: String,
    slug: String,
    filter: FilterModels.FilterGroupBackend,
): ToolVM {
    return ToolVM(
        id = id.value,
        name = name,
        path = "${filter.queryName.value}/${id.value}",
        images = buildImages(id.value, ImageType.ITEM),
        slug = slug,
    )
}
