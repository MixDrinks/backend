package org.mixdrinks.view.v2.controllers.score

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
import org.mixdrinks.data.Cocktail
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.view.error.QueryRequireException
import org.mixdrinks.view.error.VoteError
import org.mixdrinks.view.v2.controllers.settings.AppSettings
import org.mixdrinks.view.v2.getCocktailId
import org.mixdrinks.view.v2.roundScore

fun Application.score(appSettings: AppSettings) {
    routing {
        post("v2/cocktails/score") {
            val id = call.getCocktailId()
            val vote = call.getRatting(appSettings)

            call.respond(transaction {
                CocktailsTable.update({ CocktailsTable.id eq id.id }) {
                    it[ratingValue] = ratingValue + vote
                    it[ratingCount] = ratingCount + 1
                }

                scoreCocktailsChangeResponse(id)
            })
        }
        post("v2/cocktails/visit") {
            val id = call.getCocktailId()

            call.respond(transaction {
                CocktailsTable.update({ CocktailsTable.id eq id.id }) {
                    it[visitCount] = visitCount + 1
                }

                scoreCocktailsChangeResponse(id)
            })
        }
        get("v2/cocktail/ratting") {
            val id = call.getCocktailId()
            call.respond(transaction { scoreCocktailsChangeResponse(id) })
        }
    }
}

@Serializable
data class CocktailScoreChangeResponse(
    @SerialName("cocktailId") val cocktailId: CocktailId,
    @SerialName("rating") val rating: Float?,
    @SerialName("visitCount") val visitCount: Int,
)

fun scoreCocktailsChangeResponse(id: CocktailId): CocktailScoreChangeResponse {
    val cocktail = Cocktail.findById(id.id) ?: throw QueryRequireException("Cocktail not found")

    return CocktailScoreChangeResponse(
        cocktailId = id,
        rating = cocktail.getRatting()?.let { notNullScore -> roundScore(notNullScore) },
        visitCount = cocktail.visitCount,
    )
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
