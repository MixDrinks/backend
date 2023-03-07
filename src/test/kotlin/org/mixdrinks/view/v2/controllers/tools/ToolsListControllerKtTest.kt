package org.mixdrinks.view.v2.controllers.tools

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
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
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.Tool

class ToolsListControllerKtTest : FunSpec({
    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify return tools") {
        prepareData()

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                itemsList()
            }

            val response = client.get("v2/tools/all")

            response.status shouldBe HttpStatusCode.OK
            val result = Json.decodeFromString<List<ToolsVM>>(response.bodyAsText())

            result.size shouldBe 2
            result.map { it.name } shouldBe listOf("Tool1", "Tool2")
        }
    }
})

private fun prepareData() {
    transaction {
        createDataBase()

        Tool.new {
            name = "Tool1"
            about = ""
        }

        Tool.new {
            name = "Tool2"
            about = ""
        }
    }
}