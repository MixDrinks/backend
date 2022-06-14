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
data class FilterResultVM(
    @SerialName("totalCount") val totalCount: Int,
    @SerialName("cocktails") val cocktails: List<CompactCocktailVM>,
    @SerialName("cocktailsByTagCounts") val cocktailsByTagCounts: Map<Int, Int>,
    @SerialName("cocktailsByGoodCounts") val cocktailsByGoodCounts: Map<Int, Int>,
    @SerialName("cocktailsByToolCounts") val cocktailsByToolCounts: Map<Int, Int>,
)

@Serializable
data class CompactCocktailVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("images") val images: List<Image>,
)

@Serializable
data class FullCocktailVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("visitCount") val visitCount: Int,
    @SerialName("rating") val rating: Float,
    @SerialName("receipt") val receipt: List<String>,
    @SerialName("images") val images: List<Image>,
    @SerialName("goods") val goods: List<FullIngredient>,
    @SerialName("tools") val tools: List<FullIngredient>,
    @SerialName("tags") val tags: List<TagVM>,
)

@Serializable
data class SimpleIngredient(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("images") val images: List<Image>,
)

@Serializable
data class FullIngredient(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("images") val images: List<Image>,
    @SerialName("amount") val amount: Int,
    @SerialName("unit") val unit: String,
)
