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
import org.mixdrinks.domain.FilterGroups

class SiteMapCreator {

    val siteMapDto: List<String> = transaction {
        getCocktailSlug().map { "cocktails/$it" } +
            getGoodSlug().map { "goods/$it" } +
            getToolSlug().map { "tools/$it" } +
            getGlasswareSlug().map { "glassware/$it" } +
            getFirstLevelFirstersSlugs()
    }

    private fun getFirstLevelFirstersSlugs(): List<String> {
        return FilterGroups.values().flatMap { filter ->
            when (filter) {
                FilterGroups.TAGS -> Tag.all().map { it.slug }
                FilterGroups.GOODS -> Good.all().map { it.slug }
                FilterGroups.TOOLS -> Tool.all().map { it.slug }
                FilterGroups.TASTE -> Taste.all().map { it.slug }
                FilterGroups.ALCOHOL_VOLUME -> AlcoholVolumes.all().map { it.slug }
                FilterGroups.GLASSWARE -> Glassware.all().map { it.slug }
            }
                .map { "${filter.queryName.value}=$it" }
        }
    }

    private fun getGlasswareSlug(): List<String> {
        return GlasswareTable.slice(GlasswareTable.slug).selectAll().map { it[GlasswareTable.slug] }
    }

    private fun getToolSlug(): List<String> {
        return ToolsTable.slice(ToolsTable.slug).selectAll().map { it[ToolsTable.slug] }
    }

    private fun getGoodSlug(): List<String> {
        return GoodsTable.slice(GoodsTable.slug).selectAll().map { it[GoodsTable.slug] }
    }

    private fun getCocktailSlug(): List<String> {
        return CocktailsTable.slice(CocktailsTable.slug).selectAll().map { it[CocktailsTable.slug] }
    }
}
