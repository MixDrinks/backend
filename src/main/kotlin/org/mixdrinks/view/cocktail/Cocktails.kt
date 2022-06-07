package org.mixdrinks.view.cocktail

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inSubQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.*
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages
import org.mixdrinks.view.tag.TagVM

fun Application.cocktails() {
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
            val tags = call.request.queryParameters["tags"]
                ?.split(",")
                ?.mapNotNull(String::toIntOrNull)

            val goods = call.request.queryParameters["items"]
                ?.split(",")
                ?.mapNotNull(String::toIntOrNull)

            val search = call.request.queryParameters["query"]

            call.respond(getCompactCocktail(search, tags, goods))
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

private fun getFullCocktail(id: Int): FullCocktailVM {
    return transaction {
        val cocktail = CocktailsTable.select { CocktailsTable.id eq id }.first()

        val cocktailId = cocktail[CocktailsTable.id]

        return@transaction FullCocktailVM(
            id = cocktailId,
            name = cocktail[CocktailsTable.name],
            images = buildImages(cocktailId, ImageType.COCKTAIL),
            receipt = cocktail[CocktailsTable.steps].toList(),
            goods = getSimpleIngredients(cocktailId, ItemType.GOOD),
            tools = getSimpleIngredients(cocktailId, ItemType.TOOL),
            tags = getCocktailTags(cocktailId),
        )
    }
}

private fun getCompactCocktail(
    search: String?,
    tags: List<Int>?,
    goods: List<Int>?,
): List<CompactCocktailVM> {
    fun searchQuery(): Op<Boolean> {
        return if (search != null) {
            CocktailsTable.name.lowerCase() like "%$search%".lowercase()
        } else {
            Op.TRUE
        }
    }

    fun tagQuery(): Op<Boolean> {
        return if (tags != null) {
            val cocktailIdsByTag = CocktailToTagTable
                .slice(CocktailToTagTable.cocktailId)
                .select { CocktailToTagTable.tagId inList tags }

            CocktailsTable.id inSubQuery cocktailIdsByTag
        } else {
            Op.TRUE
        }
    }

    fun itemsQuery(): Op<Boolean> {
        return if (goods != null) {
            val cocktailIdsByGoods = CocktailsToItemsTable
                .slice(CocktailsToItemsTable.cocktailId)
                .select { CocktailsToItemsTable.goodId inList goods }

            CocktailsTable.id inSubQuery cocktailIdsByGoods
        } else {
            Op.TRUE
        }
    }

    return transaction {
        CocktailsTable.select { searchQuery() and tagQuery() and itemsQuery() }
            .map { cocktailRow ->
                buildCompactCocktail(cocktailRow)
            }
    }
}

fun buildCompactCocktail(cocktailRow: ResultRow): CompactCocktailVM {
    val id = cocktailRow[CocktailsTable.id]

    return CompactCocktailVM(
        id,
        cocktailRow[CocktailsTable.name],
        buildImages(id, ImageType.COCKTAIL),
        getSimpleIngredients(id, ItemType.GOOD),
        getCocktailTags(id),
    )
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

private fun getSimpleIngredients(id: Int, relation: ItemType): List<SimpleIngredient> {
    return CocktailsToItemsTable.join(ItemsTable, JoinType.INNER, ItemsTable.id, CocktailsToItemsTable.goodId)
        .select { CocktailsToItemsTable.cocktailId eq id and (CocktailsToItemsTable.relation eq relation.relation) }
        .map { imageRow ->
            SimpleIngredient(
                name = imageRow[ItemsTable.name],
                images = buildImages(imageRow[ItemsTable.id], ImageType.ITEM),
            )
        }

}

enum class ItemType(val relation: Int) {
    GOOD(1), TOOL(2)
}

