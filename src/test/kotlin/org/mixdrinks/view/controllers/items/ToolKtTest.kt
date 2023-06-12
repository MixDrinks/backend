package org.mixdrinks.view.controllers.items

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.createDataBase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Tool

class ToolKtTest : AnnotationSpec() {

    @Suppress("MemberVisibilityCanBePrivate")
    val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    @After
    fun afterSpec() {
        TransactionManager.closeAndUnregister(database)
    }

    @Test
    fun verifyToolReturnBySlug() {
        prepareData()

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                routing {
                    tool()
                }
            }

            client.get("v2/tool/test-tool").let { response ->
                response.status.value shouldBe 200
            }
        }
        TransactionManager.closeAndUnregister(database)
    }

    private fun prepareData() {
        transaction {
            createDataBase()

            Tool.new {
                name = "Test Tool"
                about = "Test Tool About"
                slug = "test-tool"
            }
        }
    }
}
