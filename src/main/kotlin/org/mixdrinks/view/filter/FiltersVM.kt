package org.mixdrinks.view.filter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FiltersVM(
    @SerialName("goods")
    val goods: List<FilterProperty>,
    @SerialName("tools")
    val tools: List<FilterProperty>,
    @SerialName("tags")
    val tags: List<FilterProperty>,
)

@Serializable
data class FilterProperty(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String
)
