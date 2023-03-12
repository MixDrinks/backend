package org.fullness.integration

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import org.fullness.CocktailData
import org.fullness.prepareData
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.FilterId
import org.mixdrinks.view.cocktail.domain.SortType
import org.mixdrinks.view.v2.controllers.filter.FilterModels
import org.mixdrinks.view.v2.controllers.search.DescriptionBuilder
import org.mixdrinks.view.v2.controllers.search.FilterCache
import org.mixdrinks.view.v2.controllers.search.SearchParams
import org.mixdrinks.view.v2.controllers.search.SearchResponseBuilder

class SearchResponseBuilderIntegration : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify build search response not queries") {
        prepareData(
            listOf(
                CocktailData(
                    id = 0,
                    tagIds = listOf(1, 2, 3),
                    goodIds = listOf(10, 20, 30),
                    toolIds = listOf(100, 200, 300),
                ), CocktailData(
                    id = 1,
                    tagIds = listOf(1, 2),
                    goodIds = listOf(10, 20),
                    toolIds = listOf(100, 200),
                ), CocktailData(
                    id = 2,
                    tagIds = listOf(2, 3),
                    goodIds = listOf(20, 30),
                    toolIds = listOf(200, 300),
                )
            )
        )

        val filterCache = FilterCache()
        val searchResponseBuilder = SearchResponseBuilder(
            filterCache, CocktailSelector(filterCache.filterGroups), DescriptionBuilder()
        )

        val result = searchResponseBuilder.getCocktailsBySearch(
            searchParams = SearchParams(), page = null, sortType = SortType.MOST_POPULAR
        )

        result.cocktails.map { it.id } shouldContainExactlyInAnyOrder listOf(0, 1, 2)

        verifyFutureCountResponse(
            result, FilterModels.Filters.TAGS, mapOf(
                FilterId(1) to 2,
                FilterId(2) to 3,
                FilterId(3) to 2,
            )
        )

        verifyFutureCountResponse(
            result, FilterModels.Filters.GOODS, mapOf(
                FilterId(10) to 2,
                FilterId(20) to 3,
                FilterId(30) to 2,
            )
        )

        verifyFutureCountResponse(
            result, FilterModels.Filters.TOOLS, mapOf(
                FilterId(100) to 2,
                FilterId(200) to 3,
                FilterId(300) to 2,
            )
        )
    }

    test("Verify build search response with tags") {
        prepareData(
            listOf(
                CocktailData(
                    id = 0,
                    tagIds = listOf(1, 2, 3),
                    goodIds = listOf(10, 20, 30),
                    toolIds = listOf(100, 200, 300),
                ), CocktailData(
                    id = 1,
                    tagIds = listOf(1, 2),
                    goodIds = listOf(10, 20),
                    toolIds = listOf(100, 200),
                ), CocktailData(
                    id = 2,
                    tagIds = listOf(2, 3),
                    goodIds = listOf(20, 30),
                    toolIds = listOf(200, 300),
                )
            )
        )

        val filterCache = FilterCache()
        val searchResponseBuilder = SearchResponseBuilder(
            filterCache, CocktailSelector(filterCache.filterGroups), DescriptionBuilder()
        )

        val result = searchResponseBuilder.getCocktailsBySearch(
            searchParams = createSearchParam(tagIds = listOf(1)),
            page = null,
            sortType = SortType.MOST_POPULAR
        )

        result.cocktails.map { it.id } shouldContainExactlyInAnyOrder listOf(0, 1)

        verifyFutureCountResponse(
            result, FilterModels.Filters.TAGS, mapOf(
                FilterId(1) to 2,
                FilterId(2) to 2,
                FilterId(3) to 1,
            )
        )

        verifyFutureCountResponse(
            result, FilterModels.Filters.GOODS, mapOf(
                FilterId(10) to 2,
                FilterId(20) to 2,
                FilterId(30) to 1,
            )
        )

        verifyFutureCountResponse(
            result, FilterModels.Filters.TOOLS, mapOf(
                FilterId(100) to 2,
                FilterId(200) to 2,
                FilterId(300) to 1,
            )
        )
    }

    test("Verify build search response with tag plus goods") {
        prepareData(
            listOf(
                CocktailData(
                    id = 0,
                    tagIds = listOf(1, 2, 3),
                    goodIds = listOf(10, 20, 30),
                    toolIds = listOf(100, 200),
                ), CocktailData(
                    id = 1,
                    tagIds = listOf(1, 2),
                    goodIds = listOf(10, 20),
                    toolIds = listOf(100, 200),
                ), CocktailData(
                    id = 2,
                    tagIds = listOf(2, 3),
                    goodIds = listOf(20, 30),
                    toolIds = listOf(200, 300),
                )
            )
        )

        val filterCache = FilterCache()
        val searchResponseBuilder = SearchResponseBuilder(
            filterCache, CocktailSelector(filterCache.filterGroups), DescriptionBuilder()
        )

        val result = searchResponseBuilder.getCocktailsBySearch(
            searchParams = createSearchParam(
                tagIds = listOf(1),
                goodIds = listOf(30)
            ),
            page = null,
            sortType = SortType.MOST_POPULAR
        )

        result.cocktails.map { it.id } shouldContainExactlyInAnyOrder listOf(0)

        verifyFutureCountResponse(
            result, FilterModels.Filters.TAGS, mapOf(
                FilterId(1) to 1,
                FilterId(2) to 1,
                FilterId(3) to 1,
            )
        )

        verifyFutureCountResponse(
            result, FilterModels.Filters.GOODS, mapOf(
                FilterId(10) to 1,
                FilterId(20) to 1,
                FilterId(30) to 1,
            )
        )

        verifyFutureCountResponse(
            result, FilterModels.Filters.TOOLS, mapOf(
                FilterId(100) to 1,
                FilterId(200) to 1,
                FilterId(300) to 0,
            )
        )
    }
})

private fun createSearchParam(
    tagIds: List<Int> = emptyList(),
    goodIds: List<Int> = emptyList(),
    toolIds: List<Int> = emptyList(),
): SearchParams {
    return SearchParams(
        mapOf(
            FilterModels.Filters.TAGS.id to tagIds.map { FilterId(it) },
            FilterModels.Filters.GOODS.id to goodIds.map { FilterId(it) },
            FilterModels.Filters.TOOLS.id to toolIds.map { FilterId(it) },
        )
    )
}

private fun verifyFutureCountResponse(
    result: SearchResponseBuilder.SearchResponse,
    filter: FilterModels.Filters,
    expectedCount: Map<FilterId, Int>,
) {
    result.futureCounts[filter.id] shouldContainExactlyInAnyOrder expectedCount.map { (filterId, count) ->
        SearchResponseBuilder.FilterCount(
            filterId, count
        )
    }
}
