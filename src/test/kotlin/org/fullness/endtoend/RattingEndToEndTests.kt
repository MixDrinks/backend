package org.fullness.endtoend

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
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
import org.mixdrinks.settings.AppSettings
import org.mixdrinks.view.v2.controllers.score.RattingBuilder
import org.mixdrinks.view.v2.controllers.score.RattingItem
import org.mixdrinks.view.v2.controllers.score.ScoreChangeResponse
import org.mixdrinks.view.v2.controllers.score.rattingSearchView
import org.mixdrinks.view.v2.controllers.score.scoreV2
import org.mixdrinks.view.v2.controllers.search.CocktailsSourceV2

class RattingEndToEndTests : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify visit count change") {
        prepareData(
            listOf(
                MockCocktailRatting(
                    id = 0,
                    visitCount = 10,
                    ratingCount = 10,
                    ratingValue = 25,
                )
            )
        )

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                val appSetting = AppSettings(1, 5, 10)
                rattingSearchView(RattingBuilder(CocktailsSourceV2()), appSetting)
                scoreV2(appSetting)
            }

            client.get("v2/search/ratings").let { response ->
                val result =
                    Json.decodeFromString<Map<CocktailsSourceV2.CocktailId, RattingItem>>(response.bodyAsText())

                result[CocktailsSourceV2.CocktailId(0)] shouldBe RattingItem(
                    cocktailId = CocktailsSourceV2.CocktailId(0),
                    rating = 2.5F,
                    visitCount = 10,
                )
            }

            client.post("v2/cocktails/visit?id=0").let { response ->
                val result = Json.decodeFromString<ScoreChangeResponse>(response.bodyAsText())
                result.rating shouldBe 2.5
                result.visitCount shouldBe 11
            }

            //Verify ratting return new value
            client.get("v2/search/ratings").let { response ->
                val result =
                    Json.decodeFromString<Map<CocktailsSourceV2.CocktailId, RattingItem>>(response.bodyAsText())

                result[CocktailsSourceV2.CocktailId(0)] shouldBe RattingItem(
                    cocktailId = CocktailsSourceV2.CocktailId(0),
                    rating = 2.5F,
                    visitCount = 11,
                )
            }
        }
    }

    test("Verify ratting change") {
        prepareData(
            listOf(
                MockCocktailRatting(
                    id = 0,
                    visitCount = 10,
                    ratingCount = 10,
                    ratingValue = 25,
                ), MockCocktailRatting(
                    id = 1,
                    visitCount = 1,
                    ratingCount = 0,
                    ratingValue = null,
                )
            )
        )

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                val appSetting = AppSettings(1, 5, 10)
                rattingSearchView(RattingBuilder(CocktailsSourceV2()), appSetting)
                scoreV2(appSetting)
            }

            client.get("v2/search/ratings").let { response ->
                val result =
                    Json.decodeFromString<Map<CocktailsSourceV2.CocktailId, RattingItem>>(response.bodyAsText())

                result[CocktailsSourceV2.CocktailId(0)] shouldBe RattingItem(
                    cocktailId = CocktailsSourceV2.CocktailId(0),
                    rating = 2.5F,
                    visitCount = 10,
                )

                result[CocktailsSourceV2.CocktailId(1)] shouldBe RattingItem(
                    cocktailId = CocktailsSourceV2.CocktailId(1),
                    rating = null,
                    visitCount = 1,
                )
            }

            client.post("v2/cocktails/score?id=0") {
                contentType(ContentType.Application.Json)
                setBody("{\"value\":5}")
            }.let { response ->
                val result = Json.decodeFromString<ScoreChangeResponse>(response.bodyAsText())
                result.rating shouldBe 2.7F
                result.visitCount shouldBe 10
            }
        }
    }
})

private data class MockCocktailRatting(
    val id: Int,
    val visitCount: Int,
    val ratingCount: Int,
    val ratingValue: Int?,
)

private fun prepareData(cocktails: List<MockCocktailRatting>) {
    transaction {
        SchemaUtils.drop(CocktailsTable, CocktailsToItemsTable, CocktailToTagTable, TagsTable, ItemsTable)
        SchemaUtils.create(CocktailsTable, CocktailsToItemsTable, CocktailToTagTable, TagsTable, ItemsTable)

        cocktails.forEach { cocktail ->
            CocktailsTable.insert {
                it[id] = cocktail.id
                it[name] = ""
                it[steps] = arrayOf()
                it[visitCount] = cocktail.visitCount
                it[ratingCount] = cocktail.ratingCount
                it[ratingValue] = cocktail.ratingValue
            }
        }
    }
}
