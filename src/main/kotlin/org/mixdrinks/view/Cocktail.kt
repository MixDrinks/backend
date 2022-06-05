package org.mixdrinks.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.view.CocktailVM

fun Application.cocktails() {
    install(ContentNegotiation) {
        json()
    }

    val databaseUrl = environment.config.property("ktor.database.url").getString()
    val user = environment.config.property("ktor.database.user").getString()
    val password = environment.config.property("ktor.database.password").getString()

    Database.connect(
        url = "jdbc:postgresql://$databaseUrl?sslmode=require",
        user = user,
        password = password,
    )

    routing {
        get("cocktails") {
            val search = call.request.queryParameters["query"].toString()

            call.respond(transaction {
                CocktailsTable.select { CocktailsTable.name like "%$search%" }.map { row ->
                    CocktailVM(
                        row[CocktailsTable.id],
                        row[CocktailsTable.name],
                    )
                }
            })
        }
    }
}
