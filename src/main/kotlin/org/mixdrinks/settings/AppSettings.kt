package org.mixdrinks.settings

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    @SerialName("minVote")
    val minVote: Int,
    @SerialName("maxVote")
    val maxVote: Int,
    @SerialName("pageSize")
    val pageSize: Int,
)
