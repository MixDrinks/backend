package org.mixdrinks.view.scores

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.view.error.CocktailNotFound
import org.mixdrinks.view.error.ItemsNotFound
import org.mixdrinks.view.error.QueryRequire

fun Application.scores() {
    routing {
        post("cocktails/visit") {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: throw QueryRequire("id")

            transaction {
                val count =
                    (CocktailsTable.slice(CocktailsTable.visitCount).select { CocktailsTable.id eq id }.singleOrNull()
                        ?: throw CocktailNotFound(id))[CocktailsTable.visitCount]

                CocktailsTable.update({ CocktailsTable.id eq id }) {
                    it[visitCount] = count + 1
                }
            }

            call.respond(HttpStatusCode.Accepted)
        }

        post("items/visit") {
            val id = call.request.queryParameters["id"]?.toIntOrNull() ?: throw QueryRequire("id")

            transaction {
                val count = (ItemsTable.slice(ItemsTable.visitCount).select { ItemsTable.id eq id }.singleOrNull()
                    ?: throw ItemsNotFound(id))[ItemsTable.visitCount]

                ItemsTable.update({ ItemsTable.id eq id }) {
                    it[visitCount] = count + 1
                }
            }

            call.respond(HttpStatusCode.Accepted)
        }
    }
}
