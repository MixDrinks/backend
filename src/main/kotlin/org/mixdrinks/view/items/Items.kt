package org.mixdrinks.view.items

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

fun Application.items() {
    routing {
        get("items/full") {
            val id = call.request.queryParameters["id"]?.toIntOrNull()

            if (id != null) {
                call.respond(getFullItem(id))
            } else {
                call.respond(HttpStatusCode.BadRequest, "Query id is require, and must be integer")
            }
        }
    }
}

private fun getFullItem(id: Int): ItemVm {
    return transaction {
        val item = ItemsTable.select { ItemsTable.id eq id }.limit(1).first()
        val itemId = item[ItemsTable.id]

        return@transaction ItemVm(
            id = itemId,
            name = item[ItemsTable.name],
            about = item[ItemsTable.about],
            images = buildImages(itemId, ImageType.ITEM),
        )
    }
}