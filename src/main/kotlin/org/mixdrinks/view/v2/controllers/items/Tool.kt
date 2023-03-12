package org.mixdrinks.view.v2.controllers.items

import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Tool
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

fun Routing.tool() {
    get("v2/tool/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Tool id is required")
        call.respond(
            transaction {
                Tool.findById(id)?.let { tool ->
                    val toolId = tool.id.value

                    return@transaction ItemVm(
                        id = toolId,
                        name = tool.name,
                        about = tool.about,
                        images = buildImages(toolId, ImageType.ITEM),
                    )
                } ?: throw NotFoundException("Tool with id $id not found")
            }
        )
    }
}