package org.mixdrinks.view.controllers.items

import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Good
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

fun Routing.good() {
    get("v2/good/{id}") {
        val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Good id is required")
        call.respond(
            transaction {
                Good.findById(id)?.let { good ->
                    val goodId = good.id.value

                    return@transaction ItemVm(
                        id = goodId,
                        name = good.name,
                        about = good.about,
                        images = buildImages(goodId, ImageType.ITEM),
                        slug = good.slug,
                    )
                } ?: throw NotFoundException("Good with id $id not found")
            }
        )
    }
}
