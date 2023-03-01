package org.mixdrinks.view.v2.controllers.snapshot

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailToTagTable
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.data.CocktailsToItemsTable
import org.mixdrinks.data.ItemsTable
import org.mixdrinks.data.TagsTable
import org.mixdrinks.view.cocktail.ItemType
import org.mixdrinks.view.cocktail.TagVM
import org.mixdrinks.view.v2.data.CocktailId
import org.mixdrinks.view.v2.data.ItemId
import org.mixdrinks.view.v2.data.TagId

fun Application.snapshot() {
    routing {
        get("v2/snapshot") {
            call.respond(transaction {
                val cocktails = getCocktailsSnapshot()
                val items = getItemsSnapshot()

                val tags = TagsTable.selectAll().map {
                    TagVM(
                        TagId(it[TagsTable.id].value),
                        it[TagsTable.name],
                    )
                }

                val cocktailToTags = getTagsSnapshot()

                val cocktailToGoods =
                    CocktailsToItemsTable.select { CocktailsToItemsTable.relation eq ItemType.GOOD.relation }.map {
                        Snapshot.CocktailToGood(
                            CocktailId(it[CocktailsToItemsTable.cocktailId]),
                            ItemId(it[CocktailsToItemsTable.itemId]),
                            it[CocktailsToItemsTable.amount],
                            it[CocktailsToItemsTable.unit]
                        )
                    }

                val cocktailToTools =
                    CocktailsToItemsTable.select { CocktailsToItemsTable.relation eq ItemType.TOOL.relation }.map {
                        Snapshot.CocktailToTool(
                            CocktailId(it[CocktailsToItemsTable.cocktailId]),
                            ItemId(it[CocktailsToItemsTable.itemId]),
                            it[CocktailsToItemsTable.amount],
                            it[CocktailsToItemsTable.unit]
                        )
                    }

                return@transaction Snapshot(
                    cocktails, items, tags, cocktailToTags, cocktailToGoods, cocktailToTools
                )
            })
        }
    }
}

private fun getTagsSnapshot() = CocktailToTagTable.selectAll().map {
    Snapshot.CocktailToTag(
        CocktailId(it[CocktailToTagTable.cocktailId]), TagId(it[CocktailToTagTable.tagId])
    )
}

private fun getItemsSnapshot() = ItemsTable.selectAll().map {
    Snapshot.Item(
        ItemId(it[ItemsTable.id].value),
        it[ItemsTable.name],
        it[ItemsTable.about],
        it[ItemsTable.relation],
    )
}

private fun getCocktailsSnapshot(): List<Snapshot.Cocktail> {
    val cocktails = CocktailsTable.selectAll().map {
        val cocktailId = CocktailId(it[CocktailsTable.id].value)
        Snapshot.Cocktail(
            cocktailId, it[CocktailsTable.name], it[CocktailsTable.steps].toList(), getCocktailRelation(cocktailId)
        )
    }
    return cocktails
}

private fun getCocktailRelation(cocktailId: CocktailId): Snapshot.CocktailRelation {
    return Snapshot.CocktailRelation(
        CocktailToTagTable.select {
            CocktailToTagTable.cocktailId eq cocktailId.value
        }
            .map { TagId(it[CocktailToTagTable.tagId]) },
        CocktailsToItemsTable.select {
            CocktailsToItemsTable.cocktailId eq cocktailId.value and
                    (CocktailsToItemsTable.relation eq ItemType.GOOD.relation)
        }
            .map { ItemId(it[CocktailsToItemsTable.itemId]) },
        CocktailsToItemsTable.select {
            CocktailsToItemsTable.cocktailId eq cocktailId.value and
                    (CocktailsToItemsTable.relation eq ItemType.TOOL.relation)
        }
            .map { ItemId(it[CocktailsToItemsTable.itemId]) },
    )
}

@Serializable
data class Snapshot(
    @SerialName("cocktails") val cocktails: List<Cocktail>,
    @SerialName("items") val items: List<Item>,
    @SerialName("tags") val tags: List<TagVM>,
    @SerialName("cocktailToTags") val cocktailToTags: List<CocktailToTag>,
    @SerialName("cocktailToGoods") val cocktailToGoods: List<CocktailToGood>,
    @SerialName("cocktailToTools") val cocktailToTools: List<CocktailToTool>,
) {

    @Serializable
    data class CocktailToTag(
        @SerialName("cocktailId") val cocktailId: CocktailId,
        @SerialName("tagId") val tagId: TagId,
    )

    @Serializable
    data class CocktailToTool(
        @SerialName("cocktailId") val cocktailId: CocktailId,
        @SerialName("toolId") val toolId: ItemId,
        @SerialName("amount") val amount: Int,
        @SerialName("unit") val unit: String,
    )

    @Serializable
    data class CocktailToGood(
        @SerialName("cocktailId") val cocktailId: CocktailId,
        @SerialName("goodId") val toolId: ItemId,
        @SerialName("amount") val amount: Int,
        @SerialName("unit") val unit: String,
    )

    @Serializable
    data class Cocktail(
        @SerialName("id") val id: CocktailId,
        @SerialName("name") val name: String,
        @SerialName("steps") val steps: List<String>,
        @SerialName("relation") val relation: CocktailRelation,
    )

    @Serializable
    data class CocktailRelation(
        @SerialName("tagIds") val tagIds: List<TagId>,
        @SerialName("goodIds") val goodIds: List<ItemId>,
        @SerialName("toolIds") val toolIds: List<ItemId>,
    )

    @Serializable
    data class Item(
        @SerialName("id") val id: ItemId,
        @SerialName("name") val name: String,
        @SerialName("description") val description: String,
        @SerialName("relation") val relation: Int,
    )
}
