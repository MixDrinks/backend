package org.mixdrinks.view.v2.controllers.items

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mixdrinks.view.images.Image

@Serializable
data class ItemVm(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("about")
    val about: String,
    @SerialName("visitCount")
    val visitCount: Int,
    @SerialName("images")
    val images: List<Image>,
)
