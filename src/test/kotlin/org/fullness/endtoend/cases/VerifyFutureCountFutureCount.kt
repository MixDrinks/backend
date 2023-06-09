package org.fullness.endtoend.cases

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.createDataBase
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToGlasswareTable
import org.mixdrinks.data.CocktailsToGoodsTable
import org.mixdrinks.data.CocktailsToTastesTable
import org.mixdrinks.data.CocktailsToToolsTable
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.Good
import org.mixdrinks.data.Taste
import org.mixdrinks.data.Tool
import org.mixdrinks.domain.CocktailSelector
import org.mixdrinks.dto.FilterId
import org.mixdrinks.view.controllers.filter.FilterCache
import org.mixdrinks.view.controllers.filter.FilterModels
import org.mixdrinks.view.controllers.search.DescriptionBuilder
import org.mixdrinks.view.controllers.search.FilterCount
import org.mixdrinks.view.controllers.search.SearchResponse
import org.mixdrinks.view.controllers.search.SearchResponseBuilder
import org.mixdrinks.view.controllers.search.slug.SearchSlugResponseBuilder
import org.mixdrinks.view.controllers.search.slug.filterSlugs
import org.mixdrinks.view.controllers.settings.AppSettings

class VerifyFutureCountFutureCount : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    beforeSpec {
        prepareData()
    }

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify glassware filter correct data") {
        testApplication {
            mockApp()
            val response = client.get(
                "v2/filter/${FilterModels.FilterGroupBackend.GLASSWARE.queryName.value}=test-glassware-2"
            )

            response.status shouldBe HttpStatusCode.OK

            val result = Json.decodeFromString<SearchResponse>(response.bodyAsText())

            result shouldBe SearchResponseMatch(
                cocktails = listOf(2, 3),
                toolsCounts = listOf(
                    FilterCount(
                        FilterId(1), 1, "glassware=test-glassware-2/tools=test-tool-1",
                    ),
                    FilterCount(
                        FilterId(2), 1, "glassware=test-glassware-2/tools=test-tool-2",
                    ),
                    FilterCount(
                        FilterId(3), 2, "glassware=test-glassware-2/tools=test-tool-3",
                    )
                ),
                glasswareCounts = listOf(
                    FilterCount(
                        FilterId(1), 1, "glassware=test-glassware-1",
                    ),
                    FilterCount(
                        FilterId(2), 2, "glassware=test-glassware-2",
                    )
                )

            )
        }
    }

    test("Verify tools filter correct data") {
        testApplication {
            mockApp()
            val response = client.get(
                "v2/filter/${FilterModels.FilterGroupBackend.TOOLS.queryName.value}=test-tool-3"
            )
            response.status shouldBe HttpStatusCode.OK

            Json.decodeFromString<SearchResponse>(response.bodyAsText()) shouldBe SearchResponseMatch(
                cocktails = listOf(2, 3),
                toolsCounts = listOf(
                    FilterCount(
                        FilterId(1), 1, "tools=test-tool-1,test-tool-3",
                    ),
                    FilterCount(
                        FilterId(2), 1, "tools=test-tool-2,test-tool-3",
                    ),
                    FilterCount(
                        FilterId(3), 2, "tools=test-tool-3",
                    )
                ),
                glasswareCounts = listOf(
                    FilterCount(
                        FilterId(1), 0, "glassware=test-glassware-1/tools=test-tool-3",
                    ),
                    FilterCount(
                        FilterId(2), 2, "glassware=test-glassware-2/tools=test-tool-3",
                    )
                )
            )
        }
    }

    test("Verify tools plus glassware filter correct data") {
        testApplication {
            mockApp()
            val response = client.get(
                "v2/filter/${FilterModels.FilterGroupBackend.TOOLS.queryName.value}=test-tool-3" +
                        "/${FilterModels.FilterGroupBackend.GLASSWARE.queryName.value}=test-glassware-2"
            )

            response.status shouldBe HttpStatusCode.OK

            Json.decodeFromString<SearchResponse>(response.bodyAsText()) shouldBe SearchResponseMatch(
                cocktails = listOf(2, 3),
                toolsCounts = listOf(
                    FilterCount(
                        FilterId(1), 1, "glassware=test-glassware-2/tools=test-tool-1,test-tool-3",
                    ),
                    FilterCount(
                        FilterId(2), 1, "glassware=test-glassware-2/tools=test-tool-2,test-tool-3",
                    ),
                    FilterCount(
                        FilterId(3), 2, "glassware=test-glassware-2/tools=test-tool-3",
                    )
                ),
                glasswareCounts = listOf(
                    FilterCount(
                        FilterId(1), 0, "glassware=test-glassware-1/tools=test-tool-3",
                    ),
                    FilterCount(
                        FilterId(2), 2, "glassware=test-glassware-2/tools=test-tool-3",
                    )
                )
            )
        }
    }

    test("Verify tools plus glassware filter correct data (2)") {
        testApplication {
            mockApp()
            val response = client.get(
                "v2/filter/${FilterModels.FilterGroupBackend.TOOLS.queryName.value}=test-tool-2/" +
                        "${FilterModels.FilterGroupBackend.GLASSWARE.queryName.value}=test-glassware-2"
            )

            response.status shouldBe HttpStatusCode.OK

            Json.decodeFromString<SearchResponse>(response.bodyAsText()) shouldBe SearchResponseMatch(
                cocktails = listOf(3),
                toolsCounts = listOf(
                    FilterCount(
                        FilterId(1), 0, "glassware=test-glassware-2/tools=test-tool-1,test-tool-2",
                    ),
                    FilterCount(
                        FilterId(2), 1, "glassware=test-glassware-2/tools=test-tool-2",
                    ),
                    FilterCount(
                        FilterId(3), 1, "glassware=test-glassware-2/tools=test-tool-2,test-tool-3",
                    )
                ),
                glasswareCounts = listOf(
                    FilterCount(
                        FilterId(1), 1, "glassware=test-glassware-1/tools=test-tool-2",
                    ),
                    FilterCount(
                        FilterId(2), 1, "glassware=test-glassware-2/tools=test-tool-2",
                    )
                )
            )
        }
    }
})

