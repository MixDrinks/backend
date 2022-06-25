package org.mixdrinks.view.v2.controllers.filter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class FilterModels {

    @JvmInline
    @Serializable
    value class FilterGroupId(val value: Int)

    @JvmInline
    @Serializable
    value class FilterId(val value: Int)

    @JvmInline
    @Serializable
    value class FilterQueryName(val value: String)

    enum class Filters(val id: FilterGroupId, val queryName: FilterQueryName, val translation: String) {
        TAGS(
            id = FilterGroupId(0), queryName = FilterQueryName("tags"), translation = "Інше",
        ),
        GOODS(
            id = FilterGroupId(1), queryName = FilterQueryName("goods"), translation = "Інгрідієнти"
        ),
        TOOLS(
            id = FilterGroupId(2),
            queryName = FilterQueryName("tools"),
            translation = "Приладдя",
        ),
    }

    @Serializable
    data class FilterGroup(
        @SerialName("id") val id: FilterGroupId,
        @SerialName("queryName") val queryName: FilterQueryName,
        @SerialName("name") val name: String,
        @SerialName("items") val items: List<FilterItem>,
    ) {
        constructor(filters: Filters, items: List<FilterItem>) : this(
            filters.id, filters.queryName, filters.translation, items
        )
    }

    @Serializable
    data class FilterItem(
        @SerialName("id") val id: FilterId,
        @SerialName("name") val name: String,
        @SerialName("cocktailCount") val cocktailCount: Long,
    )
}