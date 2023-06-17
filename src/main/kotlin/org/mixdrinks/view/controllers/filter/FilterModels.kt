package org.mixdrinks.view.controllers.filter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.FilterQueryName
import org.mixdrinks.dto.SelectionType

class FilterModels {

    @Serializable
    data class FilterGroup(
        @SerialName("id") val id: FilterGroupId,
        @SerialName("queryName") val queryName: FilterQueryName,
        @SerialName("name") val name: String,
        @SerialName("items") val items: List<FilterItem>,
        @SerialName("selectionType") val selectionType: SelectionType,
        @SerialName("sortOrder") val sortOrder: Int,
    ) {
        constructor(filterGroupBackend: FilterGroups, items: List<FilterItem>, sortOrder: Int) : this(
            filterGroupBackend.id,
            filterGroupBackend.queryName,
            filterGroupBackend.translation,
            items,
            filterGroupBackend.selectionType,
            sortOrder
        )
    }

    @Serializable
    data class FilterItem(
        @SerialName("id") val id: FilterId,
        @SerialName("name") val name: String,
        @SerialName("cocktailCount") val cocktailCount: Int,
        @SerialName("slug") val slug: String,
    )
}
