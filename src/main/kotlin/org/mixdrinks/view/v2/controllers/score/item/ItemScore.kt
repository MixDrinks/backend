package org.mixdrinks.view.v2.controllers.score.item

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.data.GoodsTable.visitCount
import org.mixdrinks.view.error.QueryRequireException
import org.mixdrinks.view.v2.data.ItemId

fun Application.itemScore() {
    routing {
        post("v2/item/visit") {
            val id = call.getItemId()

            call.respond(transaction {
                GoodsTable.update({ GoodsTable.id eq id.value }) {
                    it[visitCount] = visitCount + 1
                }

                return@transaction scoreItemChangeResponse(id)
            })
        }

    }
}

@Serializable
data class ItemScoreChangeResponse(
    @SerialName("itemId") val cocktailId: ItemId,
    @SerialName("visitCount") val visitCount: Int,
)

private fun scoreItemChangeResponse(id: ItemId): ItemScoreChangeResponse {
    return GoodsTable.select { GoodsTable.id eq id.value }.firstOrNull()?.let {
        ItemScoreChangeResponse(
            cocktailId = id, visitCount = it[visitCount]
        )
    } ?: throw QueryRequireException("Item not found")
}

private fun ApplicationCall.getItemId(): ItemId {
    val id = this.request.queryParameters["id"]?.toIntOrNull() ?: throw QueryRequireException("id")
    return ItemId(id)
}
