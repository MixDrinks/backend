package org.mixdrinks.view.scores

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelineContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.settings.AppSettings
import org.mixdrinks.view.error.CocktailNotFound
import org.mixdrinks.view.error.ItemsNotFound
import org.mixdrinks.view.error.QueryRequire
import org.mixdrinks.view.error.VoteError

fun Application.scores(appSettings: AppSettings) {
    routing {
        cocktailsVisit()

        cocktailsScore(appSettings)

        itemsVisit()
    }
}

private fun Routing.itemsVisit() {
    post("items/visit") {
        val id = getId()

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

private fun Routing.cocktailsScore(appSettings: AppSettings) {
    post("cocktails/score") {
        val id = getId()

        val vote = call.receive<ScoreRequest>().value

        if (vote !in appSettings.minVote..appSettings.maxVote) {
            throw VoteError()
        }

        transaction {
            val cocktailRow = (CocktailsTable.slice(CocktailsTable.ratingCount, CocktailsTable.ratingValue)
                .select { CocktailsTable.id eq id }.singleOrNull() ?: throw CocktailNotFound(id))

            val ratingCount = cocktailRow[CocktailsTable.ratingCount]
            val ratingValue = cocktailRow.getOrNull(CocktailsTable.ratingValue)

            val newRating = (ratingValue ?: 0) + vote

            CocktailsTable.update({ CocktailsTable.id eq id }) {
                it[CocktailsTable.ratingValue] = newRating
                it[CocktailsTable.ratingCount] = ratingCount + 1
            }
        }

        call.respond(HttpStatusCode.Accepted)
    }
}

private fun Routing.cocktailsVisit() {
    post("cocktails/visit") {
        val id = getId()

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
}

private fun PipelineContext<Unit, ApplicationCall>.getId(): Int {
    val id = call.request.queryParameters["id"]?.toIntOrNull() ?: throw QueryRequire("id")
    return id
}

@Serializable
data class ScoreRequest(
    @SerialName("value") val value: Int,
)
