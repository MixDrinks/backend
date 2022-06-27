package org.mixdrinks.view.v2

import io.ktor.server.application.ApplicationCall
import org.mixdrinks.view.error.QueryRequireException
import org.mixdrinks.view.v2.data.CocktailId
import java.math.BigDecimal
import java.math.RoundingMode

fun roundScore(score: Float): Float {
    return BigDecimal(score.toDouble()).setScale(1, RoundingMode.FLOOR).toFloat()
}

fun ApplicationCall.getCocktailId(): CocktailId {
    val id = this.request.queryParameters["id"]?.toIntOrNull() ?: throw QueryRequireException("id")
    return CocktailId(id)
}
