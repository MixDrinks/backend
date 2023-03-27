package org.mixdrinks.view.controllers.filter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.dto.SelectionType

class FilterModels {

    @JvmInline
    @Serializable
    value class FilterQueryName(val value: String)

    @Suppress("MagicNumber")
    enum class FilterGroupBackend(
        val id: FilterGroupId,
        val queryName: FilterQueryName,
        val translation: String,
        val selectionType: SelectionType,
        val sortOrder: Int,
    ) {
        TAGS(
            id = FilterGroups.TAGS.id,
            queryName = FilterQueryName("tags"),
            translation = "Інше",
            selectionType = SelectionType.MULTIPLE,
            sortOrder = 5,
        ),
        GOODS(
            id = FilterGroups.GOODS.id,
            queryName = FilterQueryName("goods"),
            translation = "Інгрідієнти",
            selectionType = SelectionType.MULTIPLE,
            sortOrder = 1,
        ),
        TOOLS(
            id = FilterGroups.TOOLS.id,
            queryName = FilterQueryName("tools"),
            translation = "Приладдя",
            selectionType = SelectionType.MULTIPLE,
            sortOrder = 4,
        ),
        TASTE(
            id = FilterGroups.TASTE.id,
            queryName = FilterQueryName("taste"),
            translation = "Смак",
            selectionType = SelectionType.MULTIPLE,
            sortOrder = 2,
        ),
        ALCOHOL_VOLUME(
            id = FilterGroups.ALCOHOL_VOLUME.id,
            queryName = FilterQueryName("alcohol-volume"),
            translation = "Алкоголь",
            selectionType = SelectionType.SINGLE,
            sortOrder = 1,
        ),
        GLASSWARE(
            id = FilterGroups.GLASSWARE.id,
            queryName = FilterQueryName("glassware"),
            translation = "Стакан",
            selectionType = SelectionType.SINGLE,
            sortOrder = 3,
        )
    }

    @Serializable
    data class FilterGroup(
        @SerialName("id") val id: FilterGroupId,
        @SerialName("queryName") val queryName: FilterQueryName,
        @SerialName("name") val name: String,
        @SerialName("items") val items: List<FilterItem>,
        @SerialName("selectionType") val selectionType: SelectionType,
        @SerialName("sortOrder") val sortOrder: Int,
    ) {
        constructor(filterGroupBackend: FilterGroupBackend, items: List<FilterItem>, sortOrder: Int) : this(
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
