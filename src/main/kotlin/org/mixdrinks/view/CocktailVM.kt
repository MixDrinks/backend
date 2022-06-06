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
    @SerialName("goods")
    val goods: List<SimpleIngredient>,
    @SerialName("tags")
    val tags: List<TagVM>,
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

@Serializable
data class TagVM(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
)