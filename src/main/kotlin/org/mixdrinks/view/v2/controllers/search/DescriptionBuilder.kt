package org.mixdrinks.view.v2.controllers.search

import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.view.cocktail.ItemType
import org.mixdrinks.view.v2.controllers.filter.FilterModels

class DescriptionBuilder {

    fun buildDescription(searchParams: SearchParams): String? {
        if (searchParams.filters.size != 1) {
            return null
        }

        searchParams.filters.keys.first().let { key ->
            return when (key) {
                FilterModels.Filters.TAGS.id -> buildTagDescription(searchParams.filters[key].orEmpty())
                FilterModels.Filters.GOODS.id -> buildGoodDescription(searchParams.filters[key].orEmpty())
                FilterModels.Filters.TASTE.id -> buildGoodDescription(searchParams.filters[key].orEmpty())
                FilterModels.Filters.ALCOHOL_VOLUME.id -> buildGoodDescription(searchParams.filters[key].orEmpty())
                FilterModels.Filters.TOOLS.id -> null
                else -> null
            }
        }
    }

    private fun buildTagDescription(tagId: List<FilterModels.FilterId>): String? {
        if (tagId.size != 1) {
            return null
        }

        val description = transaction {
            TagsTable.select { TagsTable.id eq tagId.first().value }.firstOrNull()?.get(TagsTable.name)
        }

        return "Коктейлі $description"
    }

    private fun buildGoodDescription(goodIds: List<FilterModels.FilterId>): String {
        val description = transaction {
            ItemsTable
                .select {
                    ItemsTable.id inList goodIds.map(FilterModels.FilterId::value) and
                            (ItemsTable.relation eq ItemType.GOOD.relation)
                }
                .joinToString(separator = ", ") { it[ItemsTable.name] }
        }

        return "Коктейлі з $description"
    }
}
