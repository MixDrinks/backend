package org.mixdrinks.view.cocktail.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test
import org.mixdrinks.view.cocktail.data.CocktailsSource
import org.mixdrinks.view.cocktail.data.FullCocktailData

internal class CocktailsFutureCountCalculatorTest {

    @Test
    fun `Verify future tag count clear exist`() {
        val cocktails = listOf(
            mockCocktail(tagIds = listOf(1)),
            mockCocktail(tagIds = listOf(1, 2)),
            mockCocktail(tagIds = listOf(1, 2, 3)),
        )

        val allTagIds = listOf(1, 2, 3, 4)

        val cocktailsSource = mockCocktailsSource(cocktails, allTagIds = allTagIds)

        val result = CocktailsFutureCountCalculator(cocktailsSource)
            .getFutureCounts(existSearchParam = CocktailsFilterSearchParam(null, emptyList(), emptyList(), emptyList()))

        val expected = FutureCocktailsCounts(
            tagCounts = mapOf(
                1 to 3,
                2 to 2,
                3 to 1,
                4 to 0,
            ),
            goodCounts = emptyMap(),
            toolCounts = emptyMap(),
        )

        Assert.assertEquals(expected, result)
    }

    @Test
    fun `Verify future tag count has exist tag`() {
        val cocktails = listOf(
            mockCocktail(tagIds = listOf(1, 3)),
            mockCocktail(tagIds = listOf(1, 2)),
            mockCocktail(tagIds = listOf(1, 2, 3)),
        )

        val allTagIds = listOf(1, 2, 3)

        val cocktailsSource = mockCocktailsSource(cocktails, allTagIds = allTagIds)

        val result = CocktailsFutureCountCalculator(cocktailsSource)
            .getFutureCounts(
                existSearchParam = CocktailsFilterSearchParam(
                    null,
                    tags = listOf(3),
                    emptyList(),
                    emptyList()
                )
            )

        val expected = FutureCocktailsCounts(
            tagCounts = mapOf(
                1 to 2,
                2 to 1,
                3 to 2
            ),
            goodCounts = emptyMap(),
            toolCounts = emptyMap(),
        )

        Assert.assertEquals(expected, result)
    }

    @Test
    fun `Verify future good count`() {
        val cocktails = listOf(
            mockCocktail(goodIds = listOf(10, 20, 30, 40)),
            mockCocktail(goodIds = listOf(10, 20, 3, 4)),
            mockCocktail(goodIds = listOf(1, 2, 3, 4)),
        )

        val allGoodIds = listOf(1, 2, 3, 4, 10, 20, 30, 40)

        val cocktailsSource = mockCocktailsSource(cocktails = cocktails, allGoodIds = allGoodIds)

        val result = CocktailsFutureCountCalculator(cocktailsSource)
            .getFutureCounts(
                existSearchParam = CocktailsFilterSearchParam(
                    goods = listOf(4),
                    search = null,
                    tags = null,
                    tools = null
                )
            )

        val expected = FutureCocktailsCounts(
            goodCounts = mapOf(
                1 to 1,
                2 to 1,
                3 to 2,
                4 to 2,
                10 to 1,
                20 to 1,
                30 to 0,
                40 to 0,
            ),
            toolCounts = emptyMap(),
            tagCounts = emptyMap(),
        )

        Assert.assertEquals(expected, result)
    }

    @Test
    fun `Verify future tools`() {
        val cocktails = listOf(
            mockCocktail(toolIds = listOf(1, 2)),
            mockCocktail(toolIds = listOf(1)),
        )

        val allToolIds = listOf(1, 2)

        val cocktailsSource = mockCocktailsSource(cocktails = cocktails, allToolIds = allToolIds)

        val result = CocktailsFutureCountCalculator(cocktailsSource)
            .getFutureCounts(
                existSearchParam = CocktailsFilterSearchParam(
                    tools = null,
                    goods = null,
                    search = null,
                    tags = null,
                )
            )

        val expected = FutureCocktailsCounts(
            toolCounts = mapOf(
                1 to 2,
                2 to 1,
            ),
            goodCounts = emptyMap(),
            tagCounts = emptyMap(),
        )

        Assert.assertEquals(expected, result)
    }

    private fun mockCocktail(
        goodIds: List<Int> = emptyList(),
        toolIds: List<Int> = emptyList(),
        tagIds: List<Int> = emptyList(),
    ): FullCocktailData {
        return FullCocktailData(
            id = 0,
            name = "",
            rating = null,
            visitCount = 0,
            goodIds = goodIds,
            toolIds = toolIds,
            tagIds = tagIds,
        )
    }

    private fun mockCocktailsSource(
        cocktails: List<FullCocktailData>,
        allGoodIds: List<Int> = emptyList(),
        allToolIds: List<Int> = emptyList(),
        allTagIds: List<Int> = emptyList(),
    ): CocktailsSource {
        return mockk {
            every { this@mockk.cocktails } answers {
                cocktails
            }
            every { this@mockk.allTagIds } answers {
                allTagIds
            }
            every { this@mockk.allGoodIds } answers {
                allGoodIds
            }
            every { this@mockk.allToolIds } answers {
                allToolIds
            }
        }
    }
}
