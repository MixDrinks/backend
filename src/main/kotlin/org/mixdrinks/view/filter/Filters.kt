package org.mixdrinks.view.filter

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.view.cocktail.ItemType

fun Application.filters() {
    routing {
        get("meta/all") {
            call.respond(transaction {

                FiltersVM(
                    tags = TagsTable
                        .selectAll().orderBy(TagsTable.id)
                        .associate { tagRow ->
                            Pair(tagRow[TagsTable.id], tagRow[TagsTable.name])
                        },
                    goods = ItemsTable
                        .select { ItemsTable.relation eq ItemType.GOOD.relation }.orderBy(ItemsTable.id)
                        .associate { goodRow ->
                            Pair(goodRow[ItemsTable.id], goodRow[ItemsTable.name])
                        },
                    tools = ItemsTable
                        .select { ItemsTable.relation eq ItemType.TOOL.relation }.orderBy(ItemsTable.id)
                        .associate { toolRow ->
                            Pair(toolRow[ItemsTable.id], toolRow[ItemsTable.name])
                        }
                )
            })
        }
    }
}