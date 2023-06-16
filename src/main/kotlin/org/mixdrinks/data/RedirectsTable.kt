package org.mixdrinks.data

import org.jetbrains.exposed.sql.Table

object RedirectsTable : Table(name = "redirects") {
    val from = text("from")
    val to = text("to")
}
