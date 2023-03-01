package org.mixdrinks.data

import org.jetbrains.exposed.sql.Table

object CocktailsTable : Table(name = "cocktails") {
    val id = integer("id")
    val name = text("name")
    val steps = textArray("recipe")
    val visitCount = integer("visit_count")
    val ratingCount = integer("rating_count")
    val ratingValue = integer("rating_value").nullable()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object ItemsTable : Table(name = "goods") {
    val id = integer("id")
    val name = text("name")
    val about = text("about")
    val relation = integer("relation")
    val visitCount = integer("visit_count")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object CocktailsToItemsTable : Table(name = "cocktails_to_items") {
    val cocktailId = integer("cocktail_id")
    val itemId = integer("good_id")
    val unit = text("unit")
    val amount = integer("amount")
    val relation = integer("relation")
}

object TagsTable : Table(name = "tags") {
    val id = integer("id")
    val name = text("name")
}

object TastesTable : Table(name = "tastes") {
    val id = integer("id")
    val name = text("name")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object CocktailsToTastesTable : Table(name = "cocktails_to_tastes") {
    val tasteId = integer("taste_id") references TastesTable.id
    val cocktailId = integer("cocktail_id") references CocktailsTable.id
}

object AlcoholVolumesTable : Table(name = "alcohol_volumes") {
    val id = integer("id")
    val name = text("name")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object CocktailsToAlcoholVolumesTable : Table(name = "cocktails_to_alcohol_volume") {
    val cocktailId = integer("cocktail_id") references CocktailsTable.id
    val alcoholVolumeId = integer("alcohol_volume_id") references AlcoholVolumesTable.id
}

object CocktailToTagTable : Table(name = "cocktails_to_tags") {
    val cocktailId = integer("cocktail_id")
    val tagId = integer("tag_id")
}
