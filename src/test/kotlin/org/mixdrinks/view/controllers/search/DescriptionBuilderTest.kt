package org.mixdrinks.view.controllers.search

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.createDataBase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.AlcoholVolumes
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.Good
import org.mixdrinks.data.Tag
import org.mixdrinks.data.Taste
import org.mixdrinks.data.Tool
import org.mixdrinks.dto.FilterGroupId
import org.mixdrinks.dto.FilterId
import org.mixdrinks.view.controllers.filter.FilterModels
import org.mixdrinks.view.controllers.search.DescriptionBuilder
import org.mixdrinks.view.controllers.search.SearchParams

class DescriptionBuilderTest : FunSpec({

    @Suppress("MemberVisibilityCanBePrivate") val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    afterSpec {
        TransactionManager.closeAndUnregister(database)
    }

    beforeSpec {
        prepareData(
            alcoholVolumes = (1..10).toList(),
            taste = (1..10).toList(),
            goods = (1..10).toList(),
            tools = (1..10).toList(),
            tags = (1..10).toList(),
            glassware = (1..10).toList(),
        )
    }

    test("Verify that the alcohol volume description") {
        buildDescription(
            mapOf(
                FilterModels.FilterGroupBackend.ALCOHOL_VOLUME.id to listOf(FilterId(1)),
            )
        ) shouldBe "AlcoholVolumes1 коктейлі"
    }

    test("Verify that the alcohol volume with taste") {
        buildDescription(
            mapOf(
                FilterModels.FilterGroupBackend.ALCOHOL_VOLUME.id to listOf(FilterId(1)),
                FilterModels.FilterGroupBackend.TASTE.id to listOf(FilterId(8), FilterId(9)),
            )
        ) shouldBe "AlcoholVolumes1 Taste8 Taste9 коктейлі"
    }

    test("Verify that the alcohol volume and goods") {
        buildDescription(
            mapOf(
                FilterModels.FilterGroupBackend.ALCOHOL_VOLUME.id to listOf(FilterId(1)),
                FilterModels.FilterGroupBackend.GOODS.id to listOf(FilterId(4), FilterId(5)),
            )
        ) shouldBe "AlcoholVolumes1 коктейлі з Good4 Good5"
    }

    test("Verify description with goods and tags") {
        buildDescription(
            mapOf(
                FilterModels.FilterGroupBackend.GOODS.id to listOf(FilterId(4), FilterId(5)),
                FilterModels.FilterGroupBackend.TAGS.id to listOf(FilterId(7), FilterId(4)),
            )
        ) shouldBe "коктейлі Tag4 Tag7 з Good4 Good5"
    }

    test("Verify description with all but without glassware") {
        buildDescription(
            mapOf(
                FilterModels.FilterGroupBackend.ALCOHOL_VOLUME.id to listOf(FilterId(1)),
                FilterModels.FilterGroupBackend.GOODS.id to listOf(FilterId(4), FilterId(5)),
                FilterModels.FilterGroupBackend.TAGS.id to listOf(FilterId(7), FilterId(4)),
                FilterModels.FilterGroupBackend.TOOLS.id to listOf(FilterId(7), FilterId(4)),
            )
        ) shouldBe "AlcoholVolumes1 коктейлі Tag4 Tag7 з Good4 Good5"
    }

    test("Verify description with all") {
        buildDescription(
            mapOf(
                FilterModels.FilterGroupBackend.ALCOHOL_VOLUME.id to listOf(FilterId(1)),
                FilterModels.FilterGroupBackend.GOODS.id to listOf(FilterId(4), FilterId(5)),
                FilterModels.FilterGroupBackend.TAGS.id to listOf(FilterId(7), FilterId(4)),
                FilterModels.FilterGroupBackend.TOOLS.id to listOf(FilterId(7), FilterId(4)),
                FilterModels.FilterGroupBackend.GLASSWARE.id to listOf(FilterId(1)),
            )
        ) shouldBe "AlcoholVolumes1 коктейлі Tag4 Tag7 з Good4 Good5 в Glassware1"
    }

    test("Verify description just tools") {
        buildDescription(
            mapOf(
                FilterModels.FilterGroupBackend.TOOLS.id to listOf(FilterId(7), FilterId(4)),
            )
        ) shouldBe null
    }

    test("Verify description just glassware") {
        buildDescription(
            mapOf(
                FilterModels.FilterGroupBackend.GLASSWARE.id to listOf(FilterId(7)),
            )
        ) shouldBe "коктейлі в Glassware7"
    }
})

private fun buildDescription(filters: Map<FilterGroupId, List<FilterId>>): String? {
    return DescriptionBuilder().buildDescription(
        SearchParams(filters)
    )
}

@Suppress("LongParameterList")
private fun prepareData(
    alcoholVolumes: List<Int> = emptyList(),
    goods: List<Int> = emptyList(),
    tools: List<Int> = emptyList(),
    taste: List<Int> = emptyList(),
    tags: List<Int> = emptyList(),
    glassware: List<Int> = emptyList(),
) {
    transaction {
        createDataBase()

        alcoholVolumes.forEach {
            AlcoholVolumes.new(id = it) {
                name = "AlcoholVolumes$it"
                slug = "slug_$it"
            }
        }

        goods.forEach {
            Good.new(id = it) {
                name = "Good$it"
                about = "$it"
                slug = "good_$it"
            }
        }

        tools.forEach {
            Tool.new(id = it) {
                name = "Tool$it"
                about = "$it"
                slug = "tool_$it"
            }
        }

        taste.forEach {
            Taste.new(id = it) {
                name = "Taste$it"
                slug = "taste_$it"
            }
        }

        tags.forEach {
            Tag.new(id = it) {
                name = "Tag$it"
                slug = "tag_$it"
            }
        }

        glassware.forEach {
            Glassware.new(id = it) {
                name = "Glassware$it"
                about = "About"
                slug = "glassware_$it"
            }
        }
    }
}
