package org.mixdrinks.view.controllers.items

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.createDataBase
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Good

class GoodKtTest : AnnotationSpec() {

    @Suppress("MemberVisibilityCanBePrivate")
    val database =
        org.jetbrains.exposed.sql.Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    @After
    fun afterSpec() {
        TransactionManager.closeAndUnregister(database)
    }

    @Test
    fun verifyGoodBySlug() {
        prepareData()

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                routing {
                    good()
                }
            }

            client.get("v3/goods/test-good").let { httpResponse ->
                httpResponse.status shouldBe HttpStatusCode.OK
                val result = Json.decodeFromString<ItemVm>(httpResponse.bodyAsText())

                result.slug shouldBe "test-good"
                result.about shouldBe "Test Good About"
            }

            client.get("v3/goods/test-good-2").status shouldBe HttpStatusCode.NotFound
        }
    }

    private fun prepareData() {
        transaction {
            createDataBase()

            Good.new {
                name = "Test Good"
                about = "Test Good About"
                slug = "test-good"
            }
        }
    }
}
