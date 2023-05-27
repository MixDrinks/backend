package org.mixdrinks.view.snapshot.sitemap

import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.GlasswareTable
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.data.ToolsTable

class SiteMapCreator {

    val siteMapDto: Urlset = transaction {
        Urlset(
            url = (getCocktailIds().map { "https://mixdrinks.org/cocktails/$it" } +
                    getGoodIds().map { "https://mixdrinks.org/goods/$it" } +
                    getToolIds().map { "https://mixdrinks.org/tools/$it" } +
                    getGlasswareIds().map { "https://mixdrinks.org/glassware/$it" }).map { Url(it) }
        )
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