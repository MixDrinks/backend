package org

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.mixdrinks.cocktails.visit.VisitTable
import org.mixdrinks.data.AlcoholVolumesTable
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToAlcoholVolumesTable
import org.mixdrinks.data.CocktailsToGlasswareTable
import org.mixdrinks.data.CocktailsToGoodsTable
import org.mixdrinks.data.CocktailsToTastesTable
import org.mixdrinks.data.CocktailsToToolsTable
import org.mixdrinks.data.GlasswareTable
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.TastesTable
import org.mixdrinks.data.ToolsTable
import org.mixdrinks.users.UsersTable

fun Transaction.createDataBase() {
    SchemaUtils.drop(
        CocktailsTable,
        CocktailsToGoodsTable,
        CocktailToTagTable,
        TagsTable,
        GoodsTable,
        TastesTable,
        CocktailsToTastesTable,
        CocktailsToAlcoholVolumesTable,
        CocktailToTagTable,
        ToolsTable,
        CocktailsToToolsTable,
        AlcoholVolumesTable,
        GlasswareTable,
        CocktailsToGlasswareTable,
        VisitTable,
        UsersTable,
    )
    SchemaUtils.create(
        CocktailsTable,
        CocktailsToGoodsTable,
        CocktailToTagTable,
        TagsTable,
        GoodsTable,
        TastesTable,
        CocktailsToTastesTable,
        CocktailsToAlcoholVolumesTable,
        CocktailToTagTable,
        ToolsTable,
        CocktailsToToolsTable,
        AlcoholVolumesTable,
        GlasswareTable,
        CocktailsToGlasswareTable,
        VisitTable,
        UsersTable,
    )
}
