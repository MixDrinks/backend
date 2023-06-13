package org.mixdrinks.view.controllers.items

import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.GlasswareTable
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.view.controllers.filter.FilterModels
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

fun Routing.glassware() {
    legacy()
    get("v3/${FilterModels.FilterGroupBackend.GLASSWARE.queryName.value}/{slug}") {
        val slug = call.parameters["slug"] ?: throw BadRequestException("Glassware slug is required")

        call.respond(
            transaction {
                Glassware.find { GlasswareTable.slug eq slug }.firstOrNull()?.let { glassware ->
                    val glasswareId = glassware.id.value

                    return@transaction ItemVm(
                        id = glasswareId,
                        name = glassware.name,
                        about = glassware.about,
                        images = buildImages(glasswareId, ImageType.ITEM),
                        slug = glassware.slug,
                    )
                } ?: throw NotFoundException("Glassware with slug $slug not found")
            }
        )
    }
}

private fun Routing.legacy() {
    get("v2/glassware/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Tool id is required")
        call.respond(
            transaction {
                Glassware.findById(id)?.let { glassware ->
                    val glasswareId = glassware.id.value

                    return@transaction ItemVm(
                        id = glasswareId,
                        name = glassware.name,
                        about = glassware.about,
                        images = buildImages(glasswareId, ImageType.ITEM),
                        slug = glassware.slug,
                    )
                } ?: throw NotFoundException("Glassware with id $id not found")
            }
        )
    }
}
