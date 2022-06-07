package org.mixdrinks.view

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.*

fun Application.cocktails() {
    install(ContentNegotiation) {
        json()
    }

    val databaseUrl = environment.config.property("ktor.database.url").getString()
    val user = environment.config.property("ktor.database.user").getString()
    val password = environment.config.property("ktor.database.password").getString()

    Database.connect(
        url = "jdbc:postgresql://$databaseUrl?sslmode=require",
        user = user,
        password = password,
    )

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
            val search = call.request.queryParameters["query"]

            call.respond(getCompactCocktail(search, tags))
        }
        get("cocktails/full") {
            val id = call.request.queryParameters["id"]?.toIntOrNull()

            if (id != null) {
                call.respond(getFullCocktail(id))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Query id is require, and must be integer")
            }

        }
        get("tags/all") {
            call.respond(transaction {
                TagsTable.selectAll().orderBy(TagsTable.id)
                    .map { row ->
                        TagVM(
                            id = row[TagsTable.id],
                            name = row[TagsTable.name],
                        )
                    }
            })
        }
    }
}

enum class ItemType(val relation: Int) {
    GOOD(1), TOOL(2)
}

enum class ImageType(val imagePrefix: String) {
    COCKTAIL("cocktails"), ITEM("goods")
}

private fun getFullCocktail(id: Int): FullCocktailVM {
    return transaction {
        val cocktail = CocktailsTable.select { CocktailsTable.id eq id }.first()

        val cocktailId = cocktail[CocktailsTable.id]

        return@transaction FullCocktailVM(
            id = cocktailId,
            name = cocktail[CocktailsTable.name],
            images = buildImages(cocktailId, ImageType.COCKTAIL),
            goods = getSimpleIngredients(cocktailId, ItemType.GOOD),
            tools = getSimpleIngredients(cocktailId, ItemType.TOOL),
            tags = getCocktailTags(cocktailId)
        )
    }
}

private fun getSimpleIngredients(id: Int, relation: ItemType): List<SimpleIngredient> {
    return CocktailsToItemsTable.join(GoodsTable, JoinType.INNER, GoodsTable.id, CocktailsToItemsTable.goodId)
        .select { CocktailsToItemsTable.cocktailId eq id and (CocktailsToItemsTable.relation eq relation.relation) }
        .map { imageRow ->
            SimpleIngredient(
                name = imageRow[GoodsTable.name],
                images = buildImages(imageRow[GoodsTable.id], ImageType.ITEM),
            )
        }

}

private fun getCompactCocktail(search: String?, tags: List<Int>?): List<CompactCocktailVM> {
    fun searchQuery(): Op<Boolean> {
        println("query $search")
        return if (search != null) {
            CocktailsTable.name.lowerCase() like "%$search%".lowercase()
        } else {
            Op.TRUE
        }
    }

    fun tagQuery(): Op<Boolean> {
        return if (tags != null) {
            val cocktailIdsByTag = CocktailToTagTable.select { CocktailToTagTable.tagId inList tags }
                .map { row ->
                    row[CocktailToTagTable.cocktailId]
                }.distinct()
            CocktailsTable.id inList cocktailIdsByTag
        } else {
            Op.TRUE
        }
    }

    return transaction {
        CocktailsTable.select { searchQuery() and tagQuery() }
            .map { cocktailRow ->
                val id = cocktailRow[CocktailsTable.id]

                CompactCocktailVM(
                    id,
                    cocktailRow[CocktailsTable.name],
                    buildImages(id, ImageType.COCKTAIL),
                    getSimpleIngredients(id, ItemType.GOOD),
                    getCocktailTags(id),
                )
            }
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

private fun buildImages(id: Int, type: ImageType): List<Image> {
    data class SizeDep(
        val responseSize: String,
        val imageSize: String,
    )
    return listOf("webp", "jpg").map { format ->
        listOf(
            SizeDep("570", "origin"),
            SizeDep("410", "560"),
            SizeDep("330", "400"),
            SizeDep("0", "320"),
        ).map { size ->
            Image(
                src = "https://image.mixdrinks.org/${type.imagePrefix}/$id/${size.imageSize}/$id.$format",
                media = "screen and (min-width: ${size.responseSize}px)",
                type = "image/$format"
            )
        }
    }.flatten()
}

