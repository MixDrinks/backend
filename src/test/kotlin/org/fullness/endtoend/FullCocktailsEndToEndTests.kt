package org.fullness.endtoend

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
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
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToGlasswareTable
import org.mixdrinks.data.CocktailsToGoodsTable
import org.mixdrinks.data.CocktailsToTastesTable
import org.mixdrinks.data.CocktailsToToolsTable
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.Good
import org.mixdrinks.data.Taste
import org.mixdrinks.data.Tool
import org.mixdrinks.view.cocktail.FullCocktailVM
import org.mixdrinks.view.cocktail.TagVM
import org.mixdrinks.view.cocktail.cocktails

internal class FullCocktailsEndToEndTests : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify cocktail not found") {
        transaction {
            createDataBase()
        }

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                cocktails()
            }

            val response = client.get("v2/cocktails/full?id=1")

            response.status shouldBe HttpStatusCode.NotFound
        }
    }


    test("Verify return full cocktail") {
        val tastes = listOf(
            "Taste 1", "Taste 2"
        )
        prepareData(tastes)

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                cocktails()
            }

            val response = client.get("v2/cocktails/full?id=1")

            response.status shouldBe HttpStatusCode.OK
            val result = Json.decodeFromString<FullCocktailVM>(response.bodyAsText())

            result.id shouldBe 1
            result.receipt shouldBe arrayOf("Test step 1", "Test step 2")
            result.tastes.map(TagVM::name) shouldContainExactly tastes

            result.tools.map { it.name } shouldBe listOf("Test glassware 1", "Test tool 1")
            result.goods.first().let { good ->
                good.name shouldBe "Test item 1"
                good.amount shouldBe 100
                good.unit shouldBe "ml"
            }
        }
    }
})

class TestCocktail(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestCocktail>(CocktailsTable)

    var name by CocktailsTable.name
    var visitCount by CocktailsTable.visitCount
    var steps by CocktailsTable.steps
    var ratingCount by CocktailsTable.ratingCount
    var ratingValue by CocktailsTable.ratingValue

    var tools by Tool via CocktailsToToolsTable
    var taste by Taste via CocktailsToTastesTable
    var glassware by Glassware via CocktailsToGlasswareTable
}

private fun prepareData(tastes: List<String>) {
    transaction {
        createDataBase()

        TestCocktail.new(id = 1) {
            name = "Test cocktail 1"
            steps = arrayOf("Test step 1", "Test step 2")
            visitCount = 1
            ratingCount = 1
            ratingValue = 3

            tools = SizedCollection(listOf(
                Tool.new {
                    name = "Test tool 1"
                    about = "Test tool 1"
                    visitCount = 0
                }
            ))

            taste = SizedCollection(tastes
                .map {
                    Taste.new {
                        name = it
                    }
                }
            )

            glassware = SizedCollection(listOf(
                Glassware.new(id = 10) {
                    name = "Test glassware 1"
                    about = "Test glassware 1"
                    visitCount = 0
                }
            ))
        }

        Good.new(id = 1) {
            name = "Test item 1"
            about = "Test item 1"
        }

        CocktailsToGoodsTable.insert {
            it[cocktailId] = 1
            it[goodId] = 1
            it[unit] = "ml"
            it[amount] = 100
        }

    }
}
