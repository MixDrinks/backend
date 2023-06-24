package org.mixdrinks.view.controllers.search

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.AlcoholVolumes
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.Taste
import org.mixdrinks.data.TastesTable
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.dto.FilterId
import org.mixdrinks.view.controllers.search.slug.SearchParams

class DescriptionBuilder {

    fun buildDescription(searchParams: SearchParams): String? {
        val filters = searchParams.filters

        return transaction {
            buildString {
                addAlcoholDescriptionIfExist(filters[FilterGroups.ALCOHOL_VOLUME.id])
                if (filters[FilterGroups.TASTE.id]?.isNotEmpty() == true) {
                    append(", ")
                } else {
                    append(" ")
                }
                addTasteDescriptionIfExist(filters[FilterGroups.TASTE.id])

                append(COCKTAIL_NAME)

                addTagsDescriptionIfExist(filters[FilterGroups.TAGS.id])
                addGoodsDescriptionIfExist(filters[FilterGroups.GOODS.id])
                addGlasswareDescriptionIfExist(filters[FilterGroups.GLASSWARE.id])
            }
                .removePrefix(" ")
                .takeIf { it.isNotEmpty() && it != COCKTAIL_NAME }
        }
    }

    private fun StringBuilder.addGlasswareDescriptionIfExist(glasswareIds: List<FilterId>?) {
        glasswareIds
            ?.map { it.value }
            ?.takeIf { it.isNotEmpty() }
            ?.get(0)
            ?.let { glasswareId ->
                Glassware.findById(glasswareId)?.let {
                    append(" в ")
                    append(it.name.capitalize())
                }
            }
    }

    private fun StringBuilder.addGoodsDescriptionIfExist(goodIds: List<FilterId>?) {
        goodIds
            ?.map { it.value }
            ?.takeIf { it.isNotEmpty() }
            ?.let { safeGoodIds ->
                append(" з ")
                GoodsTable
                    .select {
                        GoodsTable.id inList safeGoodIds
                    }
                    .joinToString(separator = ", ") { it[GoodsTable.name].capitalize() }
                    .let { append(it) }
            }
    }

    private fun StringBuilder.addTagsDescriptionIfExist(tagsId: List<FilterId>?) {
        tagsId
            ?.map { it.value }
            ?.takeIf { it.isNotEmpty() }
            ?.let { safeTagIds ->
                TagsTable
                    .select {
                        TagsTable.id inList safeTagIds
                    }
                    .orderBy(TagsTable.id)
                    .joinToString(separator = ", ") { it[TagsTable.name].capitalize() }
                    .let {
                        append(" ")
                        append(it)
                    }
            }
    }

    private fun StringBuilder.addTasteDescriptionIfExist(tasteId: List<FilterId>?) {
        tasteId
            ?.map { it.value }
            ?.let { tasteIds ->
                Taste.find { TastesTable.id inList tasteIds }
                    .joinToString(separator = ", ") { it.name.capitalize() }
                    .let {
                        append(it)
                        append(" ")
                    }
            }
    }

    private fun StringBuilder.addAlcoholDescriptionIfExist(alcoholFilters: List<FilterId>?) {
        alcoholFilters
            ?.map { it.value }
            ?.takeIf { it.isNotEmpty() }
            ?.get(0)
            ?.let { safeAlcoholVolumeIds ->
                AlcoholVolumes.findById(safeAlcoholVolumeIds)?.let {
                    append(it.name.capitalize())
                }
            }
    }

    companion object {
        private const val COCKTAIL_NAME = "коктейлі"
    }
}
