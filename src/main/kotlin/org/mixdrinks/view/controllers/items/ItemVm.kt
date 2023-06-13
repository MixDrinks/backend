package org.mixdrinks.view.controllers.items

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.view.images.Image

@Serializable
data class ItemVm(
    @SerialName("id") val id: Int,
    @SerialName("slug") val slug: String,
    @SerialName("name") val name: String,
    @SerialName("about") val about: String,
    @SerialName("images") val images: List<Image>,
)
