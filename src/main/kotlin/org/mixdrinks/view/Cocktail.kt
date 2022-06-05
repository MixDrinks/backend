package org.mixdrinks.plugins

import io.ktor.http.*
import io.ktor.http.websocket.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.view.*
import java.awt.PageAttributes.MediaType
import java.util.*

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
        get("cocktails") {
            val search = call.request.queryParameters["query"].toString()
            val modeStr = call.request.queryParameters["mode"]?.uppercase()
            val mode = modeStr?.let { CocktailView.valueOf(it) } ?: CocktailView.MINI

            when (mode) {
                CocktailView.MINI -> call.respond(getMiniCocktail(search))
                CocktailView.COMPACT -> call.respond(getCompactCocktail(search))
                CocktailView.FULL -> call.respond(HttpStatusCode.NotAcceptable)
            }
        }
    }
}

//22
private fun getCompactCocktail(search: String): List<CompactCocktailVM> {
    return transaction {
        CocktailsTable.select { CocktailsTable.name.lowerCase() like "%$search%".lowercase() }.map { cocktailRow ->

            val id = cocktailRow[CocktailsTable.id]

            val ingredients =
                CocktailsToItemsTable.join(GoodsTable, JoinType.INNER, GoodsTable.id, CocktailsToItemsTable.goodId)
                    .select { CocktailsToItemsTable.cocktailId eq id and (CocktailsToItemsTable.relation eq 1) }
                    .map { imageRow ->
                        SimpleIngredient(
                            name = imageRow[GoodsTable.name],
                            images = buildImages(imageRow[GoodsTable.id], "goods"),
                        )
                    }

            CompactCocktailVM(
                id,
                cocktailRow[CocktailsTable.name],
                buildImages(id, "cocktails"),
                ingredients,
            )
        }
    }
}

private fun buildImages(id: Int, type: String): List<Image> {
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
                src = "https://image.mixdrinks.org/$type/$id/${size.imageSize}/$id.$format",
                media = "screen and (min-width: ${size.responseSize}px)",
                type = "image/$format"
            )
        }
    }.flatten()
}

private fun getMiniCocktail(search: String): List<SimpleCocktailVM> {
    return transaction {
        CocktailsTable.select { CocktailsTable.name.lowerCase() like "%$search%".lowercase() }.map { row ->
            SimpleCocktailVM(
                row[CocktailsTable.id],
                row[CocktailsTable.name],
            )
        }
    }
}
