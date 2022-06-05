package org.mixdrinks.view

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimpleCocktailVM(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
)

@Serializable
data class CompactCocktailVM(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("images")
    val images: List<Image>,
    @SerialName("ingredients")
    val ingredients: List<SimpleIngredient>,
)

@Serializable
data class SimpleIngredient(
    @SerialName("name")
    val name: String,
    @SerialName("images")
    val images: List<Image>,
)

@Serializable
data class Image(
    @SerialName("srcset")
    val src: String,
    @SerialName("media")
    val media: String,
    @SerialName("type")
    val type: String,
)

enum class CocktailView {
    MINI, COMPACT, FULL
}