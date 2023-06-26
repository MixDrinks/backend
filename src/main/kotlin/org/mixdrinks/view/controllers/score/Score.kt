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
import org.mixdrinks.data.Cocktail
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.view.controllers.settings.AppSettings
import org.mixdrinks.view.error.QueryRequireException
import org.mixdrinks.view.error.VoteError
import org.mixdrinks.view.v2.getCocktailId
import org.mixdrinks.view.v2.roundScore

fun Application.score(appSettings: AppSettings) {
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

            call.respond(transaction {
                scoreCocktailsChangeResponse(
                    Cocktail.findById(id.id)!!
                )
            })
        }
        post("v2/cocktails/visit") {
            val id = call.getCocktailId()

            transaction {
                CocktailsTable.update({ CocktailsTable.id eq id.id }) {
                    it[visitCount] = visitCount + 1
                }
            }

            call.respond(
                transaction {
                    scoreCocktailsChangeResponse(
                        Cocktail.findById(id.id) ?: throw QueryRequireException("Cocktail not found")
                    )
                }
            )
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

@Serializable
data class CocktailScoreChangeResponse(
    @SerialName("cocktailId") val cocktailId: CocktailId,
    @SerialName("rating") val rating: Float?,
    @SerialName("visitCount") val visitCount: Int,
)

private fun scoreCocktailsChangeResponse(cocktail: Cocktail): CocktailScoreChangeResponse {
    return CocktailScoreChangeResponse(
        cocktailId = CocktailId(cocktail.id.value),
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
