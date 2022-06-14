package org.mixdrinks.view.cocktail

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.*
import org.mixdrinks.view.cocktail.domain.filterCocktails
import org.mixdrinks.view.error.SortTypeNotFound
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

data class CocktailFilter(
    val id: Int,
    val name: String,
    val rating: Float?,
    val visitCount: Int,
    val goodIds: List<Int>,
    val toolIds: List<Int>,
    val tagIds: List<Int>,
)

enum class SortType(val key: String) {
    MOST_POPULAR("most-popular"),
    BIGGEST_RATE("biggest-rate"),
}

class FilterRouter {

    private val cocktails: List<CocktailFilter>

    init {
        println("Start create filter")
        cocktails = transaction {
            return@transaction CocktailsTable.slice(
                CocktailsTable.id,
                CocktailsTable.name,
                CocktailsTable.ratingCount,
                CocktailsTable.ratingValue,
                CocktailsTable.visitCount
            ).selectAll()
                .map { cocktailRow ->
                    val cocktailId = cocktailRow[CocktailsTable.id]

                    val goodIds = CocktailsToItemsTable.slice(CocktailsToItemsTable.goodId)
                        .select { CocktailsToItemsTable.cocktailId eq cocktailId and (CocktailsToItemsTable.relation eq ItemType.GOOD.relation) }
                        .map { it[CocktailsToItemsTable.goodId] }

                    val toolIds = CocktailsToItemsTable.slice(CocktailsToItemsTable.goodId)
                        .select { CocktailsToItemsTable.cocktailId eq cocktailId and (CocktailsToItemsTable.relation eq ItemType.TOOL.relation) }
                        .map { it[CocktailsToItemsTable.goodId] }

                    val tagIds = CocktailToTagTable.slice(CocktailToTagTable.tagId)
                        .select { CocktailToTagTable.cocktailId eq cocktailId }.map { it[CocktailToTagTable.tagId] }

                    return@map CocktailFilter(
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
        println("End create filter")
    }

    fun filter(routing: Routing) = with(routing) {
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

            call.respond(getCompactCocktail(search, tags, goods, tools, offset, limit, sortType))
        }
    }

    private fun getCompactCocktail(
        search: String?,
        tags: List<Int>?,
        goods: List<Int>?,
        tools: List<Int>?,
        offset: Int?,
        limit: Int?,
        sortType: SortType,
    ): FilterResultVM {
        return transaction {
            val allTags = TagsTable.slice(TagsTable.id).selectAll().map { it[TagsTable.id] }
            val result = filterCocktails(cocktails, search, tags, goods, tools, offset, limit, sortType, allTags)

            val resultCocktails = result.list
                .map { cocktailFilter ->
                    CompactCocktailVM(
                        cocktailFilter.id,
                        cocktailFilter.name,
                        cocktailFilter.rating,
                        cocktailFilter.visitCount,
                        buildImages(cocktailFilter.id, ImageType.COCKTAIL),
                    )
                }

            FilterResultVM(result.totalCount, resultCocktails, result.tagMaps, result.goodMaps, result.toolMaps)
        }
    }
}
