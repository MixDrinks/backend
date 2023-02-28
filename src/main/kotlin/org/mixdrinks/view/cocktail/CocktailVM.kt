package org.mixdrinks.view.cocktail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.view.images.Image
import org.mixdrinks.view.v2.data.TagId

@Serializable
data class SimpleCocktailVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
)

@Serializable
data class CompactCocktailVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("rating") val rating: Float?,
    @SerialName("visitCount") val visitCount: Int,
    @SerialName("images") val images: List<Image>,
)

@Serializable
data class FullCocktailVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("visitCount") val visitCount: Int,
    @SerialName("rating") val rating: Float?,
    @SerialName("ratingCount") val ratingCount: Int,
    @SerialName("receipt") val receipt: List<String>,
    @SerialName("images") val images: List<Image>,
    @SerialName("goods") val goods: List<FullIngredient>,
    @SerialName("tools") val tools: List<FullIngredient>,
    @SerialName("tags") val tags: List<TagVM>,
    @SerialName("tastes") val tastes: List<TagVM>,
)

@Serializable
data class FullIngredient(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("images") val images: List<Image>,
    @SerialName("amount") val amount: Int,
    @SerialName("unit") val unit: String,
)

@Serializable
data class TagVM(
    @SerialName("id")
    val id: TagId,
    @SerialName("name")
    val name: String,
)
