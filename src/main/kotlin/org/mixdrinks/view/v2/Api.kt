package org.mixdrinks.view.v2

import io.ktor.server.application.Application
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.view.cocktail.cocktails
import org.mixdrinks.view.controllers.filter.FilterCache
import org.mixdrinks.view.controllers.filter.FilterSource
import org.mixdrinks.view.controllers.filter.filterMetaInfo
import org.mixdrinks.view.controllers.items.items
import org.mixdrinks.view.controllers.score.RattingBuilder
import org.mixdrinks.view.controllers.score.rattingSearchView
import org.mixdrinks.view.controllers.score.score
import org.mixdrinks.view.controllers.search.DescriptionBuilder
import org.mixdrinks.view.controllers.search.SearchResponseBuilder
import org.mixdrinks.view.controllers.search.searchView
import org.mixdrinks.view.controllers.search.slug.SearchSlugResponseBuilder
import org.mixdrinks.view.controllers.search.slug.filterSlugs
import org.mixdrinks.view.controllers.settings.AppSettings
import org.mixdrinks.view.controllers.settings.appSetting
import org.mixdrinks.view.snapshot.SnapshotCreator
import org.mixdrinks.view.snapshot.sitemap.SiteMapCreator
import org.mixdrinks.view.snapshot.snapshot

fun Application.api(appSettings: AppSettings) {
    val filterCache = FilterCache()
    val cocktailSelector = CocktailSelector(filterCache.filterGroups)
    val snapshotCreator = SnapshotCreator(filterCache)
    this.filterMetaInfo(FilterSource(filterCache))

    val searchResponseBuilder = SearchResponseBuilder(filterCache, cocktailSelector, DescriptionBuilder())

    this.searchView(searchResponseBuilder, appSettings)
    this.rattingSearchView(RattingBuilder(cocktailSelector), appSettings)
    this.score(appSettings)
    this.cocktails()
    this.items()
    this.appSetting(appSettings)
    this.snapshot(snapshotCreator, SiteMapCreator())

    val searchSlugResponseBuilder = SearchSlugResponseBuilder(filterCache, searchResponseBuilder)

    this.filterSlugs(searchSlugResponseBuilder, appSettings)
}
