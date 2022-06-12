package org.mixdrinks.view.cocktail

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.callbackFlow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inSubQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.*
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages
import org.mixdrinks.view.tag.TagVM
import kotlin.time.measureTime

data class CocktailFilter(
    val id: Int,
    val name: String,
    val goodIds: List<Int>,
    val toolIds: List<Int>,
    val tagIds: List<Int>,
)

class Filter {

    private val cocktails: List<CocktailFilter>

    init {
        println("Start create filter")
        cocktails = transaction {
            return@transaction CocktailsTable
                .slice(CocktailsTable.id, CocktailsTable.name)
                .selectAll()
                .map { cocktailRow ->
                    val cocktailId = cocktailRow[CocktailsTable.id]

                    val goodIds =
                        CocktailsToItemsTable
                            .slice(CocktailsToItemsTable.goodId)
                            .select { CocktailsToItemsTable.cocktailId eq cocktailId and (CocktailsToItemsTable.relation eq ItemType.GOOD.relation) }
                            .map { it[CocktailsToItemsTable.goodId] }

                    val toolIds =
                        CocktailsToItemsTable
                            .slice(CocktailsToItemsTable.goodId)
                            .select { CocktailsToItemsTable.cocktailId eq cocktailId and (CocktailsToItemsTable.relation eq ItemType.TOOL.relation) }
                            .map { it[CocktailsToItemsTable.goodId] }

                    val tagIds =
                        CocktailToTagTable
                            .slice(CocktailToTagTable.tagId)
                            .select { CocktailToTagTable.cocktailId eq cocktailId }
                            .map { it[CocktailToTagTable.tagId] }

                    return@map CocktailFilter(
                        id = cocktailId,
                        name = cocktailRow[CocktailsTable.name],
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
            val tags = call.request.queryParameters["tags"]
                ?.split(",")
                ?.mapNotNull(String::toIntOrNull)

            val goods = call.request.queryParameters["goods"]
                ?.split(",")
                ?.mapNotNull(String::toIntOrNull)

            val tools = call.request.queryParameters["tools"]
                ?.split(",")
                ?.mapNotNull(String::toIntOrNull)

            val search = call.request.queryParameters["query"]

            var offset = call.request.queryParameters["offset"]?.toIntOrNull()
            var limit = call.request.queryParameters["limit"]?.toIntOrNull()

            val page = call.request.queryParameters["page"]?.toIntOrNull()

            if (page != null) {
                offset = (page * DEFAULT_PAGE_SIZE)
                limit = DEFAULT_PAGE_SIZE
            }

            call.respond(getCompactCocktail(search, tags, goods, tools, offset, limit))
        }
    }


    private fun getCompactCocktail(
        search: String?,
        tags: List<Int>?,
        goods: List<Int>?,
        tools: List<Int>?,
        limit: Int?,
        offset: Int?,
    ): FilterResultVM {
        var result = cocktails

        if (search != null) {
            result = result.filter { it.name.contains(search) }
        }

        if (tags != null) {
            result = result.filter { it.tagIds.containsAll(tags) }
        }

        if (goods != null) {
            result = result.filter { it.goodIds.containsAll(goods) }
        }

        if (tools != null) {
            result = result.filter { it.toolIds.containsAll(tools) }
        }

        val count = result.count()

        if (offset != null) {
            result = result.subList(offset, result.size)
        }

        if (limit != null) {
            result = result.subList(0, limit)
        }

        val cocktails = transaction {
            result.map { cocktailFilter ->
                CompactCocktailVM(
                    cocktailFilter.id,
                    cocktailFilter.name,
                    buildImages(cocktailFilter.id, ImageType.COCKTAIL),
                    getSimpleGoods(cocktailFilter.goodIds),
                    getTags(cocktailFilter.tagIds)
                )
            }
        }

        return FilterResultVM(count, cocktails)
    }

    private fun getTags(tagIds: List<Int>): List<TagVM> {
        return TagsTable.select { TagsTable.id inList tagIds }
            .map { itemRow ->
                TagVM(
                    id = itemRow[TagsTable.id],
                    name = itemRow[TagsTable.name],
                )
            }
    }

    private fun getSimpleGoods(goods: List<Int>): List<SimpleIngredient> {
        return ItemsTable.select { ItemsTable.id inList goods }
            .map { itemRow ->
                SimpleIngredient(
                    id = itemRow[ItemsTable.id],
                    name = itemRow[ItemsTable.name],
                    images = buildImages(itemRow[ItemsTable.id], ImageType.ITEM),
                )
            }
    }
}
