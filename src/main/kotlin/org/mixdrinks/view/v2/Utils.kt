package org.mixdrinks.view.v2

import io.ktor.server.application.ApplicationCall
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.view.error.QueryRequireException

fun ApplicationCall.getCocktailId(): CocktailId {
    val id = this.request.queryParameters["id"]?.toIntOrNull() ?: throw QueryRequireException("id")
    return CocktailId(id)
}
