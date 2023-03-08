package org.fullness.endtoend

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.createDataBase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.view.v2.controllers.score.item.ItemScoreChangeResponse
import org.mixdrinks.view.v2.controllers.score.item.itemScore
import org.mixdrinks.view.v2.data.ItemId

class ItemChangeVisitCountEndToEnd : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify item change visit count") {
        prepareDatabase(
            listOf(
                MockItem(
                    id = 0,
                    visitCount = 0,
                )
            )
        )

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }

                itemScore()
            }

            client.post("v2/item/visit?id=0").let { response ->
                response.status shouldBe HttpStatusCode.OK

                val result = Json.decodeFromString<ItemScoreChangeResponse>(response.bodyAsText())

                result.cocktailId shouldBe ItemId(0)
                result.visitCount shouldBe 1
            }
        }
    }
})

private data class MockItem(
    val id: Int,
    val visitCount: Int,
)

private fun prepareDatabase(items: List<MockItem>) {
    transaction {
        createDataBase()

        items.forEach { item ->
            ItemsTable.insert {
                it[id] = item.id
                it[visitCount] = item.visitCount
                it[name] = ""
                it[about] = ""
                it[relation] = 0
            }
        }
    }
}
