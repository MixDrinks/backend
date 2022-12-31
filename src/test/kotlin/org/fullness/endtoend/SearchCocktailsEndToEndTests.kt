package org.fullness.endtoend

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.featureSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.view.v2.controllers.settings.AppSettings
import org.mixdrinks.view.v2.controllers.search.CocktailsSourceV2
import org.mixdrinks.view.v2.controllers.search.DescriptionBuilder
import org.mixdrinks.view.v2.controllers.search.SearchResponseBuilder
import org.mixdrinks.view.v2.controllers.search.searchView
import java.util.concurrent.atomic.AtomicInteger

internal class SearchCocktailsEndToEndTests : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify return all") {
        val mockId = 999

        val mockCocktails = MockCocktail(
            id = mockId,
            name = "NAME1",
            visitCount = 1,
            ratingCount = 1,
            ratingValue = 3,
        )

        prepareData(
            listOf(
                mockCocktails,
            ).plus(buildList {
                repeat(10) {
                    this.add(MockCocktail.getDefault())
                }
            })
        )
        testApplication {
            initApp()
            val response = client.get("v2/search/cocktails")

            response.status shouldBe HttpStatusCode.OK
            val result = Json.decodeFromString<SearchResponseBuilder.SearchResponse>(response.bodyAsText())

            result.totalCount shouldBe 11
            result.cocktails.size shouldBe 11

            result.cocktails.first { it.id == mockId }.run {
                name shouldBe mockCocktails.name
                rating shouldBe mockCocktails.rating
                visitCount shouldBe mockCocktails.visitCount
            }
        }
    }

    test("Verify return page 0") {
        val pageSize = 5

        prepareData(buildList {
            repeat(7) {
                this.add(MockCocktail.getDefault())
            }
        })
        testApplication {
            initApp(pageSize)
            val response = client.get("v2/search/cocktails?page=0")

            response.status shouldBe HttpStatusCode.OK
            val result = Json.decodeFromString<SearchResponseBuilder.SearchResponse>(response.bodyAsText())

            result.totalCount shouldBe 7

            result.cocktails.size shouldBe pageSize
        }
    }

    test("Verify return page 1") {
        val pageSize = 5

        prepareData(buildList {
            repeat(7) {
                this.add(MockCocktail.getDefault())
            }
        })
        testApplication {
            initApp(pageSize)
            val response = client.get("v2/search/cocktails?page=1")

            response.status shouldBe HttpStatusCode.OK
            val result = Json.decodeFromString<SearchResponseBuilder.SearchResponse>(response.bodyAsText())

            result.totalCount shouldBe 7

            result.cocktails.size shouldBe 2
        }
    }

    featureSpec {
        test("verify sort most popular") {
            prepareData(
                listOf(
                    MockCocktail(
                        id = 1,
                        name = "",
                        visitCount = 3,
                        ratingCount = 10,
                        ratingValue = 50,
                    ),

                    MockCocktail(
                        id = 2,
                        name = "",
                        visitCount = 10,
                        ratingCount = 10,
                        ratingValue = 20,
                    ),
                )
            )
            testApplication {
                initApp(10)

                val response = client.get("v2/search/cocktails?sort=most-popular")

                response.status shouldBe HttpStatusCode.OK
                val result = Json.decodeFromString<SearchResponseBuilder.SearchResponse>(response.bodyAsText())

                result.cocktails.map { it.id } shouldBe listOf(2, 1)
            }
        }

        test("verify sort biggest rate with null") {
            prepareData(
                listOf(
                    MockCocktail(
                        id = 1,
                        name = "",
                        visitCount = 3,
                        ratingCount = 10,
                        ratingValue = null,
                    ),

                    MockCocktail(
                        id = 2,
                        name = "",
                        visitCount = 10,
                        ratingCount = 0,
                        ratingValue = null,
                    ),

                    MockCocktail(
                        id = 3,
                        name = "",
                        visitCount = 10,
                        ratingCount = 10,
                        ratingValue = 20,
                    ),
                )
            )
            testApplication {
                initApp(10)

                val response = client.get("v2/search/cocktails?sort=biggest-rate")

                response.status shouldBe HttpStatusCode.OK
                val result = Json.decodeFromString<SearchResponseBuilder.SearchResponse>(response.bodyAsText())

                val resultId = result.cocktails.map { it.id }

                resultId[0] shouldBe 3

                resultId shouldContainAll listOf(1, 2)
            }
        }


        test("verify sort biggest rate") {
            prepareData(
                listOf(
                    MockCocktail(
                        id = 1,
                        name = "",
                        visitCount = 3,
                        ratingCount = 10,
                        ratingValue = 50,
                    ),

                    MockCocktail(
                        id = 2,
                        name = "",
                        visitCount = 10,
                        ratingCount = 0,
                        ratingValue = null,
                    ),

                    MockCocktail(
                        id = 3,
                        name = "",
                        visitCount = 10,
                        ratingCount = 10,
                        ratingValue = 20,
                    ),
                )
            )
            testApplication {
                initApp(10)

                val response = client.get("v2/search/cocktails?sort=biggest-rate")

                response.status shouldBe HttpStatusCode.OK
                val result = Json.decodeFromString<SearchResponseBuilder.SearchResponse>(response.bodyAsText())

                result.cocktails.map { it.id } shouldBe listOf(1, 3, 2)
            }
        }


    }
})

private fun ApplicationTestBuilder.initApp(pageSize: Int = 100) {
    application {
        install(ContentNegotiation) {
            json()
        }
        searchView(SearchResponseBuilder(CocktailsSourceV2(), DescriptionBuilder()), createAppSetting(pageSize))
    }
}

private fun createAppSetting(pageSize: Int): AppSettings {
    return mockk() {
        every { this@mockk.pageSize } answers { pageSize }
    }
}

private data class MockCocktail(
    val id: Int,
    val name: String,
    val visitCount: Int,
    val ratingCount: Int,
    val ratingValue: Int?,
) {
    val rating: Float?
        get() = ratingValue?.let {
            it.toFloat() / ratingCount.toFloat()
        }

    companion object {
        private val idCount: AtomicInteger = AtomicInteger(0)
        fun getDefault(): MockCocktail {
            return MockCocktail(
                id = idCount.incrementAndGet(),
                name = "MockName${idCount.get()}",
                visitCount = 0,
                ratingCount = 0,
                ratingValue = null,
            )
        }
    }
}

private fun prepareData(cocktails: List<MockCocktail>) {
    transaction {
        SchemaUtils.drop(CocktailsTable, CocktailsToItemsTable, CocktailToTagTable, TagsTable, ItemsTable)
        SchemaUtils.create(CocktailsTable, CocktailsToItemsTable, CocktailToTagTable, TagsTable, ItemsTable)

        cocktails.forEach { cocktail ->
            CocktailsTable.insert {
                it[id] = cocktail.id
                it[name] = cocktail.name
                it[steps] = arrayOf()
                it[visitCount] = cocktail.visitCount
                it[ratingCount] = cocktail.ratingCount
                it[ratingValue] = cocktail.ratingValue
            }
        }
    }
}
