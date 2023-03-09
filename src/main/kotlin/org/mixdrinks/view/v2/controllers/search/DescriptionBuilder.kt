package org.mixdrinks.view.v2.controllers.search

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.AlcoholVolumes
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.Taste
import org.mixdrinks.data.TastesTable
import org.mixdrinks.view.v2.controllers.filter.FilterModels

class DescriptionBuilder {

    fun buildDescription(searchParams: SearchParams): String? {
        val filters = searchParams.filters

        return transaction {
            buildString {
                filters[FilterModels.Filters.ALCOHOL_VOLUME.id]
                    ?.map { it.value }
                    ?.takeIf { it.isNotEmpty() }
                    ?.get(0)
                    ?.let { alcoholVolumeIds ->
                        AlcoholVolumes.findById(alcoholVolumeIds)?.let {
                            append(it.name)
                            append(" ")
                        }
                    }

                filters[FilterModels.Filters.TASTE.id]
                    ?.map { it.value }
                    ?.let { tasteIds ->
                        Taste.find { TastesTable.id inList tasteIds }
                            .joinToString(separator = " ") { it.name }
                            .let {
                                append(it)
                                append(" ")
                            }
                    }

                append("коктейлі")

                filters[FilterModels.Filters.TAGS.id]
                    ?.map { it.value }
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { tagIds ->
                        TagsTable
                            .select {
                                TagsTable.id inList tagIds
                            }
                            .orderBy(TagsTable.id)
                            .joinToString(separator = " ") { it[TagsTable.name] }
                            .let {
                                append(" ")
                                append(it)
                            }
                    }

                filters[FilterModels.Filters.GOODS.id]
                    ?.map { it.value }
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { goodIds ->
                        append(" з ")
                        GoodsTable
                            .select {
                                GoodsTable.id inList goodIds
                            }
                            .joinToString(separator = " ") { it[GoodsTable.name] }
                            .let { append(it) }
                    }

                filters[FilterModels.Filters.GLASSWARE.id]
                    ?.map { it.value }
                    ?.takeIf { it.isNotEmpty() }
                    ?.get(0)
                    ?.let { glasswareId ->
                        Glassware.findById(glasswareId)?.let {
                            append(" в ")
                            append(it.name)
                        }
                    }

            }.takeIf { it.isNotEmpty() && it != "коктейлі" }
        }
    }
}
