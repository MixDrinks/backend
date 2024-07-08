package org.mixdrinks.view.controllers.score

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mixdrinks.cocktails.score.scoreCocktailsChangeResponse
import org.mixdrinks.data.Cocktail
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.mongo.Mongo
import org.mixdrinks.view.controllers.settings.AppSettings
import org.mixdrinks.view.error.QueryRequireException
import org.mixdrinks.view.error.VoteError
import org.mixdrinks.view.v2.getCocktailId

fun Application.score(appSettings: AppSettings, mongo: Mongo) {
    routing {
        post("v2/cocktails/score") {
            val id = call.getCocktailId()
            val vote = call.getRatting(appSettings)

            transaction {
                val cocktail = Cocktail.findById(id.id) ?: throw QueryRequireException("Cocktail not found")
                val newValue = (cocktail.ratingValue ?: 0) + vote

                CocktailsTable.update({ CocktailsTable.id eq id.id }) {
                    it[ratingValue] = newValue
                    it[ratingCount] = ratingCount + 1
                }
            }

            mongo.updateRating(id.id, vote)

            call.respond(transaction {
                scoreCocktailsChangeResponse(
                    Cocktail.findById(id.id)!!
                )
            })
        }
        get("v2/cocktails/ratting") {
            call.respond(transaction {
                Cocktail.all().associate { cocktail ->
                    Pair(CocktailId(cocktail.id.value), scoreCocktailsChangeResponse(cocktail))
                }
            })
        }
    }
}

private suspend fun ApplicationCall.getRatting(appSettings: AppSettings): Int {
    val vote = this.receive<ScoreRequest>().value

    if (vote !in appSettings.minVote..appSettings.maxVote) {
        throw VoteError()
    }

    return vote
}

@Serializable
data class ScoreRequest(
    @SerialName("value") val value: Int,
)
