package org.mixdrinks.view

import io.kotest.core.spec.style.FunSpec
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import io.mockk.Matcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.controllers.filter.FilterModels
import org.mixdrinks.view.controllers.search.SearchResponse
import org.mixdrinks.view.controllers.search.slug.SearchParamsSlugs
import org.mixdrinks.view.controllers.search.slug.SearchSlugResponseBuilder
import org.mixdrinks.view.controllers.search.slug.filterSlugs
import org.mixdrinks.view.controllers.settings.AppSettings

class FilterViewV2Tests : FunSpec({
    test("verify one tag (one filer id in one filter group)") {
        val tag1 = "tag-1"
        setupAppAndVerify(
            "${FilterModels.FilterGroupBackend.TAGS.queryName.value}=$tag1",
            mapOf(
                FilterModels.FilterGroupBackend.TAGS to listOf(tag1)
            )
        )
    }

    test("verify two tags (two filer ids in one filter group)") {
        val tag1 = "tag-1"
        val tag2 = "tag-2"
        setupAppAndVerify(
            "${FilterModels.FilterGroupBackend.TAGS.queryName.value}=$tag1,$tag2",
            mapOf(
                FilterModels.FilterGroupBackend.TAGS to listOf(tag1, tag2)
            )
        )
    }

    test("verify two groups with two tags (two filer ids in one filter group)") {
        val tag1 = "tag-1"
        val tag2 = "tag-2"
        val good1 = "good-1"
        val good2 = "good-2"
        setupAppAndVerify(
            "${FilterModels.FilterGroupBackend.TAGS.queryName.value}=$tag1,$tag2/" +
                    "${FilterModels.FilterGroupBackend.GOODS.queryName.value}=$good1,$good2",
            mapOf(
                FilterModels.FilterGroupBackend.TAGS to listOf(tag1, tag2),
                FilterModels.FilterGroupBackend.GOODS to listOf(good1, good2),
            )
        )
    }

    test("verify three groups with two tags in first and second and one into last") {
        val tag1 = "tag-1"
        val tag2 = "tag-2"
        val good1 = "good-1"
        val good2 = "good-2"
        val alcohol1 = "alcohol-1"
        setupAppAndVerify(
            "${FilterModels.FilterGroupBackend.TAGS.queryName.value}=$tag1,$tag2/" +
                    "${FilterModels.FilterGroupBackend.GOODS.queryName.value}=$good1,$good2/" +
                    "${FilterModels.FilterGroupBackend.ALCOHOL_VOLUME.queryName.value}=$alcohol1",
            mapOf(
                FilterModels.FilterGroupBackend.TAGS to listOf(tag1, tag2),
                FilterModels.FilterGroupBackend.GOODS to listOf(good1, good2),
                FilterModels.FilterGroupBackend.ALCOHOL_VOLUME to listOf(alcohol1),
            )
        )
    }
})

data class SearchParamMatcher(
    private val params: Map<FilterModels.FilterGroupBackend, List<String>>
) : Matcher<SearchParamsSlugs> {
    override fun match(arg: SearchParamsSlugs?): Boolean {
        val filters = arg?.filters ?: return false
        return params.mapKeys { (key, _) -> key.queryName.value } == filters
    }

    override fun toString(): String = params.toString()
}

private fun setupAppAndVerify(
    filterStr: String,
    expectedMap: Map<FilterModels.FilterGroupBackend, List<String>>,
) {
    val searchBuilder = mockk<SearchSlugResponseBuilder>(relaxed = true, relaxUnitFun = true) {
        every { getCocktailsSearchBySlugs(any(), any(), any()) } answers {
            SearchResponse(
                0,
                emptyList(),
                mapOf(),
                ""
            )
        }
    }
    testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            this.filterSlugs(searchBuilder, AppSettings(1, 1, 24))
        }

        client.get("v2/filter/$filterStr")

        verify {
            searchBuilder.getCocktailsSearchBySlugs(
                match(
                    SearchParamMatcher(
                        expectedMap
                    )
                ),
                null,
                SortType.MOST_POPULAR
            )
        }
    }
}
