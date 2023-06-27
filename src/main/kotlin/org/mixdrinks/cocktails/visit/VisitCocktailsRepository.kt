package org.mixdrinks.cocktails.visit

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.cocktails.CocktailMapper
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.view.cocktail.CompactCocktailVM
import org.mixdrinks.view.controllers.search.paggination.Page

class VisitCocktailsRepository(
    private val cocktailMapper: CocktailMapper,
) {

    fun getVisitedCocktails(userId: String, page: Page? = null): List<CompactCocktailVM> = transaction {
        val cocktailIds = VisitTable.slice(VisitTable.cocktailId, VisitTable.time.max())
            .select { VisitTable.userId eq userId }
            .groupBy(VisitTable.cocktailId)
            .orderBy(VisitTable.time.max(), SortOrder.DESC)
            .let {
                if (page != null) {
                    it.limit(page.limit, page.offset.toLong())
                } else {
                    it
                }
            }
            .map { it[VisitTable.cocktailId] }

        return@transaction CocktailsTable.select { CocktailsTable.id inList cocktailIds }
            .map { cocktailMapper.createCocktails(it) }
            .sortedBy { cocktailIds.indexOf(it.id.id) }
    }
}
