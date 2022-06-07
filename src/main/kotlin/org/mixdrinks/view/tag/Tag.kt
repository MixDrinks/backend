package org.mixdrinks.view.tag

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.TagsTable

fun Application.tags() {
    routing {
        get("tags/all") {
            call.respond(transaction {
                TagsTable.selectAll().orderBy(TagsTable.id)
                    .map { row ->
                        TagVM(
                            id = row[TagsTable.id],
                            name = row[TagsTable.name],
                        )
                    }
            })
        }
    }
}

