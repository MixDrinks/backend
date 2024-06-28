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
import org.mixdrinks.view.error.QueryRequireException
import org.mixdrinks.view.v2.getCocktailId

fun Routing.visitRouting() {
    post("v2/cocktails/visit") {
        this.call.incVisitMethod()
    }
}

private suspend fun ApplicationCall.incVisitMethod() {
    val id = this.getCocktailId()

    transaction {
        val cocktail = Cocktail.findById(id.id) ?: throw QueryRequireException("Cocktail not found")
        CocktailsTable.update({ CocktailsTable.id eq id.id }) {
            it[visitCount] = cocktail.visitCount + 1
        }
    }

    this.respond(
        transaction {
            scoreCocktailsChangeResponse(
                Cocktail.findById(id.id)!!
            )
        }
    )
}

