package org.mixdrinks.view.controllers.search.paggination

import io.ktor.server.application.ApplicationCall

fun ApplicationCall.getPage(pageSize: Int): Page? {
    val pageIndex = this.request.queryParameters["page"]?.toIntOrNull() ?: return null
    return Page(
        offset = pageIndex * pageSize, limit = pageSize
    )
}