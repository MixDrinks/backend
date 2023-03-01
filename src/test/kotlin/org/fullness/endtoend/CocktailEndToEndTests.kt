package org.fullness.endtoend

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.post
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
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.view.v2.controllers.score.CocktailScoreChangeResponse
import org.mixdrinks.view.v2.controllers.score.scoreV2
import org.mixdrinks.view.v2.controllers.settings.AppSettings

class CocktailEndToEndTests : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify ratting return new") {
        prepareData(
            listOf(
                MockCocktailVisit(
                    id = 0,
                    visitCount = 10,
                )
            )
        )

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                this.scoreV2(AppSettings(1, 1, 1))
            }

            verifyVisitCount(10)

            client.post("v2/cocktails/visit?id=0")

            verifyVisitCount(11)
        }
    }
})

private suspend fun ApplicationTestBuilder.verifyVisitCount(count: Int) {
    client.get("v2/cocktail/ratting?id=0").let { response ->
        response.status shouldBe HttpStatusCode.OK

        val result = Json.decodeFromString<CocktailScoreChangeResponse>(response.bodyAsText())

        result.visitCount shouldBe count
    }
}

private data class MockCocktailVisit(
    val id: Int,
    val visitCount: Int,
)


private fun prepareData(cocktails: List<MockCocktailVisit>) {
    transaction {
        createDataBase()

        cocktails.forEach { cocktail ->
            CocktailsTable.insert {
                it[id] = cocktail.id
                it[name] = ""
                it[steps] = arrayOf()
                it[visitCount] = cocktail.visitCount
                it[ratingCount] = 10
                it[ratingValue] = 500
            }
        }
    }
}
