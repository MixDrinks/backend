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
import org.createDataBase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToGoodsTable
import org.mixdrinks.data.CocktailsToToolsTable
import org.mixdrinks.data.GoodsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.data.ToolsTable
import org.mixdrinks.dto.FilterId
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

            result[3].let { filterGroup: FilterModels.FilterGroup ->
                verifyFilterGroup(filterGroup, FilterModels.Filters.GOODS)

                verifyItems(
                    filterGroup.items, listOf(
                        Pair(3, 11),
                        Pair(4, 2),
                    )
                )
            }

            result[4].let { filterGroup: FilterModels.FilterGroup ->
                verifyFilterGroup(filterGroup, FilterModels.Filters.TAGS)

                verifyItems(
                    filterGroup.items, listOf(
                        Pair(2, 10),
                        Pair(1, 1),
                    )
                )
            }

            result[5].let { filterGroup: FilterModels.FilterGroup ->
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
            FilterId(id),
            "Name$id",
            count.toLong()
        )
    }
}

private data class FilterData(
    val type: FilterModels.Filters, val id: Int, val cocktailsCount: Int, val name: String = "Name$id"
)

@Suppress("LongMethod")
private fun prepareData(
    tags: List<FilterData>,
) {
    transaction {
        createDataBase()

        tags.forEachIndexed { index, tag ->
            CocktailsTable.insert {
                it[id] = index
                it[name] = "Cocktail$index"
                it[steps] = arrayOf()
                it[visitCount] = 10
                it[ratingCount] = 10
                it[ratingValue] = 10
            }
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
                    GoodsTable.insert {
                        it[id] = tag.id
                        it[name] = tag.name
                        it[about] = ""
                        it[visitCount] = 0
                    }

                    repeat(tag.cocktailsCount) {
                        CocktailsToGoodsTable.insert {
                            it[goodId] = tag.id
                            it[cocktailId] = index
                            it[unit] = ""
                            it[amount] = 0
                        }
                    }
                }

                FilterModels.Filters.TOOLS -> {
                    ToolsTable.insert {
                        it[id] = tag.id
                        it[name] = tag.name
                        it[about] = ""
                        it[visitCount] = 0
                    }

                    repeat(tag.cocktailsCount) {
                        CocktailsToToolsTable.insert {
                            it[toolId] = tag.id
                            it[cocktailId] = index
                        }
                    }
                }

                else -> {}
            }
        }
    }
}
