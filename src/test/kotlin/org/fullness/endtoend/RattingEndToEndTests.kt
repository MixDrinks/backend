package org.fullness.endtoend

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.createDataBase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.auth.FirebasePrincipalUser
import org.mixdrinks.auth.firebase
import org.mixdrinks.cocktails.score.CocktailScoreChangeResponse
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.view.controllers.score.score
import org.mixdrinks.view.controllers.settings.AppSettings

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
                install(Authentication) {
                    firebase {
                        validate {
                            FirebasePrincipalUser("")
                        }
                    }
                }
                install(ContentNegotiation) {
                    json()
                }
                val appSetting = AppSettings(1, 5, 10)
                score(appSetting)
            }

            client.post("v2/cocktails/visit?id=0").let { response ->
                val result = Json.decodeFromString<CocktailScoreChangeResponse>(response.bodyAsText())
                result.rating shouldBe 2.5
                result.visitCount shouldBe 11
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
                install(Authentication) {
                    firebase {
                        validate {
                            FirebasePrincipalUser("")
                        }
                    }
                }
                install(ContentNegotiation) {
                    json()
                }
                val appSetting = AppSettings(1, 5, 10)
                score(appSetting)
            }

            client.post("v2/cocktails/score?id=0") {
                contentType(ContentType.Application.Json)
                setBody("{\"value\":5}")
            }.let { response ->
                val result = Json.decodeFromString<CocktailScoreChangeResponse>(response.bodyAsText())
                result.rating shouldBe 2.7F
                result.visitCount shouldBe 10
            }
        }
    }

    test("Verify ratting change from null") {
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
                install(Authentication) {
                    firebase {
                        validate {
                            FirebasePrincipalUser("")
                        }
                    }
                }
                install(ContentNegotiation) {
                    json()
                }
                val appSetting = AppSettings(1, 5, 10)
                score(appSetting)
            }

            client.post("v2/cocktails/score?id=1") {
                contentType(ContentType.Application.Json)
                setBody("{\"value\":4}")
            }.let { response ->
                val result = Json.decodeFromString<CocktailScoreChangeResponse>(response.bodyAsText())
                result.rating shouldBe 4F
                result.visitCount shouldBe 1
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
        createDataBase()

        cocktails.forEach { cocktail ->
            CocktailsTable.insert {
                it[id] = cocktail.id
                it[name] = ""
                it[steps] = arrayOf()
                it[visitCount] = cocktail.visitCount
                it[ratingCount] = cocktail.ratingCount
                it[ratingValue] = cocktail.ratingValue
                it[slug] = "cocktail-${cocktail.id}"
            }
        }
    }
}
