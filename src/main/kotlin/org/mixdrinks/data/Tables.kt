package org.mixdrinks.data

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object CocktailsTable : IntIdTable(name = "cocktails", columnName = "id") {
    val name = text("name")
    val steps = textArray("recipe")
    val visitCount = integer("visit_count")
    val ratingCount = integer("rating_count")
    val ratingValue = integer("rating_value").nullable()
}

class Cocktail(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Cocktail>(CocktailsTable)

    val name by CocktailsTable.name
    var visitCount by CocktailsTable.visitCount
    var ratingCount by CocktailsTable.ratingCount
    var ratingValue by CocktailsTable.ratingValue

    fun getRatting(): Float? {
        return ratingValue?.let { ratingValue ->
            ratingCount.takeIf { it != 0 }?.let { ratingCount ->
                ratingValue.toFloat() / ratingCount.toFloat()
            }
        }
    }
}

object ItemsTable : IntIdTable(name = "goods", columnName = "id") {
    val name = text("name")
    val about = text("about")
    val relation = integer("relation")
    val visitCount = integer("visit_count")
}

object CocktailsToItemsTable : Table(name = "cocktails_to_items") {
    val cocktailId = integer("cocktail_id")
    val itemId = integer("good_id")
    val unit = text("unit")
    val amount = integer("amount")
    val relation = integer("relation")
}

object TagsTable : IntIdTable(name = "tags", columnName = "id") {
    val name = text("name")
}

object TastesTable : IntIdTable(name = "tastes", columnName = "id") {
    val name = text("name")
}

object CocktailsToTastesTable : Table(name = "cocktails_to_tastes") {
    val tasteId = integer("taste_id").references(TastesTable.id)
    val cocktailId = integer("cocktail_id").references(CocktailsTable.id)
}

object AlcoholVolumesTable : IntIdTable(name = "alcohol_volumes", columnName = "id") {
    val name = text("name")
}

object CocktailsToAlcoholVolumesTable : Table(name = "cocktails_to_alcohol_volume") {
    val cocktailId = integer("cocktail_id").references(CocktailsTable.id)
    val alcoholVolumeId = integer("alcohol_volume_id").references(AlcoholVolumesTable.id)
}

object CocktailToTagTable : Table(name = "cocktails_to_tags") {
    val cocktailId = integer("cocktail_id")
    val tagId = integer("tag_id")
}