private class SearchResponseMatch(
    private val cocktails: List<Int>,
    private val toolsCounts: List<FilterCount>,
    private val glasswareCounts: List<FilterCount>,
) : Matcher<SearchResponse> {

    @Suppress("ReturnCount")
    override fun test(value: SearchResponse): MatcherResult {
        if (value.totalCount != cocktails.size) {
            return MatcherResult(
                false,
                { "Total count is not equal to ${cocktails.size}" },
                { "Total count is equal to ${cocktails.size}" }
            )
        }

        if (value.cocktails.map { it.id.id }.sorted() != cocktails.sorted()) {
            return ComparableMatcherResult(
                false,
                { "Cocktails is not equal to $cocktails" },
                { "Cocktails is equal to $cocktails" },
                value.cocktails.map { it.id.id }.sorted().toString(),
                cocktails.sorted().toString(),
            )
        }

        if (value.futureCounts[FilterModels.FilterGroupBackend.TOOLS.id].orEmpty() != toolsCounts) {
            return ComparableMatcherResult(
                false,
                { "Tools counts is not equal to $toolsCounts" },
                { "Tools counts is equal to $toolsCounts" },
                value.futureCounts[FilterModels.FilterGroupBackend.TOOLS.id].orEmpty().toString(),
                toolsCounts.toString(),
            )
        }

        if (value.futureCounts[FilterModels.FilterGroupBackend.GLASSWARE.id].orEmpty() != glasswareCounts) {
            return ComparableMatcherResult(
                false,
                { "Glassware counts is not equal to $glasswareCounts" },
                { "Glassware counts is equal to $glasswareCounts" },
                value.futureCounts[FilterModels.FilterGroupBackend.GLASSWARE.id].orEmpty().toString(),
                glasswareCounts.toString(),
            )
        }

        return MatcherResult(
            true,
            { "Search response is equal to expected" },
            { "Search response is not equal to expected" }
        )
    }

}

private fun ApplicationTestBuilder.mockApp() {
    application {
        install(ContentNegotiation) {
            json()
        }
        val filterCache = FilterCache()
        val cocktailSelector = CocktailSelector(filterCache.filterGroups)
        val searchResponseBuilder = SearchResponseBuilder(filterCache, cocktailSelector, DescriptionBuilder())
        val searchSlugResponseBuilder = SearchSlugResponseBuilder(filterCache, searchResponseBuilder)
        this.filterSlugs(searchSlugResponseBuilder, AppSettings(1, 1, 24))
    }
}

private fun prepareData() {
    transaction {
        createDataBase()

        val tool1 = Tool.new(id = 1) {
            name = "Test tool 1"
            about = "Test tool 1"
            slug = "test-tool-1"
        }

        val tool2 = Tool.new(id = 2) {
            name = "Test tool 2"
            about = "Test tool 2"
            slug = "test-tool-2"
        }

        val tool3 = Tool.new(id = 3) {
            name = "Test tool 3"
            about = "Test tool 3"
            slug = "test-tool-3"
        }

        val glassware1 = Glassware.new(id = 1) {
            name = "Test glassware 1"
            about = "Test glassware 1"
            slug = "test-glassware-1"
        }

        val glassware2 = Glassware.new(id = 2) {
            name = "Test glassware 2"
            about = "Test glassware 2"
            slug = "test-glassware-2"
        }

        createCocktail(1, glassware1, listOf(tool1, tool2))
        createCocktail(2, glassware2, listOf(tool1, tool3))
        createCocktail(3, glassware2, listOf(tool2, tool3))

        Good.new(id = 1) {
            name = "Test item 1"
            about = "Test item 1"
            slug = "test-item-1"
        }

        CocktailsToGoodsTable.insert {
            it[cocktailId] = 1
            it[goodId] = 1
            it[unit] = "ml"
            it[amount] = 100
        }

        CocktailsToGoodsTable.insert {
            it[cocktailId] = 2
            it[goodId] = 1
            it[unit] = "ml"
            it[amount] = 100
        }
    }
}

class TestCocktail(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestCocktail>(CocktailsTable)

    var name by CocktailsTable.name
    var visitCount by CocktailsTable.visitCount
    var steps by CocktailsTable.steps
    var ratingCount by CocktailsTable.ratingCount
    var ratingValue by CocktailsTable.ratingValue
    var slug by CocktailsTable.slug

    var tools by Tool via CocktailsToToolsTable
    var taste by Taste via CocktailsToTastesTable
    var glassware by Glassware via CocktailsToGlasswareTable
}

private fun createCocktail(id: Int, argGlassware: Glassware, argTools: List<Tool>) {
    TestCocktail.new(id = id) {
        name = "Test cocktail $id"
        steps = arrayOf("Test step ${id}1", "Test step ${id}2")
        visitCount = 1
        ratingCount = 1
        ratingValue = 3
        slug = "test-cocktail-${id}"

        tools = SizedCollection(argTools)

        taste = SizedCollection(
            listOf(
                Taste.new {
                    name = "Taste ${id}1"
                    slug = "taste-${id}1"
                }
            )
        )

        glassware = SizedCollection(argGlassware)
    }
}
