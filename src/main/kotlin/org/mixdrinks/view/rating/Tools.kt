package org.mixdrinks.view.rating

import org.jetbrains.exposed.sql.ResultRow
import org.mixdrinks.data.CocktailsTable

fun ResultRow.getRating(): Float? {
    return this[CocktailsTable.ratingValue]?.let { ratingValue ->
        this[CocktailsTable.ratingCount].takeIf { it != 0 }?.let { ratingCount ->
            ratingValue.toFloat() / ratingCount.toFloat()
        }
    }
}
