package org.fullness.endtoend

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
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
import org.mixdrinks.data.Item
import org.mixdrinks.data.Tool
import org.mixdrinks.view.v2.controllers.items.ItemVm
import org.mixdrinks.view.v2.controllers.items.items

class FullItemGoodTest : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify return good") {
        prepareDatabase()

        verifyResult(1) { response: HttpResponse ->
            response.status shouldBe HttpStatusCode.OK
            val result = Json.decodeFromString<ItemVm>(response.bodyAsText())

            result.name shouldBe "Good"
        }
    }

    test("Verify return tool") {
        prepareDatabase()

        verifyResult(2) { response: HttpResponse ->
            response.status shouldBe HttpStatusCode.OK
            val result = Json.decodeFromString<ItemVm>(response.bodyAsText())

            result.name shouldBe "Tool"
        }
    }

    test("Verify return not found") {
        prepareDatabase()

        verifyResult(10) { response: HttpResponse ->
            response.status shouldBe HttpStatusCode.NotFound
        }
    }


})

private fun verifyResult(id: Int, check: suspend (response: HttpResponse) -> Unit) {
    testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            this.items()
        }

        check(client.get("v2/items/full?id=${id}"))
    }
}

fun prepareDatabase() {
    transaction {
        createDataBase()

        Item.new(id = 1) {
            name = "Good"
            about = "About Good"
            relation = 1
        }

        Tool.new(id = 2) {
            name = "Tool"
            about = "About Tool"
        }
    }
}