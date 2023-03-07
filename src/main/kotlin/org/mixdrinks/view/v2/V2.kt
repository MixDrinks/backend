package org.mixdrinks.view.v2

import io.ktor.server.application.Application
import org.mixdrinks.view.cocktail.cocktails
import org.mixdrinks.view.v2.controllers.filter.FilterSource
import org.mixdrinks.view.v2.controllers.filter.filterMetaInfo
import org.mixdrinks.view.v2.controllers.items.items
import org.mixdrinks.view.v2.controllers.score.RattingBuilder
import org.mixdrinks.view.v2.controllers.score.item.itemScore
import org.mixdrinks.view.v2.controllers.score.rattingSearchView
import org.mixdrinks.view.v2.controllers.score.score
import org.mixdrinks.view.v2.controllers.search.CocktailsSourceV2
import org.mixdrinks.view.v2.controllers.search.DescriptionBuilder
import org.mixdrinks.view.v2.controllers.search.SearchResponseBuilder
import org.mixdrinks.view.v2.controllers.search.searchView
import org.mixdrinks.view.v2.controllers.settings.AppSettings
import org.mixdrinks.view.v2.controllers.settings.appSetting
import org.mixdrinks.view.v2.controllers.tools.itemsList

fun Application.v2(appSettings: AppSettings) {
    val cocktailsSource = CocktailsSourceV2()
    this.filterMetaInfo(FilterSource())
    this.searchView(SearchResponseBuilder(cocktailsSource, DescriptionBuilder()), appSettings)
    this.rattingSearchView(RattingBuilder(cocktailsSource), appSettings)
    this.score(appSettings)
    this.itemScore()
    this.itemsList()
    this.cocktails()
    this.items()
    this.appSetting(appSettings)
}
