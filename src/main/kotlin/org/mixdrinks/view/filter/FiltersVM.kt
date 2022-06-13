package org.mixdrinks.view.filter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FiltersVM(
    @SerialName("tags")
    val tags: Map<Int, String>,
    @SerialName("goods")
    val goods: Map<Int, String>,
    @SerialName("tools")
    val tools: Map<Int, String>,
    @SerialName("cocktails")
    val cocktails: Map<Int, String>,
)