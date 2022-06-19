package org.mixdrinks.view.cocktail.domain

import org.junit.Assert
import org.junit.Test
import org.mixdrinks.view.cocktail.data.FullCocktailData

internal class CocktailSearchFilterKtTest {

    @Test
    fun `Check is filter remove by query`() {
        val list = listOf(
            createFullCocktailData("TestName"),
            createFullCocktailData("SomeName"),
            createFullCocktailData("Testname"),
            createFullCocktailData("TesTName"),
        )

        val result =
            list.filterBySearch(createSearchParam("tN"))

        Assert.assertEquals(listOf(list[0], list[2], list[3]), result)
    }

    @Test
    fun `Check is filter remove all`() {
        val list = listOf(
            createFullCocktailData(),
            createFullCocktailData(),
            createFullCocktailData(),
            createFullCocktailData(),
        )

        val result =
            list.filterBySearch(createSearchParam())

        Assert.assertEquals(list, result)
    }

    @Test
    fun `Check is filter keep by tags`() {
        val list = listOf(
            createFullCocktailData(tagsIds = listOf(1, 2, 3, 4)),
            createFullCocktailData(tagsIds = listOf(1, 2, 3)),
            createFullCocktailData(tagsIds = listOf(1, 4)),
            createFullCocktailData(tagsIds = listOf(500, 501)),
        )

        Assert.assertEquals(listOf(list[0], list[1]), list.filterBySearch(createSearchParam(tagIds = listOf(1, 2))))
        Assert.assertEquals(listOf(list[3]), list.filterBySearch(createSearchParam(tagIds = listOf(500))))
        Assert.assertEquals(listOf(list[0], list[2]), list.filterBySearch(createSearchParam(tagIds = listOf(1, 4))))
    }


    private fun createSearchParam(
        search: String? = null,
        tagIds: List<Int>? = null,
    ): CocktailsFilterSearchParam {
        return CocktailsFilterSearchParam(search = search, tags = tagIds, goods = null, tools = null)
    }

    private fun createFullCocktailData(
        name: String? = null,
        tagsIds: List<Int> = emptyList(),
    ): FullCocktailData {
        return FullCocktailData(
            id = 0,
            name = name ?: "Def name",
            rating = null,
            visitCount = 0,
            goodIds = emptyList(),
            toolIds = emptyList(),
            tagIds = tagsIds
        )
    }

}