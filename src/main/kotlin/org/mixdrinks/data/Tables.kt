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

    var name by CocktailsTable.name
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

class FullCocktail(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<FullCocktail>(CocktailsTable)

    val name by CocktailsTable.name
    val steps: Array<String> by CocktailsTable.steps
    val visitCount by CocktailsTable.visitCount
    val ratingCount by CocktailsTable.ratingCount
    val ratingValue by CocktailsTable.ratingValue

    val ratting: Float?
        get() {
            return ratingValue?.let { ratingValue ->
                ratingCount.takeIf { it != 0 }?.let { ratingCount ->
                    ratingValue.toFloat() / ratingCount.toFloat()
                }
            }
        }

    val goods by Good via CocktailsToGoodsTable

}

object GoodsTable : IntIdTable(name = "goods", columnName = "id") {
    val name = text("name")
    val about = text("about")
    val visitCount = integer("visit_count").default(0)
}

class Good(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Good>(GoodsTable)

    var name by GoodsTable.name
    var about by GoodsTable.about
    var visitCount by GoodsTable.visitCount
}

object CocktailsToGoodsTable : Table(name = "cocktails_to_items") {
    val cocktailId = reference("cocktail_id", CocktailsTable.id)
    val goodId = reference("good_id", GoodsTable.id)
    val unit = text("unit")
    val amount = integer("amount")
}

object TagsTable : IntIdTable(name = "tags", columnName = "id") {
    val name = text("name")
}

class Tag(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Tag>(TagsTable)

    var name by TagsTable.name
}

object TastesTable : IntIdTable(name = "tastes", columnName = "id") {
    val name = text("name")
}

class Taste(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Taste>(TastesTable)

    var name by TastesTable.name
}

object CocktailsToTastesTable : Table(name = "cocktails_to_tastes") {
    val tasteId = reference("taste_id", TastesTable.id)
    val cocktailId = reference("cocktail_id", CocktailsTable.id)
}

object AlcoholVolumesTable : IntIdTable(name = "alcohol_volumes", columnName = "id") {
    val name = text("name")
}

class AlcoholVolumes(id: EntityID<Int>): IntEntity(id) {
    companion object : IntEntityClass<AlcoholVolumes>(AlcoholVolumesTable)

    var name by AlcoholVolumesTable.name
}

object CocktailsToAlcoholVolumesTable : Table(name = "cocktails_to_alcohol_volume") {
    val cocktailId = integer("cocktail_id").references(CocktailsTable.id)
    val alcoholVolumeId = integer("alcohol_volume_id").references(AlcoholVolumesTable.id)
}

object CocktailToTagTable : Table(name = "cocktails_to_tags") {
    val cocktailId = integer("cocktail_id")
    val tagId = integer("tag_id")
}

object ToolsTable : IntIdTable(name = "tools", columnName = "id") {
    val name = text("name")
    val about = text("about")
    val visitCount = integer("visit_count").default(0)
}

class Tool(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Tool>(ToolsTable)

    var name by ToolsTable.name
    var about by ToolsTable.about
    var visitCount by ToolsTable.visitCount
}

object CocktailsToToolsTable : Table(name = "cocktails_to_tools") {
    val cocktailId = reference("cocktail_id", CocktailsTable.id)
    val toolId = reference("tool_id", ToolsTable.id)
}
