package org.mixdrinks.view.v2.controllers.score

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsTable.visitCount
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.settings.AppSettings
import org.mixdrinks.view.error.QueryRequireException
import org.mixdrinks.view.error.VoteError
import org.mixdrinks.view.rating.getRating
import org.mixdrinks.view.scores.ScoreRequest
import org.mixdrinks.view.v2.data.CocktailId
import org.mixdrinks.view.v2.data.ItemId
import org.mixdrinks.view.v2.roundScore

@Serializable
data class CocktailScoreChangeResponse(
    @SerialName("cocktailId") val cocktailId: CocktailId,
    @SerialName("rating") val rating: Float?,
    @SerialName("visitCount") val visitCount: Int,
)

fun Application.scoreV2(appSettings: AppSettings) {
    routing {
        post("v2/cocktails/score") {
            val id = call.getCocktailId()
            val vote = call.getRatting(appSettings)

            call.respond(transaction {
                CocktailsTable.update({ CocktailsTable.id eq id.value }) {
                    it[ratingValue] = ratingValue + vote
                    it[ratingCount] = ratingCount + 1
                }

                scoreCocktailsChangeResponse(id)
            })
        }
        post("v2/cocktails/visit") {
            val id = call.getCocktailId()

            call.respond(transaction {
                CocktailsTable.update({ CocktailsTable.id eq id.value }) {
                    it[visitCount] = visitCount + 1
                }

                scoreCocktailsChangeResponse(id)
            })
        }
    }
}

private fun scoreCocktailsChangeResponse(id: CocktailId): CocktailScoreChangeResponse {
    return CocktailsTable.select { CocktailsTable.id eq id.value }.firstOrNull()?.let {
        CocktailScoreChangeResponse(
            cocktailId = id,
            rating = it.getRating()?.let { notNullScore -> roundScore(notNullScore) },
            visitCount = it[visitCount],
        )
    } ?: throw QueryRequireException("Cocktail not found")
}

private fun ApplicationCall.getCocktailId(): CocktailId {
    val id = this.request.queryParameters["id"]?.toIntOrNull() ?: throw QueryRequireException("id")
    return CocktailId(id)
}
private suspend fun ApplicationCall.getRatting(appSettings: AppSettings): Int {
    val vote = this.receive<ScoreRequest>().value

    if (vote !in appSettings.minVote..appSettings.maxVote) {
        throw VoteError()
    }

    return vote
}
