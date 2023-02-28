package org.fullness.endtoend

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
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.CocktailsToTastesTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.TastesTable
import org.mixdrinks.view.cocktail.ItemType
import org.mixdrinks.view.v2.controllers.filter.FilterModels
import org.mixdrinks.view.v2.controllers.filter.FilterSource
import org.mixdrinks.view.v2.controllers.filter.filterMetaInfo

class FilterProvidesEndToEndTests : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    test("Verify filters") {
        prepareData(
            listOf(
                FilterData(
                    type = FilterModels.Filters.TAGS,
                    id = 1,
                    cocktailsCount = 1,
                ), FilterData(
                    type = FilterModels.Filters.TAGS,
                    id = 2,
                    cocktailsCount = 10,
                ), FilterData(
                    type = FilterModels.Filters.GOODS,
                    id = 3,
                    cocktailsCount = 11,
                ), FilterData(
                    type = FilterModels.Filters.GOODS,
                    id = 4,
                    cocktailsCount = 2,
                ), FilterData(
                    type = FilterModels.Filters.TOOLS,
                    id = 5,
                    cocktailsCount = 100,
                ), FilterData(
                    type = FilterModels.Filters.TOOLS,
                    id = 6,
                    cocktailsCount = 1000,
                )
            )
        )

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                filterMetaInfo(FilterSource())
            }

            val response = client.get("v2/filters")

            response.status shouldBe HttpStatusCode.OK
            val result = Json.decodeFromString<List<FilterModels.FilterGroup>>(response.bodyAsText())

            result[0].let { filterGroup: FilterModels.FilterGroup ->
                verifyFilterGroup(filterGroup, FilterModels.Filters.GOODS)

                verifyItems(
                    filterGroup.items, listOf(
                        Pair(3, 11),
                        Pair(4, 2),
                    )
                )
            }

            result[1].let { filterGroup: FilterModels.FilterGroup ->
                verifyFilterGroup(filterGroup, FilterModels.Filters.TAGS)

                verifyItems(
                    filterGroup.items, listOf(
                        Pair(2, 10),
                        Pair(1, 1),
                    )
                )
            }

            result[2].let { filterGroup: FilterModels.FilterGroup ->
                verifyFilterGroup(filterGroup, FilterModels.Filters.TOOLS)

                verifyItems(
                    filterGroup.items, listOf(
                        Pair(6, 1000),
                        Pair(5, 100),
                    )
                )
            }
        }
    }
})

private fun verifyFilterGroup(filterGroup: FilterModels.FilterGroup, filterModels: FilterModels.Filters) {
    filterGroup.id shouldBe filterModels.id
    filterGroup.name shouldBe filterModels.translation
    filterGroup.queryName shouldBe filterModels.queryName
}

private fun verifyItems(items: List<FilterModels.FilterItem>, idToCount: List<Pair<Int, Int>>) {
    items shouldBe idToCount.map { (id, count) ->
        FilterModels.FilterItem(
            FilterModels.FilterId(id),
            "Name$id",
            count.toLong()
        )
    }
}

private data class FilterData(
    val type: FilterModels.Filters, val id: Int, val cocktailsCount: Int, val name: String = "Name$id"
)

private fun prepareData(
    tags: List<FilterData>,
) {
    transaction {
        SchemaUtils.create(
            TagsTable,
            CocktailToTagTable,
            ItemsTable,
            CocktailsToItemsTable,
            TastesTable,
            CocktailsToTastesTable,
            TastesTable,
            CocktailsToTastesTable,
        )

        tags.forEachIndexed { index, tag ->
            when (tag.type) {
                FilterModels.Filters.TAGS -> {
                    TagsTable.insert {
                        it[id] = tag.id
                        it[name] = tag.name
                    }

                    repeat(tag.cocktailsCount) {
                        CocktailToTagTable.insert {
                            it[tagId] = tag.id
                            it[cocktailId] = index
                        }
                    }
                }

                FilterModels.Filters.GOODS -> {
                    ItemsTable.insert {
                        it[id] = tag.id
                        it[name] = tag.name
                        it[about] = ""
                        it[relation] = ItemType.GOOD.relation
                        it[visitCount] = 0
                    }

                    repeat(tag.cocktailsCount) {
                        CocktailsToItemsTable.insert {
                            it[itemId] = tag.id
                            it[cocktailId] = index
                            it[unit] = ""
                            it[amount] = 0
                            it[relation] = ItemType.GOOD.relation
                        }
                    }
                }

                FilterModels.Filters.TOOLS -> {
                    ItemsTable.insert {
                        it[id] = tag.id
                        it[name] = tag.name
                        it[about] = ""
                        it[relation] = ItemType.TOOL.relation
                        it[visitCount] = 0
                    }

                    repeat(tag.cocktailsCount) {
                        CocktailsToItemsTable.insert {
                            it[itemId] = tag.id
                            it[cocktailId] = index
                            it[unit] = ""
                            it[amount] = 0
                            it[relation] = ItemType.TOOL.relation
                        }
                    }
                }
            }
        }
    }
}
