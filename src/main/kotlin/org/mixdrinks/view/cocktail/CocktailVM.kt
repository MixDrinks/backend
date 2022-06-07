package org.mixdrinks.view.cocktail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.view.images.Image
import org.mixdrinks.view.tag.TagVM

@Serializable
data class SimpleCocktailVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
)

@Serializable
data class CompactCocktailVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("images") val images: List<Image>,
    @SerialName("goods") val goods: List<SimpleIngredient>,
    @SerialName("tags") val tags: List<TagVM>,
)

@Serializable
data class FullCocktailVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("receipt") val receipt: List<String>,
    @SerialName("images") val images: List<Image>,
    @SerialName("goods") val goods: List<SimpleIngredient>,
    @SerialName("tools") val tools: List<SimpleIngredient>,
    @SerialName("tags") val tags: List<TagVM>,
)

@Serializable
data class SimpleIngredient(
    @SerialName("name") val name: String,
    @SerialName("images") val images: List<Image>,
)
