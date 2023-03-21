package org.mixdrinks.view.snapshot

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.snapshot(
    snapshotCreator: SnapshotCreator,
) {
    routing {
        get("snapshot") {
            call.respond(snapshotCreator.getSnapshot())
        }
        get("v2/snapshot") {
            call.respond(snapshotCreator.getSnapshot())
        }
    }
}
