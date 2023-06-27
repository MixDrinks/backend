package org.mixdrinks.cocktails.visit

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mixdrinks.auth.FIREBASE_AUTH
import org.mixdrinks.auth.FirebasePrincipalUser
import org.mixdrinks.cocktails.score.scoreCocktailsChangeResponse
import org.mixdrinks.data.Cocktail
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.view.error.QueryRequireException
import org.mixdrinks.view.v2.getCocktailId

fun Routing.visitRouting() {
    post("v2/cocktails/visit") {
        this.call.incVisitMethod(false)
    }
    authenticate(FIREBASE_AUTH) {
        post("user-api/cocktail/visit") {
            this.call.incVisitMethod(true)
        }
    }
}

private suspend fun ApplicationCall.incVisitMethod(withAuth: Boolean) {
    val id = this.getCocktailId()

    transaction {
        val cocktail = Cocktail.findById(id.id) ?: throw QueryRequireException("Cocktail not found")
        CocktailsTable.update({ CocktailsTable.id eq id.id }) {
            it[visitCount] = cocktail.visitCount + 1
        }

        if (withAuth) {
            val user = this@incVisitMethod.principal<FirebasePrincipalUser>()!!
            VisitTable.insert {
                it[userId] = user.userId
                it[cocktailId] = id.id
                it[time] = Clock.System.now()
            }
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

