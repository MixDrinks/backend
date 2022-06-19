package org.mixdrinks.view.cocktail

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.*
import org.mixdrinks.view.cocktail.domain.CocktailsAggregator
import org.mixdrinks.view.cocktail.domain.CocktailsFilterSearchParam
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.error.SortTypeNotFound
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages
import org.mixdrinks.view.tag.TagVM

const val DEFAULT_PAGE_SIZE = 24

fun Application.cocktails(cocktailsAggregator: CocktailsAggregator) {
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
        get("cocktails/filter") {
            val tags = call.request.queryParameters["tags"]?.split(",")?.mapNotNull(String::toIntOrNull)

            val goods = call.request.queryParameters["goods"]?.split(",")?.mapNotNull(String::toIntOrNull)

            val tools = call.request.queryParameters["tools"]?.split(",")?.mapNotNull(String::toIntOrNull)

            val search = call.request.queryParameters["query"]

            var offset = call.request.queryParameters["offset"]?.toIntOrNull()
            var limit = call.request.queryParameters["limit"]?.toIntOrNull()

            val page = call.request.queryParameters["page"]?.toIntOrNull()

            if (page != null) {
                offset = (page * DEFAULT_PAGE_SIZE)
                limit = DEFAULT_PAGE_SIZE
            }

            val sortKey = call.request.queryParameters["sort"] ?: SortType.MOST_POPULAR.key

            val sortType = SortType.values().firstOrNull { it.key == sortKey } ?: throw SortTypeNotFound()

            val searchParam = CocktailsFilterSearchParam(
                search, tags, goods, tools,
            )

            val result = cocktailsAggregator.getCompactCocktail(searchParam, offset, limit, sortType)

            call.respond(
                FilterResultVMOld(
                    totalCount = result.totalCount,
                    cocktails = result.list.map { cocktailFilter ->
                        CompactCocktailVM(
                            cocktailFilter.id,
                            cocktailFilter.name,
                            cocktailFilter.rating,
                            cocktailFilter.visitCount,
                            buildImages(cocktailFilter.id, ImageType.COCKTAIL),
                        )
                    },
                    cocktailsByGoodCounts = result.counts.goodCounts,
                    cocktailsByTagCounts = result.counts.tagCounts,
                    cocktailsByToolCounts = result.counts.toolCounts,
                )
            )
        }
        get("cocktails/filter/v2") {
            val tags = call.request.queryParameters["tags"]?.split(",")?.mapNotNull(String::toIntOrNull)

            val goods = call.request.queryParameters["goods"]?.split(",")?.mapNotNull(String::toIntOrNull)

            val tools = call.request.queryParameters["tools"]?.split(",")?.mapNotNull(String::toIntOrNull)

            val search = call.request.queryParameters["query"]

            var offset = call.request.queryParameters["offset"]?.toIntOrNull()
            var limit = call.request.queryParameters["limit"]?.toIntOrNull()

            val page = call.request.queryParameters["page"]?.toIntOrNull()

            if (page != null) {
                offset = (page * DEFAULT_PAGE_SIZE)
                limit = DEFAULT_PAGE_SIZE
            }

            val sortKey = call.request.queryParameters["sort"] ?: SortType.MOST_POPULAR.key

            val sortType = SortType.values().firstOrNull { it.key == sortKey } ?: throw SortTypeNotFound()

            val searchParam = CocktailsFilterSearchParam(
                search, tags, goods, tools,
            )

            val result = cocktailsAggregator.getCompactCocktail(searchParam, offset, limit, sortType)

            call.respond(
                FilterResultVM(
                    totalCount = result.totalCount, cocktails = result.list.map { cocktailFilter ->
                        CompactCocktailVM(
                            cocktailFilter.id,
                            cocktailFilter.name,
                            cocktailFilter.rating,
                            cocktailFilter.visitCount,
                            buildImages(cocktailFilter.id, ImageType.COCKTAIL),
                        )

                    }, filterFutureCounts = FilterFutureCounts(
                        tagCounts = result.counts.tagCounts,
                        goodCounts = result.counts.goodCounts,
                        toolCounts = result.counts.toolCounts,
                    )
                )
            )
        }
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

fun ResultRow.getRating(): Float? {
    return this[CocktailsTable.ratingValue]?.let { ratingValue ->
        this[CocktailsTable.ratingCount].takeIf { it != 0 }?.let { ratingCount ->
            ratingValue.toFloat() / ratingCount.toFloat()
        }
    }
}

private fun getFullCocktail(id: Int): FullCocktailVM {
    return transaction {
        val cocktail = CocktailsTable.select { CocktailsTable.id eq id }.first()

        val cocktailId = cocktail[CocktailsTable.id]

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
        )
    }
}

private fun getCocktailTags(id: Int) =
    CocktailToTagTable.join(TagsTable, JoinType.INNER, TagsTable.id, CocktailToTagTable.tagId)
        .select { CocktailToTagTable.cocktailId eq id }.map { tagRow ->
            TagVM(
                tagRow[TagsTable.id], tagRow[TagsTable.name]
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
