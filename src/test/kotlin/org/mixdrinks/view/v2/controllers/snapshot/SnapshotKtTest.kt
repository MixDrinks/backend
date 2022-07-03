package org.mixdrinks.view.v2.controllers.snapshot

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.view.cocktail.TagVM
import org.mixdrinks.view.v2.data.CocktailId
import org.mixdrinks.view.v2.data.TagId

internal class SnapshotKtTest : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify snapshot") {
        prepareSnapshot()

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                snapshot()
            }

            val response = client.get("v2/snapshot")

            val result = Json.decodeFromString<Snapshot>(response.bodyAsText())

            result.cocktails.map { it.id } shouldBe listOf(1, 2)
            result.tags shouldBe listOf(TagVM(TagId(1), "T1"), TagVM(TagId(2), "T2"))

            result.cocktailToTags shouldContainExactlyInAnyOrder listOf(
                Snapshot.CocktailToTag(CocktailId(1), TagId(1)),
                Snapshot.CocktailToTag(CocktailId(1), TagId(2)),
                Snapshot.CocktailToTag(CocktailId(2), TagId(2)),
            )
        }
    }

})

private fun prepareSnapshot() {
    transaction {
        SchemaUtils.drop(CocktailsTable, CocktailsToItemsTable, CocktailToTagTable, TagsTable, ItemsTable)
        SchemaUtils.create(CocktailsTable, CocktailsToItemsTable, CocktailToTagTable, TagsTable, ItemsTable)

        insertCocktail(1, "N1")
        insertCocktail(2, "N2")

        insertTag(1, "T1")
        insertTag(2, "T2")

        CocktailToTagTable.insert {
            it[cocktailId] = 1
            it[tagId] = 1
        }

        CocktailToTagTable.insert {
            it[cocktailId] = 1
            it[tagId] = 2
        }

        CocktailToTagTable.insert {
            it[cocktailId] = 2
            it[tagId] = 2
        }
    }
}

private fun insertTag(id: Int, name: String) {
    TagsTable.insert {
        it[this.id] = id
        it[this.name] = name
    }
}

private fun insertCocktail(id: Int, name: String) = CocktailsTable.insert {
    it[this.id] = id
    it[this.name] = name
    it[steps] = arrayOf()
    it[visitCount] = 0
    it[ratingCount] = 10
    it[ratingValue] = 10
}