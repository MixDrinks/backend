package org.mixdrinks.view.controllers.items

import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Good
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.domain.FilterGroups
import org.mixdrinks.view.images.ImageType
import org.mixdrinks.view.images.buildImages

fun Routing.good() {
    legacy()
    get("v3/${FilterGroups.GOODS.queryName.value}/{slug}") {
        val slug = call.parameters["slug"] ?: throw BadRequestException("Good slug is required")
        call.respond(
            transaction {
                Good.find { GoodsTable.slug eq slug }.firstOrNull()?.let { good ->
                    val goodId = good.id.value

                    return@transaction ItemVm(
                        id = goodId,
                        name = good.name,
                        about = good.about,
                        images = buildImages(goodId, ImageType.ITEM),
                        slug = good.slug,
                    )
                } ?: throw NotFoundException("Good with slug $slug not found")
            }
        )
    }
}

private fun Routing.legacy() {
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
