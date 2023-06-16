package org.mixdrinks.view.snapshot.sitemap

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.AlcoholVolumes
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.GlasswareTable
import org.mixdrinks.data.Good
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.data.Tag
import org.mixdrinks.data.Taste
import org.mixdrinks.data.Tool
import org.mixdrinks.data.ToolsTable
import org.mixdrinks.view.controllers.filter.FilterModels

class SiteMapCreator {

    val siteMapDto: List<String> = transaction {
        getCocktailIds().map { "cocktails/$it" } +
            getGoodIds().map { "goods/$it" } +
            getToolIds().map { "tools/$it" } +
            getGlasswareIds().map { "glassware/$it" } +
            getFirstLevelFirstersSlugs()
    }

    private fun getFirstLevelFirstersSlugs(): List<String> {
        return FilterModels.FilterGroupBackend.values().flatMap { filter ->
            when (filter) {
                FilterModels.FilterGroupBackend.TAGS -> Tag.all().map { it.slug }
                FilterModels.FilterGroupBackend.GOODS -> Good.all().map { it.slug }
                FilterModels.FilterGroupBackend.TOOLS -> Tool.all().map { it.slug }
                FilterModels.FilterGroupBackend.TASTE -> Taste.all().map { it.slug }
                FilterModels.FilterGroupBackend.ALCOHOL_VOLUME -> AlcoholVolumes.all().map { it.slug }
                FilterModels.FilterGroupBackend.GLASSWARE -> Glassware.all().map { it.slug }
            }
                .map { "${filter.queryName.value}=$it" }
        }
    }

    private fun getGlasswareIds(): List<Int> {
        return GlasswareTable.slice(GlasswareTable.id).selectAll().map { it[GlasswareTable.id] }.map { it.value }
    }

    private fun getToolIds(): List<Int> {
        return ToolsTable.slice(ToolsTable.id).selectAll().map { it[ToolsTable.id] }.map { it.value }
    }

    private fun getGoodIds(): List<Int> {
        return GoodsTable.slice(GoodsTable.id).selectAll().map { it[GoodsTable.id] }.map { it.value }
    }

    private fun getCocktailIds(): List<Int> {
        return CocktailsTable.slice(CocktailsTable.id).selectAll().map { it[CocktailsTable.id] }.map { it.value }
    }
}
