package org.mixdrinks.view.snapshot

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.CocktailsToGoodsTable
import org.mixdrinks.data.FullCocktail
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.Good
import org.mixdrinks.data.Tag
import org.mixdrinks.data.Taste
import org.mixdrinks.data.Tool
import org.mixdrinks.dto.CocktailDto
import org.mixdrinks.dto.CocktailId
import org.mixdrinks.dto.FilterGroupDto
import org.mixdrinks.dto.FilterWithCocktailIdsDto
import org.mixdrinks.dto.GlasswareDto
import org.mixdrinks.dto.GlasswareId
import org.mixdrinks.dto.GoodDto
import org.mixdrinks.dto.GoodId
import org.mixdrinks.dto.GoodRelationDto
import org.mixdrinks.dto.SnapshotDto
import org.mixdrinks.dto.TagDto
import org.mixdrinks.dto.TagId
import org.mixdrinks.dto.TasteDto
import org.mixdrinks.dto.TasteId
import org.mixdrinks.dto.ToolDto
import org.mixdrinks.dto.ToolId
import org.mixdrinks.view.v2.controllers.filter.FilterCache

class SnapshotCreator(
    private val filterCache: FilterCache,
) {

    fun getSnapshot(): SnapshotDto {
        return transaction {
            return@transaction SnapshotDto(
                cocktails = getCocktails(),
                goods = getGoods(),
                tags = getTags(),
                tastes = getTastes(),
                tools = getTools(),
                glassware = getGlassware(),
                filterGroups = getFilterGroups(),
            )
        }
    }

    private fun getFilterGroups(): List<FilterGroupDto> {
        return filterCache.fullFilters
            .map { (filterModel, filters) ->
                FilterGroupDto(
                    id = filterModel.id,
                    name = filterModel.name,
                    filters = filters.map { filter ->
                        FilterWithCocktailIdsDto(
                            id = filter.id,
                            name = filter.name,
                            cocktailIds = filter.cocktailIds,
                        )
                    },
                    selectionType = filterModel.selectionType,
                )
            }
    }

    private fun getGlassware(): List<GlasswareDto> {
        return Glassware.all().map { glassware ->
            GlasswareDto(
                id = GlasswareId(glassware.id.value),
                name = glassware.name,
                about = glassware.about,
            )
        }
    }

    private fun getTools(): List<ToolDto> {
        return Tool.all().map { tool ->
            ToolDto(
                id = ToolId(tool.id.value),
                name = tool.name,
                about = tool.about,
            )
        }
    }

    private fun getTastes(): List<TasteDto> {
        return Taste.all().map { taste ->
            TasteDto(
                id = TasteId(taste.id.value),
                name = taste.name,
            )
        }
    }

    private fun getTags(): List<TagDto> {
        return Tag.all().map { tag ->
            TagDto(
                id = TagId(tag.id.value),
                name = tag.name,
            )
        }
    }

    private fun getGoods(): List<GoodDto> {
        return Good.all().map { good ->
            GoodDto(
                id = GoodId(good.id.value),
                name = good.name,
                about = good.about,
            )
        }
    }

    private fun getCocktails(): List<CocktailDto> {
        return FullCocktail.all().map { fullCocktail ->
            val rawId = fullCocktail.id.value
            val goods = CocktailsToGoodsTable.select {
                (CocktailsToGoodsTable.cocktailId eq rawId)
            }.map {
                GoodRelationDto(
                    GoodId(it[CocktailsToGoodsTable.goodId].value),
                    it[CocktailsToGoodsTable.amount],
                    it[CocktailsToGoodsTable.unit]
                )
            }
            CocktailDto(
                id = CocktailId(rawId),
                name = fullCocktail.name,
                receipt = fullCocktail.steps.toList(),
                goods = goods,
                tools = fullCocktail.tools.map { ToolId(it.id.value) },
                tags = fullCocktail.tags.map { TagId(it.id.value) },
                tastes = fullCocktail.tastes.map { TasteId(it.id.value) },
                glassware = GlasswareId(fullCocktail.glassware.first().id.value),
            )
        }
    }
}