package org.mixdrinks.view.v2.controllers.tools

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.view.cocktail.ItemType
import org.mixdrinks.view.images.Image
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

fun Application.itemsList() {
    routing {
        get("v2/tools/all") {
            call.respond(transaction {
                ItemsTable.select { ItemsTable.relation eq ItemType.TOOL.relation }.map(ResultRow::toItem)
            })
        }
        get("v2/goods/all") {
            call.respond(transaction {
                ItemsTable.select { ItemsTable.relation eq ItemType.GOOD.relation }.map(ResultRow::toItem)
            })
        }
    }
}

private fun ResultRow.toItem(): Item {
    val id = this[ItemsTable.id]
    return Item(
        id = id,
        name = this[ItemsTable.name],
        description = this[ItemsTable.about],
        image = buildImages(id, ImageType.ITEM)
    )
}

@Serializable
data class Item(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("image") val image: List<Image>,
)
