package org.mixdrinks.view.controllers.search

import io.ktor.server.application.ApplicationCall
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.error.SortTypeNotFound

fun ApplicationCall.getSortType(): SortType {
    val rawSortType = this.request.queryParameters["sort"]

    return if (rawSortType != null) {
        SortType.values().firstOrNull { it.key == rawSortType } ?: throw SortTypeNotFound()
    } else {
        SortType.MOST_POPULAR
    }
}

