package org.mixdrinks.view.cocktail

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.createDataBase
import org.fullness.endtoend.TestCocktail
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsToGoodsTable
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.Good
import org.mixdrinks.data.Tag
import org.mixdrinks.data.Taste
import org.mixdrinks.data.Tool

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

            val response = client.get("v2/cocktail/some_slug")

            response.status shouldBe HttpStatusCode.NotFound
        }
    }

    test("Verify return full cocktail slug") {
        prepareData()

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                cocktails()
            }

            val response = client.get("v2/cocktail/marharyta-bez-tsukru")

            response.status shouldBe HttpStatusCode.OK
            val result = Json.decodeFromString<FullCocktailV2VM>(response.bodyAsText())

            result.receipt shouldBe arrayOf(
                "Зроби на бокалі солону облямівку",
                "Налий у шейкер лаймовий сік 15 мл, лікер тріпл сек 30 мл та срібну текілу 50 мл"
            )

            result.name shouldBe "Маргарита без цукру"
            result.slug shouldBe "marharyta-bez-tsukru"

            result.goods[0].let { good ->
                good.name shouldBe "Лайм"
                good.slug shouldBe "laim"
                good.path shouldBe "goods/laim"
            }

            result.goods[1].let { good ->
                good.name shouldBe "Лаймовий сік"
                good.slug shouldBe "laimovyi-sik"
                good.path shouldBe "goods/laimovyi-sik"
            }

            result.tools[0].let { glassware ->
                glassware.name shouldBe "Келих Маргарита"
                glassware.slug shouldBe "kelykh-marharyta"
                glassware.path shouldBe "glassware/kelykh-marharyta"
            }

            result.tools[1].let { tool ->
                tool.name shouldBe "Джигер"
                tool.slug shouldBe "dzhyher"
                tool.path shouldBe "tools/dzhyher"
            }

            result.tags[0].let {
                it.name shouldBe "Кислі"
                it.slug shouldBe "kysli"
                it.path shouldBe "taste=kysli"
            }

            result.tags[1].let {
                it.name shouldBe "на текілі"
                it.slug shouldBe "na-tekili"
                it.path shouldBe "tags=na-tekili"
            }
        }
    }
})

@Suppress("LongMethod")
private fun prepareData() {
    transaction {
        createDataBase()

        TestCocktail.new(id = 1) {
            name = "Маргарита без цукру"
            slug = "marharyta-bez-tsukru"
            steps = arrayOf(
                "Зроби на бокалі солону облямівку",
                "Налий у шейкер лаймовий сік 15 мл, лікер тріпл сек 30 мл та срібну текілу 50 мл"
            )
            visitCount = 1
            ratingCount = 1
            ratingValue = 3

            glassware = SizedCollection(listOf(
                Glassware.new(id = 10) {
                    name = "Келих Маргарита"
                    slug = "kelykh-marharyta"
                    about = ""
                    visitCount = 0
                }
            ))

            tools = SizedCollection(listOf(
                Tool.new {
                    name = "Джигер"
                    slug = "dzhyher"
                    about = ""
                    visitCount = 0
                }
            ))

            taste = SizedCollection(listOf(
                Taste.new {
                    name = "Кислі"
                    slug = "kysli"
                }
            ))

            tags = SizedCollection(listOf(
                Tag.new {
                    name = "на текілі"
                    slug = "na-tekili"
                }
            ))
        }

        Good.new(id = 1) {
            name = "Лайм"
            slug = "laim"
            about = ""
        }
        CocktailsToGoodsTable.insert {
            it[cocktailId] = 1
            it[goodId] = 1
            it[unit] = "г"
            it[amount] = 10
        }

        Good.new(id = 2) {
            name = "Лаймовий сік"
            slug = "laimovyi-sik"
            about = ""
        }
        CocktailsToGoodsTable.insert {
            it[cocktailId] = 1
            it[goodId] = 2
            it[unit] = "мл"
            it[amount] = 15
        }
    }
}
