package org.mixdrinks.view.tag

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
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
