package org.mixdrinks.view

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CocktailVM(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
)