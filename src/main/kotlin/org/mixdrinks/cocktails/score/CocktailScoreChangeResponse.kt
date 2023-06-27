package org.mixdrinks.cocktails.score

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.data.Cocktail
import org.mixdrinks.dto.CocktailId
import java.math.BigDecimal
import java.math.RoundingMode

@Serializable
data class CocktailScoreChangeResponse(
    @SerialName("cocktailId") val cocktailId: CocktailId,
    @SerialName("rating") val rating: Float?,
    @SerialName("visitCount") val visitCount: Int,
)

internal fun scoreCocktailsChangeResponse(cocktail: Cocktail): CocktailScoreChangeResponse {
    return CocktailScoreChangeResponse(
        cocktailId = CocktailId(cocktail.id.value),
        rating = cocktail.getRatting()?.let { notNullScore -> roundScore(notNullScore) },
        visitCount = cocktail.visitCount,
    )
}

fun roundScore(score: Float): Float {
    return BigDecimal(score.toDouble()).setScale(1, RoundingMode.FLOOR).toFloat()
}


