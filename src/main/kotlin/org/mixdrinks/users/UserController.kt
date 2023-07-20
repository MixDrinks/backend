package org.mixdrinks.users

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.auth.FIREBASE_AUTH
import org.mixdrinks.auth.FirebasePrincipalUser
import org.mixdrinks.cocktails.visit.VisitTable

fun Application.userController() {
    routing {
        authenticate(FIREBASE_AUTH) {
            get("user-api/check") {
                val user: FirebasePrincipalUser =
                    call.principal() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(HttpStatusCode.OK, user)
            }
            delete("user-api/myself"){
                val user: FirebasePrincipalUser =
                    call.principal() ?: return@delete call.respond(HttpStatusCode.Unauthorized)

                val userId = user.userId
                transaction {
                    VisitTable.deleteWhere { VisitTable.userId eq userId }
                    UsersTable.deleteWhere { id eq userId }
                }

                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
