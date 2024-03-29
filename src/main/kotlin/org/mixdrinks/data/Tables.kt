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
    val slug = text("slug")
}

class Cocktail(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Cocktail>(CocktailsTable)

    var name by CocktailsTable.name
    var visitCount by CocktailsTable.visitCount
    var ratingCount by CocktailsTable.ratingCount
    var ratingValue by CocktailsTable.ratingValue
    var slug by CocktailsTable.slug

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
    val slug by CocktailsTable.slug
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
    val tools by Tool via CocktailsToToolsTable
    val glassware by Glassware via CocktailsToGlasswareTable
    val tags by Tag via CocktailToTagTable
    val tastes by Taste via CocktailsToTastesTable
}

object GoodsTable : IntIdTable(name = "goods", columnName = "id") {
    val name = text("name")
    val about = text("about")
    val slug = text("slug")
}

class Good(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Good>(GoodsTable)

    var name by GoodsTable.name
    var about by GoodsTable.about
    var slug by GoodsTable.slug

    val cocktails by Cocktail via CocktailsToGoodsTable
}

object CocktailsToGoodsTable : Table(name = "cocktails_to_items") {
    val cocktailId = reference("cocktail_id", CocktailsTable.id)
    val goodId = reference("good_id", GoodsTable.id)
    val unit = text("unit")
    val amount = integer("amount")
}

object TagsTable : IntIdTable(name = "tags", columnName = "id") {
    val name = text("name")
    val slug = text("slug")
}

class Tag(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Tag>(TagsTable)

    var name by TagsTable.name
    var slug by TagsTable.slug

    var cocktails by Cocktail via CocktailToTagTable
}

object TastesTable : IntIdTable(name = "tastes", columnName = "id") {
    val name = text("name")
    val slug = text("slug")
}

class Taste(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Taste>(TastesTable)

    var name by TastesTable.name
    var slug by TastesTable.slug

    var cocktails by Cocktail via CocktailsToTastesTable
}

object CocktailsToTastesTable : Table(name = "cocktails_to_tastes") {
    val tasteId = reference("taste_id", TastesTable.id)
    val cocktailId = reference("cocktail_id", CocktailsTable.id)
}

object AlcoholVolumesTable : IntIdTable(name = "alcohol_volumes", columnName = "id") {
    val name = text("name")
    val slug = text("slug")
}

class AlcoholVolumes(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<AlcoholVolumes>(AlcoholVolumesTable)

    var name by AlcoholVolumesTable.name
    var slug by AlcoholVolumesTable.slug

    var cocktails by Cocktail via CocktailsToAlcoholVolumesTable
}

object CocktailsToAlcoholVolumesTable : Table(name = "cocktails_to_alcohol_volume") {
    val cocktailId = reference("cocktail_id", CocktailsTable.id)
    val alcoholVolumeId = reference("alcohol_volume_id", AlcoholVolumesTable.id)
}

object CocktailToTagTable : Table(name = "cocktails_to_tags") {
    val cocktailId = reference("cocktail_id", CocktailsTable.id)
    val tagId = reference("tag_id", TagsTable.id)
}

object ToolsTable : IntIdTable(name = "tools", columnName = "id") {
    val name = text("name")
    val about = text("about")
    val slug = text("slug")
}

class Tool(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Tool>(ToolsTable)

    var name by ToolsTable.name
    var about by ToolsTable.about
    var slug by ToolsTable.slug

    val cocktails by Cocktail via CocktailsToToolsTable
}

object CocktailsToToolsTable : Table(name = "cocktails_to_tools") {
    val cocktailId = reference("cocktail_id", CocktailsTable.id)
    val toolId = reference("tool_id", ToolsTable.id)
}

object GlasswareTable : IntIdTable(name = "glassware", columnName = "id") {
    val name = text("name")
    val about = text("about")
    val slug = text("slug")
}

object CocktailsToGlasswareTable : Table(name = "cocktails_to_glassware") {
    val cocktailId = reference("cocktail_id", CocktailsTable.id)
    val glasswareId = reference("glassware_id", GlasswareTable.id)
}

class Glassware(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Glassware>(GlasswareTable)

    var name by GlasswareTable.name
    var about by GlasswareTable.about
    var slug by GlasswareTable.slug

    var cocktail by Cocktail via CocktailsToGlasswareTable
}
