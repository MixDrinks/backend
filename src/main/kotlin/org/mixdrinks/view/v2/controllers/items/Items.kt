package org.mixdrinks.view.v2.controllers.items

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Good
import org.mixdrinks.data.Tool
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

fun Application.items() {
    routing {
        get("v2/items/full") {
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
        Good.findById(id)?.let { item ->
            val itemId = item.id.value

            return@transaction ItemVm(
                id = itemId,
                name = item.name,
                about = item.about,
                visitCount = item.visitCount,
                images = buildImages(itemId, ImageType.ITEM),
            )
        } ?: let {
            Tool.findById(id)?.let { tool ->
                val toolId = tool.id.value

                return@transaction ItemVm(
                    id = toolId,
                    name = tool.name,
                    about = tool.about,
                    visitCount = tool.visitCount,
                    images = buildImages(toolId, ImageType.ITEM),
                )
            } ?: throw NotFoundException("Item with id $id not found")
        }
    }
}
