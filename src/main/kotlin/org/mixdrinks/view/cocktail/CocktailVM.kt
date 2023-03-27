package org.mixdrinks.view.cocktail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.TagId
import org.mixdrinks.view.images.Image

@Serializable
data class SimpleCocktailVM(
    @SerialName("id") val id: CocktailId,
    @SerialName("name") val name: String,
)

@Serializable
data class CompactCocktailVM(
    @SerialName("id") val id: CocktailId,
    @SerialName("name") val name: String,
    @SerialName("rating") val rating: Float?,
    @SerialName("visitCount") val visitCount: Int,
    @SerialName("images") val images: List<Image>,
    @SerialName("slug") val slug: String,
)

@Serializable
data class FullCocktailVM(
    @SerialName("id") val id: CocktailId,
    @SerialName("name") val name: String,
    @SerialName("visitCount") val visitCount: Int,
    @SerialName("rating") val rating: Float?,
    @SerialName("ratingCount") val ratingCount: Int,
    @SerialName("receipt") val receipt: List<String>,
    @SerialName("images") val images: List<Image>,
    @SerialName("goods") val goods: List<FullGood>,
    @SerialName("tools") val tools: List<ToolVM>,
    @SerialName("tags") val tags: List<TagVM>,
    @SerialName("slug") val slug: String,
)

@Serializable
data class FullGood(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("images") val images: List<Image>,
    @SerialName("amount") val amount: Int,
    @SerialName("unit") val unit: String,
    @SerialName("url") val path: String,
    @SerialName("slug") val slug: String,
)

@Serializable
data class ToolVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("images") val images: List<Image>,
    @SerialName("url") val path: String,
    @SerialName("slug") val slug: String,
)

@Serializable
data class TagVM(
    @SerialName("id") val id: TagId,
    @SerialName("name") val name: String,
    @SerialName("url") val path: String,
    @SerialName("slug") val slug: String,
)
