package org.mixdrinks.view.cocktail.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.mixdrinks.view.cocktail.CocktailFilter
import org.mixdrinks.view.cocktail.OffsetToBig
import kotlin.test.Test

internal class FilterKtTest {

    @Test
    fun verifyReturnAll() {
        val list = listOf(
            cocktailFilter(),
            cocktailFilter(),
        )

        val result = filterCocktails(
            cocktails = list, search = null, tags = null, goods = null, tools = null, offset = null, limit = null
        )

        Assert.assertEquals(list, result.list)
    }

    @Test
    fun verifyFilterByNameReturnAll() {
        val list = listOf(
            cocktailFilter("TestName"),
            cocktailFilter("SomeName"),
            cocktailFilter("Testname"),
        )

        val result = filterCocktails(
            cocktails = list, search = "tN", tags = null, goods = null, tools = null, offset = null, limit = null
        )

        Assert.assertEquals(listOf(list[0], list[2]), result.list)
    }

    @Test
    fun verifyFilterByTagsId() {
        val list = listOf(
            cocktailFilter(mockTagIds = listOf(1, 3)),
            cocktailFilter(mockTagIds = listOf(4, 3)),
            cocktailFilter(mockTagIds = listOf(224, 10)),
        )

        val result = filterCocktails(
            cocktails = list, search = null, tags = listOf(3), goods = null, tools = null, offset = null, limit = null
        )

        Assert.assertEquals(listOf(list[0], list[1]), result.list)
    }

    @Test
    fun verifyFilterByTagsOne() {
        val list = listOf(
            cocktailFilter(mockTagIds = listOf(1, 3)),
            cocktailFilter(mockTagIds = listOf(4, 3)),
            cocktailFilter(mockTagIds = listOf(224, 10)),
        )

        val result = filterCocktails(
            cocktails = list, search = null, tags = listOf(224), goods = null, tools = null, offset = null, limit = null
        )

        Assert.assertEquals(listOf(list[2]), result.list)
    }

    @Test
    fun verifyFilterByTwoTags() {
        val list = listOf(
            cocktailFilter(mockTagIds = listOf(1, 3, 5)),
            cocktailFilter(mockTagIds = listOf(4, 3, 6)),
            cocktailFilter(mockTagIds = listOf(224, 3, 1)),
            cocktailFilter(mockTagIds = listOf(1, 3, 5, 10)),
        )

        val result = filterCocktails(
            cocktails = list,
            search = null,
            tags = listOf(1, 3, 5),
            goods = null,
            tools = null,
            offset = null,
            limit = null
        )

        Assert.assertEquals(listOf(list[0], list[3]), result.list)
    }


    @Test
    fun verifyFilterOffsetMoreThanList() {
        val list = listOf(
            cocktailFilter(mockTagIds = listOf(1, 3)),
            cocktailFilter(mockTagIds = listOf(4, 3)),
            cocktailFilter(mockTagIds = listOf(10, 3)),
            cocktailFilter(mockTagIds = listOf(4, 3)),
            cocktailFilter(mockTagIds = listOf(3, 10)),
            cocktailFilter(mockTagIds = listOf(224, 10)),
        )

        Assert.assertThrows(OffsetToBig::class.java) {
            filterCocktails(
                cocktails = list, search = null, tags = listOf(3), goods = null, tools = null, offset = 10, limit = null
            )
        }
    }

    @Test
    fun verifyFilterLimit() {
        val list = listOf(
            cocktailFilter(mockTagIds = listOf(1, 3)),
            cocktailFilter(mockTagIds = listOf(4, 3)),
            cocktailFilter(mockTagIds = listOf(10, 3)),
            cocktailFilter(mockTagIds = listOf(4, 3)),
            cocktailFilter(mockTagIds = listOf(3, 10)),
            cocktailFilter(mockTagIds = listOf(224, 10)),
        )


        val result = filterCocktails(
            cocktails = list, search = null, tags = listOf(3), goods = null, tools = null, offset = null, limit = 2
        )

        Assert.assertEquals(listOf(list[0], list[1]), result.list)
        Assert.assertEquals(5, result.totalCount)
    }

    @Test
    fun verifyFilterLimitMoreThanList() {
        val list = listOf(
            cocktailFilter(mockTagIds = listOf(1, 3)),
            cocktailFilter(mockTagIds = listOf(4, 3)),
            cocktailFilter(mockTagIds = listOf(10, 3)),
            cocktailFilter(mockTagIds = listOf(4, 3)),
            cocktailFilter(mockTagIds = listOf(3, 10)),
            cocktailFilter(mockTagIds = listOf(224, 10)),
        )


        val result = filterCocktails(
            cocktails = list, search = null, tags = listOf(3), goods = null, tools = null, offset = null, limit = 20
        )

        Assert.assertEquals(listOf(list[0], list[1], list[2], list[3], list[4]), result.list)
        Assert.assertEquals(5, result.totalCount)
    }

    private fun cocktailFilter(
        mockName: String? = null,
        mockTagIds: List<Int>? = null,
    ): CocktailFilter {
        return mockk(relaxed = true, relaxUnitFun = true) {
            mockName?.let { every { name } answers { mockName } }
            mockTagIds?.let { every { tagIds } answers { mockTagIds } }
        }
    }
}