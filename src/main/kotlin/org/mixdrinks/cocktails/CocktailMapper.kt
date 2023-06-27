package org.mixdrinks.cocktails

import org.jetbrains.exposed.sql.ResultRow
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.view.cocktail.CompactCocktailVM
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

class CocktailMapper {

    fun createCocktails(row: ResultRow): CompactCocktailVM {
        val id = row[CocktailsTable.id].value
        val ratingValue = row[CocktailsTable.ratingValue]

        val rating = ratingValue?.let {
            it.toFloat() / row[CocktailsTable.ratingCount].toFloat()
        }

        return CompactCocktailVM(
            id = CocktailId(id),
            name = row[CocktailsTable.name],
            rating = rating,
            visitCount = row[CocktailsTable.visitCount],
            images = buildImages(id, ImageType.COCKTAIL),
            slug = row[CocktailsTable.slug],
        )
    }

}
