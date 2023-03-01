package org

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToAlcoholVolumesTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.CocktailsToTastesTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.TastesTable

fun Transaction.createDataBase() {
    SchemaUtils.drop(
        CocktailsTable,
        CocktailsToItemsTable,
        CocktailToTagTable,
        TagsTable,
        ItemsTable,
        TastesTable,
        CocktailsToTastesTable,
        CocktailsToAlcoholVolumesTable,
        CocktailToTagTable,
    )
    SchemaUtils.create(
        CocktailsTable,
        CocktailsToItemsTable,
        CocktailToTagTable,
        TagsTable,
        ItemsTable,
        TastesTable,
        CocktailsToTastesTable,
        CocktailsToAlcoholVolumesTable,
        CocktailToTagTable,
    )
}
