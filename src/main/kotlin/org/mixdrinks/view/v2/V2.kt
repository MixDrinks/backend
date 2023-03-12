package org.mixdrinks.view.v2

import io.ktor.server.application.Application
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.view.cocktail.cocktails
import org.mixdrinks.view.v2.controllers.filter.FilterCache
import org.mixdrinks.view.v2.controllers.filter.FilterSource
import org.mixdrinks.view.v2.controllers.filter.filterMetaInfo
import org.mixdrinks.view.v2.controllers.items.items
import org.mixdrinks.view.v2.controllers.score.RattingBuilder
import org.mixdrinks.view.v2.controllers.score.rattingSearchView
import org.mixdrinks.view.v2.controllers.score.score
import org.mixdrinks.view.v2.controllers.search.DescriptionBuilder
import org.mixdrinks.view.v2.controllers.search.SearchResponseBuilder
import org.mixdrinks.view.v2.controllers.search.searchView
import org.mixdrinks.view.v2.controllers.settings.AppSettings
import org.mixdrinks.view.v2.controllers.settings.appSetting

fun Application.v2(appSettings: AppSettings) {
    val filterCache = FilterCache()
    val cocktailSelector = CocktailSelector(filterCache.filterGroups)
    this.filterMetaInfo(FilterSource(filterCache))
    this.searchView(SearchResponseBuilder(filterCache, cocktailSelector, DescriptionBuilder()), appSettings)
    this.rattingSearchView(RattingBuilder(cocktailSelector), appSettings)
    this.score(appSettings)
    this.cocktails()
    this.items()
    this.appSetting(appSettings)
}
