package org.mixdrinks.view.v2.controllers.search

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
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
import org.mixdrinks.view.v2.controllers.filter.FilterModels
import org.mixdrinks.view.v2.data.CocktailId

class CocktailsSourceV2 {

    private val cache: Map<CocktailId, Map<FilterModels.FilterGroupId, List<FilterModels.FilterId>>>

    val filterIds: Map<FilterModels.Filters, List<FilterModels.FilterId>> = transaction {
        return@transaction mapOf(
            FilterModels.Filters.TAGS to TagsTable.selectAll()
                .map { FilterModels.FilterId(it[TagsTable.id].value) },
            FilterModels.Filters.GOODS to GoodsTable.selectAll()
                .map { FilterModels.FilterId(it[GoodsTable.id].value) },
            FilterModels.Filters.TOOLS to ToolsTable.selectAll()
                .map { FilterModels.FilterId(it[ToolsTable.id].value) },
            FilterModels.Filters.TASTE to TastesTable.selectAll()
                .map { FilterModels.FilterId(it[TastesTable.id].value) },
            FilterModels.Filters.ALCOHOL_VOLUME to AlcoholVolumesTable.selectAll()
                .map { FilterModels.FilterId(it[AlcoholVolumesTable.id].value) },
            FilterModels.Filters.GLASSWARE to GlasswareTable.selectAll()
                .map { FilterModels.FilterId(it[GlasswareTable.id].value) },
        )
    }

    init {
        cache = transaction {
            val cocktailIds =
                CocktailsTable.slice(CocktailsTable.id).selectAll().map { CocktailId(it[CocktailsTable.id].value) }

            return@transaction cocktailIds.associateWith { cocktailId ->
                val alcoholVolume =
                    CocktailsToAlcoholVolumesTable
                        .select { CocktailsToAlcoholVolumesTable.cocktailId eq cocktailId.value }
                        .map { FilterModels.FilterId(it[CocktailsToAlcoholVolumesTable.alcoholVolumeId]) }

                val tagIds = CocktailToTagTable
                    .select { CocktailToTagTable.cocktailId eq cocktailId.value }
                    .map { FilterModels.FilterId(it[CocktailToTagTable.tagId]) }

                val goodIds = getItemIds(cocktailId)

                val toolIds = CocktailsToToolsTable
                    .select { CocktailsToToolsTable.cocktailId eq cocktailId.value }
                    .map { FilterModels.FilterId(it[CocktailsToToolsTable.toolId].value) }

                val tasteIds = CocktailsToTastesTable
                    .select { CocktailsToTastesTable.cocktailId eq cocktailId.value }
                    .map { FilterModels.FilterId(it[CocktailsToTastesTable.tasteId].value) }

                val glassware = CocktailsToGlasswareTable
                    .select { CocktailsToGlasswareTable.cocktailId eq cocktailId.value }
                    .map { FilterModels.FilterId(it[CocktailsToGlasswareTable.glasswareId].value) }

                return@associateWith mapOf(
                    FilterModels.Filters.TAGS.id to tagIds,
                    FilterModels.Filters.GOODS.id to goodIds,
                    FilterModels.Filters.TOOLS.id to toolIds,
                    FilterModels.Filters.TASTE.id to tasteIds,
                    FilterModels.Filters.ALCOHOL_VOLUME.id to alcoholVolume,
                    FilterModels.Filters.GLASSWARE.id to glassware,
                )
            }
        }
    }

    private fun getItemIds(cocktailId: CocktailId) =
        CocktailsToGoodsTable.slice(CocktailsToGoodsTable.cocktailId, CocktailsToGoodsTable.goodId)
            .select { CocktailsToGoodsTable.cocktailId eq cocktailId.value }
            .map { FilterModels.FilterId(it[CocktailsToGoodsTable.goodId].value) }

    fun cocktailsBySearch(searchParams: SearchParams): List<CocktailId> {
        return cache.filter { (_, meta) ->
            return@filter searchParams.filters.all { (groupId, filterIds) ->
                meta[groupId]!!.containsAll(filterIds)
            }
        }.map { it.key }
    }
}
