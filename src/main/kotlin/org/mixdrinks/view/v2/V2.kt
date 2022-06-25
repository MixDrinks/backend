package org.mixdrinks.view.v2

import io.ktor.server.application.Application
import org.mixdrinks.settings.AppSettings
import org.mixdrinks.view.v2.controllers.filter.FilterSource
import org.mixdrinks.view.v2.controllers.filter.filterMetaInfo
import org.mixdrinks.view.v2.controllers.search.CocktailsSourceV2
import org.mixdrinks.view.v2.controllers.search.SearchResponseBuilder
import org.mixdrinks.view.v2.controllers.search.searchView

fun Application.v2(appSettings: AppSettings) {
    this.filterMetaInfo(FilterSource())
    this.searchView(SearchResponseBuilder(CocktailsSourceV2()), appSettings)
}
