package org.mixdrinks.view.v2.controllers.tools

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Good
import org.mixdrinks.data.Tool
import org.mixdrinks.view.images.Image
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

fun Application.itemsList() {
    routing {
        get("v2/tools/all") {
            call.respond(transaction {
                Tool.all().map {
                    ToolsVM(
                        id = it.id.value,
                        name = it.name,
                        description = it.about,
                        image = buildImages(it.id.value, ImageType.ITEM)
                    )
                }
            })
        }
        get("v2/goods/all") {
            call.respond(transaction {
                Good.all().map {
                    GoodVM(
                        id = it.id.value,
                        name = it.name,
                        description = it.about,
                        image = buildImages(it.id.value, ImageType.ITEM)
                    )
                }
            })
        }
    }
}

@Serializable
data class GoodVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("image") val image: List<Image>,
)

@Serializable
data class ToolsVM(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("image") val image: List<Image>,
)
