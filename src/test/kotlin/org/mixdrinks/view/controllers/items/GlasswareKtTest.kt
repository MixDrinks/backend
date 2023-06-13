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
import org.mixdrinks.data.Glassware

class GlasswareKtTest : AnnotationSpec() {

    @Suppress("MemberVisibilityCanBePrivate")
    val database =
        org.jetbrains.exposed.sql.Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    @After
    fun afterSpec() {
        TransactionManager.closeAndUnregister(database)
    }

    @Test
    fun verifyGlasswareBySlug() {
        prepareData()

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                routing {
                    glassware()
                }
            }

            client.get("v3/glassware/test-glassware").let { response ->
                println(response)
                response.status shouldBe HttpStatusCode.OK
                val result = Json.decodeFromString<ItemVm>(response.bodyAsText())

                result.slug shouldBe "test-glassware"
                result.about shouldBe "Test Glassware About"
            }

            client.get("v3/glassware/test-glassware-2").status shouldBe HttpStatusCode.NotFound
        }
    }

    private fun prepareData() {
        transaction {
            createDataBase()

            Glassware.new {
                name = "Test Glassware"
                about = "Test Glassware About"
                slug = "test-glassware"
            }
        }
    }

}