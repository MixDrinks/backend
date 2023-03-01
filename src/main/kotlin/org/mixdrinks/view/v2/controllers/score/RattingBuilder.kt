package org.mixdrinks.view.v2.controllers.score

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.view.rating.getRating
import org.mixdrinks.view.v2.controllers.search.CocktailsSourceV2
import org.mixdrinks.view.v2.controllers.search.Page
import org.mixdrinks.view.v2.controllers.search.SearchParams
import org.mixdrinks.view.v2.data.CocktailId

@Serializable
data class RattingItem(
    @SerialName("cocktailId") val cocktailId: CocktailId,
    @SerialName("rating") val rating: Float?,
    @SerialName("visitCount") val visitCount: Int,
)

class RattingBuilder(
    private val cocktailsSourceV2: CocktailsSourceV2
) {
    fun getRattingSearchResponse(searchParams: SearchParams, page: Page?): List<RattingItem> {
        val cocktailIds = cocktailsSourceV2.cocktailsBySearch(searchParams)

        return transaction {
            val query = CocktailsTable.select { CocktailsTable.id inList cocktailIds.map { it.value } }

            return@transaction if (page != null) {
                query.copy().limit(page.limit, page.offset.toLong())
            } else {
                query
            }.map {
                RattingItem(
                    cocktailId = CocktailId(it[CocktailsTable.id].value),
                    rating = it.getRating(),
                    visitCount = it[CocktailsTable.visitCount],
                )
            }
        }
    }
}
