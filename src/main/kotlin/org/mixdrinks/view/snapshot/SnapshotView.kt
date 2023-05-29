package org.mixdrinks.view.snapshot

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.mixdrinks.view.snapshot.sitemap.SiteMapCreator

fun Application.snapshot(
    snapshotCreator: SnapshotCreator,
    siteMapCreator: SiteMapCreator,
) {
    routing {
        /**
         * Return the snapshot of the current state of the database as a JSON object.
         * Includes all the ingredients, recipes, goods, cocktails, ect.
         */
        get("snapshot") {
            call.respond(snapshotCreator.getSnapshot())
        }
        /**
         * Return the snapshot of the current state of the database as a JSON object.
         * Includes all the ingredients, recipes, goods, cocktails, ect.
         */
        get("v2/snapshot") {
            call.respond(snapshotCreator.getSnapshot())
        }

        get("sitemap.xml") {
            call.respond(siteMapCreator.siteMapDto)
        }
    }
}
