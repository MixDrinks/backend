package org.mixdrinks.cocktails.visit

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.Instant
import org.createDataBase
import org.fullness.endtoend.TestCocktail
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.cocktails.CocktailMapper
import org.mixdrinks.data.CocktailsTable
import org.mixdrinks.users.UsersTable
import org.mixdrinks.view.cocktail.CompactCocktailVM
import org.mixdrinks.view.controllers.search.paggination.Page

class VisitCocktailsRepositoryTest : AnnotationSpec() {

    @Suppress("MemberVisibilityCanBePrivate")
    val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    @AfterAll
    fun afterSpec() {
        TransactionManager.closeAndUnregister(database)
    }

    @Test
    fun `verify return cocktails for user empty`() {
        transaction {
            createDataBase()

            TestCocktail.new(id = 1) {
                name = "Test cocktail 1"
                steps = arrayOf("Test step 1", "Test step 2")
                visitCount = 1
                ratingCount = 1
                ratingValue = 3
                slug = "test-cocktail-1"
            }

            UsersTable.insert {
                it[id] = "some_user"
            }

            UsersTable.insert {
                it[id] = "some_user_2"
            }


            VisitTable.insert {
                it[cocktailId] = 1
                it[userId] = "some_user"
                it[time] = Instant.fromEpochMilliseconds(1)
            }
        }

        val mapper = CocktailMapper()
        VisitCocktailsRepository(mapper).getVisitedCocktails("some_user_2") shouldBe listOf()
    }

    @Test
    fun `verify return cocktails for user not empty`() {
        transaction {
            createDataBase()

            TestCocktail.new(id = 1) {
                name = "Test cocktail 1"
                steps = arrayOf("Test step 1", "Test step 2")
                visitCount = 1
                ratingCount = 1
                ratingValue = 3
                slug = "test-cocktail-1"
            }

            UsersTable.insert {
                it[id] = "some_user"
            }

            VisitTable.insert {
                it[cocktailId] = 1
                it[userId] = "some_user"
                it[time] = Instant.fromEpochMilliseconds(1)
            }
        }

        val mockResult = mockk<CompactCocktailVM>(relaxed = true)

        val mapper = mockk<CocktailMapper> {
            every { createCocktails(any()) } answers { mockResult }
        }

        VisitCocktailsRepository(mapper).getVisitedCocktails("some_user") shouldBe listOf(mockResult)
    }

    @Test
    fun `verify return cocktails for user not empty with paggination`() {
        transaction {
            createDataBase()

            (0..50).forEach { id ->
                TestCocktail.new(id = id) {
                    name = "Test cocktail 1"
                    steps = arrayOf("Test step 1", "Test step 2")
                    visitCount = 1
                    ratingCount = 1
                    ratingValue = 3
                    slug = "test-cocktail-1"
                }
            }

            UsersTable.insert {
                it[id] = "some_user"
            }

            (0..50).forEach { index ->
                VisitTable.insert {
                    it[cocktailId] = index
                    it[userId] = "some_user"
                    it[time] = Instant.fromEpochMilliseconds(index.toLong())
                }
            }
        }

        val mockResult = mockk<CompactCocktailVM>(relaxed = true)

        val mapper = mockk<CocktailMapper> {
            every { createCocktails(any()) } answers { mockResult }
        }

        val result = VisitCocktailsRepository(mapper).getVisitedCocktails("some_user", Page(5, 5))
        result shouldBe listOf(mockResult, mockResult, mockResult, mockResult, mockResult)
    }
}
