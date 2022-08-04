package org.mixdrinks.view.v2

import io.ktor.server.application.Application
import org.mixdrinks.settings.AppSettings
import org.mixdrinks.view.v2.controllers.filter.FilterSource
import org.mixdrinks.view.v2.controllers.filter.filterMetaInfo
import org.mixdrinks.view.v2.controllers.score.RattingBuilder
import org.mixdrinks.view.v2.controllers.score.item.itemScoreV2
import org.mixdrinks.view.v2.controllers.score.rattingSearchView
import org.mixdrinks.view.v2.controllers.score.scoreV2
import org.mixdrinks.view.v2.controllers.search.CocktailsSourceV2
import org.mixdrinks.view.v2.controllers.search.DescriptionBuilder
import org.mixdrinks.view.v2.controllers.search.SearchResponseBuilder
import org.mixdrinks.view.v2.controllers.search.searchView
import org.mixdrinks.view.v2.controllers.snapshot.snapshot
import org.mixdrinks.view.v2.controllers.tools.itemsList

fun Application.v2(appSettings: AppSettings) {
    val cocktailsSource = CocktailsSourceV2()
    this.filterMetaInfo(FilterSource())
    this.searchView(SearchResponseBuilder(cocktailsSource, DescriptionBuilder()), appSettings)
    this.rattingSearchView(RattingBuilder(cocktailsSource), appSettings)
    this.scoreV2(appSettings)
    this.itemScoreV2()
    this.itemsList()
    this.snapshot()
}
