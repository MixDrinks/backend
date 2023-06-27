package org.mixdrinks.cocktails.visit

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.users.UsersTable

object VisitTable : Table(name = "visit_cocktails") {
    val userId = text("user_id").references(UsersTable.id)
    val cocktailId = integer("cocktail_id").references(CocktailsTable.id)
    val time = timestamp("time")
}
