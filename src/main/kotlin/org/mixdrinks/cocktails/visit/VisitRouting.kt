package org.mixdrinks.cocktails.visit

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mixdrinks.cocktails.score.scoreCocktailsChangeResponse
import org.mixdrinks.data.Cocktail
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.mongo.Mongo
import org.mixdrinks.view.error.QueryRequireException
import org.mixdrinks.view.v2.getCocktailId

fun Routing.visitRouting(mongo: Mongo) {
    post("v2/cocktails/visit") {
        this.call.incVisitMethod(mongo)
    }
}

private suspend fun ApplicationCall.incVisitMethod(mongo: Mongo) {
    val id = this.getCocktailId()

    transaction {
        val cocktail = Cocktail.findById(id.id) ?: throw QueryRequireException("Cocktail not found")
        CocktailsTable.update({ CocktailsTable.id eq id.id }) {
            it[visitCount] = cocktail.visitCount + 1
        }
    }

    mongo.incVisitCount(id.id)

    this.respond(
        transaction {
            scoreCocktailsChangeResponse(
                Cocktail.findById(id.id)!!
            )
        }
    )
}

