package org.mixdrinks.view.images

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    @SerialName("srcset") val src: String,
    @SerialName("media") val media: String,
    @SerialName("type") val type: String,
)

enum class ImageType(val imagePrefix: String) {
    COCKTAIL("cocktails"), ITEM("goods")
}

fun buildImages(id: Int, type: ImageType): List<Image> {
    data class SizeDep(
        val responseSize: String,
        val imageSize: String,
    )

    val domain = "images.mixdrinks.org"
    return listOf("webp", "jpg").map { format ->
        listOf(
            SizeDep("570", "origin"),
            SizeDep("410", "560"),
            SizeDep("330", "400"),
            SizeDep("0", "320"),
        ).map { size ->
            Image(
                src = "https://$domain/${type.imagePrefix}/$id/${size.imageSize}/$id.$format",
                media = "screen and (min-width: ${size.responseSize}px)",
                type = "image/$format"
            )
        }
    }.flatten()
}
