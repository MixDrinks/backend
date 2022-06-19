package org.mixdrinks.view.tag

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TagVM(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
)
