package org.mixdrinks.view.v2.search

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.fullness.CocktailData
import org.fullness.prepareData
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.mixdrinks.view.v2.controllers.filter.FilterModels
import org.mixdrinks.view.v2.controllers.search.CocktailsSourceV2
import org.mixdrinks.view.v2.controllers.search.SearchParam

internal class CocktailsSourceV2Test : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    beforeEach {
        prepareData(
            listOf(
                CocktailData(
                    id = 10,
                    tagIds = listOf(1000),
                    goodIds = listOf(2000),
                    toolIds = listOf(400),
                ),
                CocktailData(
                    id = 1,
                    tagIds = listOf(1, 2),
                    goodIds = listOf(2, 4),
                    toolIds = listOf(400, 404),
                ),
                CocktailData(
                    id = 2,
                    tagIds = listOf(1, 2, 3),
                    goodIds = listOf(50, 60, 2, 4),
                    toolIds = listOf(400, 401),
                ),
                CocktailData(
                    id = 3,
                    tagIds = listOf(1, 3, 100),
                    goodIds = listOf(100, 101, 2, 4),
                    toolIds = listOf(400, 402),
                ),
                CocktailData(
                    id = 4,
                    tagIds = listOf(1, 2, 100, 200),
                    goodIds = listOf(200, 201, 101),
                    toolIds = listOf(400, 401),
                ),
            )
        )
    }

    test("Verify return all with empty search param") {
        CocktailsSourceV2().getCocktailsBySearch(searchParam = SearchParam()) shouldContainExactlyInAnyOrder listOf(
            10, 1, 2, 3, 4
        ).map { CocktailsSourceV2.CocktailId(it) }
    }

    test("Verify return cocktails by one tags") {
        CocktailsSourceV2().getCocktailsBySearch(
            searchParam = createSearchParam(
                tagIds = listOf(
                    1
                )
            )
        ) shouldContainExactlyInAnyOrder listOf(1, 2, 3, 4).map { CocktailsSourceV2.CocktailId(it) }
    }

    test("Verify return cocktails by two tags") {
        CocktailsSourceV2().getCocktailsBySearch(
            searchParam = createSearchParam(
                tagIds = listOf(
                    1, 100
                )
            )
        ) shouldContainExactlyInAnyOrder listOf(3, 4).map { CocktailsSourceV2.CocktailId(it) }
    }

    test("Verify build search response by goods") {
        val source = CocktailsSourceV2()
        source.getCocktailsBySearch(
            searchParam = createSearchParam(
                goodIds = listOf(
                    2, 4
                )
            )
        ) shouldContainExactlyInAnyOrder listOf(1, 2, 3).map { CocktailsSourceV2.CocktailId(it) }

        source.getCocktailsBySearch(
            searchParam = createSearchParam(
                goodIds = listOf(
                    100
                )
            )
        ) shouldContainExactlyInAnyOrder listOf(3).map { CocktailsSourceV2.CocktailId(it) }
    }

    test("Verify build search response by tools") {
        CocktailsSourceV2().getCocktailsBySearch(
            searchParam = createSearchParam(
                toolIds = listOf(
                    401, 400
                )
            )
        ) shouldContainExactlyInAnyOrder listOf(2, 4).map { CocktailsSourceV2.CocktailId(it) }

        CocktailsSourceV2().getCocktailsBySearch(
            searchParam = createSearchParam(
                toolIds = listOf(
                    402, 404
                )
            )
        ) shouldBe emptyList()
    }

    test("Verify build search response by tags and goods") {
        CocktailsSourceV2().getCocktailsBySearch(
            searchParam = createSearchParam(
                tagIds = listOf(1, 2),
                goodIds = listOf(100, 101),
            )
        ) shouldBe emptyList()

        CocktailsSourceV2().getCocktailsBySearch(
            searchParam = createSearchParam(
                tagIds = listOf(1),
                goodIds = listOf(2),
            )
        ) shouldContainExactly listOf(1, 2, 3).map { CocktailsSourceV2.CocktailId(it) }
    }

    test("Verify filter by all") {
        CocktailsSourceV2().getCocktailsBySearch(
            searchParam = createSearchParam(
                tagIds = listOf(2),
                goodIds = listOf(101),
                toolIds = listOf(400),
            )
        ) shouldContainExactly listOf(4).map { CocktailsSourceV2.CocktailId(it) }
    }
})

private fun createSearchParam(
    tagIds: List<Int> = emptyList(),
    goodIds: List<Int> = emptyList(),
    toolIds: List<Int> = emptyList(),
): SearchParam {
    return SearchParam(
        mapOf(
            FilterModels.Filters.TAGS.id to tagIds.map { FilterModels.FilterId(it) },
            FilterModels.Filters.GOODS.id to goodIds.map { FilterModels.FilterId(it) },
            FilterModels.Filters.TOOLS.id to toolIds.map { FilterModels.FilterId(it) },
        )
    )
}
