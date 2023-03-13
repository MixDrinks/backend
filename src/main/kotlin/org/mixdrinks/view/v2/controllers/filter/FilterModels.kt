package org.mixdrinks.view.v2.controllers.filter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SelectionType

class FilterModels {

    @JvmInline
    @Serializable
    value class FilterQueryName(val value: String)

    @Suppress("MagicNumber")
    enum class Filters(
        val id: FilterGroupId,
        val queryName: FilterQueryName,
        val translation: String,
        val selectionType: SelectionType,
    ) {
        TAGS(
            id = FilterGroupId(0),
            queryName = FilterQueryName("tags"),
            translation = "Інше",
            selectionType = SelectionType.MULTIPLE,
        ),
        GOODS(
            id = FilterGroupId(1),
            queryName = FilterQueryName("goods"),
            translation = "Інгрідієнти",
            selectionType = SelectionType.MULTIPLE,
        ),
        TOOLS(
            id = FilterGroupId(2),
            queryName = FilterQueryName("tools"),
            translation = "Приладдя",
            selectionType = SelectionType.MULTIPLE,
        ),
        TASTE(
            id = FilterGroupId(3),
            queryName = FilterQueryName("taste"),
            translation = "Смак",
            selectionType = SelectionType.MULTIPLE,
        ),
        ALCOHOL_VOLUME(
            id = FilterGroupId(4),
            queryName = FilterQueryName("alcohol_volume"),
            translation = "Алкоголь",
            selectionType = SelectionType.SINGLE,
        ),
        GLASSWARE(
            id = FilterGroupId(5),
            queryName = FilterQueryName("glassware"),
            translation = "Стакан",
            selectionType = SelectionType.SINGLE,
        )
    }

    @Serializable
    data class FilterGroup(
        @SerialName("id") val id: FilterGroupId,
        @SerialName("queryName") val queryName: FilterQueryName,
        @SerialName("name") val name: String,
        @SerialName("items") val items: List<FilterItem>,
        @SerialName("selectionType") val selectionType: SelectionType,
    ) {
        constructor(filters: Filters, items: List<FilterItem>) : this(
            filters.id, filters.queryName, filters.translation, items, filters.selectionType
        )
    }

    @Serializable
    data class FilterItem(
        @SerialName("id") val id: FilterId,
        @SerialName("name") val name: String,
        @SerialName("cocktailCount") val cocktailCount: Int,
    )
}
