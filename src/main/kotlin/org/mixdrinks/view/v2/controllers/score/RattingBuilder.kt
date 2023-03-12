package org.mixdrinks.view.v2.controllers.score

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Cocktail
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.view.v2.controllers.search.Page
import org.mixdrinks.view.v2.controllers.search.SearchParams

@Serializable
data class RattingItem(
    @SerialName("cocktailId") val cocktailId: CocktailId,
    @SerialName("rating") val rating: Float?,
    @SerialName("visitCount") val visitCount: Int,
)

class RattingBuilder(
    private val cocktailSelector: CocktailSelector,
) {
    fun getRattingSearchResponse(searchParams: SearchParams, page: Page?): List<RattingItem> {
        val cocktailIds = if (searchParams.filters.isNotEmpty()) {
            cocktailSelector.getCocktailIds(searchParams.filters).map { it.id }
        } else {
            transaction {
                CocktailsTable.slice(CocktailsTable.id).selectAll().map { it[CocktailsTable.id].value }
            }
        }

        return transaction {
            val query = Cocktail.find { CocktailsTable.id inList cocktailIds }

            return@transaction if (page != null) {
                query.copy().limit(page.limit, page.offset.toLong())
            } else {
                query
            }.map {
                RattingItem(
                    cocktailId = CocktailId(it.id.value),
                    rating = it.getRatting(),
                    visitCount = it.visitCount,
                )
            }
        }
    }
}
