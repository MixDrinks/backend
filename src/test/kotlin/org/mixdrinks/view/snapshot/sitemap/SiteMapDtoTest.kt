package org.mixdrinks.view.snapshot.sitemap

import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.jupiter.api.Test

class SiteMapDtoTest {

    @Test
    fun `Verify sitemap serialization`() {
        val siteMap = Urlset(
            listOf("https://mixdrinks.org/url1", "https://mixdrinks.org/url2").map { Url(it) }
        )

        XML {
            this.recommended()
        }.encodeToString(siteMap) shouldBe """
            <urlset>
                <url>
                    <loc>https://mixdrinks.org/url1</loc>
                </url>
                <url>    private val snapshotDto: SnapshotDto = transaction {
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

                    <loc>https://mixdrinks.org/url2</loc>
                </url>
            </urlset>
        """.trimIndent()
    }
}