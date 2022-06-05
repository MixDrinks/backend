package org.mixdrinks.data

import org.jetbrains.exposed.sql.Table

object CocktailsTable : Table(name = "cocktails") {
    val id = integer("id")
    val name = text("name")
    val image = text("image_url")
    val steps = textArray("recipe")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object GoodsTable : Table(name = "goods") {
    val id = integer("id")
    val name = text("name")
    val about = text("about")
    val image = text("image_url")

    //override val primaryKey: PrimaryKey = PrimaryKey(id)
}

object CocktailsToIngredientsTable : Table(name = "cocktail_to_items") {
    val cocktailId = integer("cocktail_id")
    val goodId = integer("good_id")
    val unit = text("unit")
    val amount = integer("amount")
    val relation = text("relation")
}

object TagsTableTable : Table(name = "tags") {
    val id = integer("id")
    val name = text("name")
}

object CocktailToTagTable : Table(name = "cocktails_to_tags") {
    val cocktailId = integer("cocktail_id")
    val tagId = integer("tag_id")
}
