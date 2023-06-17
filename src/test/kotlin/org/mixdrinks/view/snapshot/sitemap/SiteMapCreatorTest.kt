package org.mixdrinks.view.snapshot.sitemap

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.collections.shouldContainAll
import org.createDataBase
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.AlcoholVolumes
import org.mixdrinks.data.Glassware
import org.mixdrinks.data.Good
import org.mixdrinks.data.Tag
import org.mixdrinks.data.Taste
import org.mixdrinks.data.Tool
import org.mixdrinks.domain.FilterGroups

class SiteMapCreatorTest : AnnotationSpec() {

    @Suppress("MemberVisibilityCanBePrivate")
    val database =
        org.jetbrains.exposed.sql.Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    @After
    fun afterSpec() {
        TransactionManager.closeAndUnregister(database)
    }

    @Test
    fun verifySiteMap() {
        prepareData()

        SiteMapCreator().siteMapDto shouldContainAll listOf(
            "${FilterGroups.GOODS.queryName.value}=good1",
            "${FilterGroups.TOOLS.queryName.value}=tool1",
            "${FilterGroups.TOOLS.queryName.value}=tool2",
            "${FilterGroups.GLASSWARE.queryName.value}=glassware1",
            "${FilterGroups.TASTE.queryName.value}=taste1",
            "${FilterGroups.TAGS.queryName.value}=tag1",
            "${FilterGroups.ALCOHOL_VOLUME.queryName.value}=alcoholVolume1"
        )
    }

    private fun prepareData() {
        transaction {
            createDataBase()

            Good.new {
                name = "good1"
                slug = "good1"
                about = "about"
            }

            Tool.new {
                name = "tool1"
                slug = "tool1"
                about = "about"
            }

            Tool.new {
                name = "tool2"
                slug = "tool2"
                about = "about"
            }

            Glassware.new {
                name = "glassware1"
                slug = "glassware1"
                about = "about"
            }

            Taste.new {
                name = "taste1"
                slug = "taste1"
            }

            Tag.new {
                name = "tag1"
                slug = "tag1"
            }

            AlcoholVolumes.new {
                name = "alcoholVolume1"
                slug = "alcoholVolume1"
            }
        }
    }
}
