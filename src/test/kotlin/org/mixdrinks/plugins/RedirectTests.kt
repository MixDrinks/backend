package org.mixdrinks.plugins

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.mixdrinks.data.RedirectsTable

class RedirectTests : AnnotationSpec() {

    @Suppress("MemberVisibilityCanBePrivate")
    val database =
        Database.connect("jdbc:h2:mem:test_db_22;DB_CLOSE_DELAY=-1;IGNORECASE=true;")

    @AfterAll
    fun afterSpec() {
        TransactionManager.closeAndUnregister(database)
    }

    @Test
    fun verifyRedirectWorks() {
        transaction {
            SchemaUtils.drop(RedirectsTable)
            SchemaUtils.create(RedirectsTable)

            RedirectsTable.insert {
                it[from] = "/tools/11_new"
                it[to] = "/tools/some_slug"
            }
        }

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                configureRedirectMiddleWare()
            }

            val response = client.get("/v2/filter/tools=1120") {
                this.headers.append("x-user-path", "/tools/11_new")
            }

            response.status shouldBe HttpStatusCode.OK
            Json.decodeFromString<SingleRedirectResponse>(response.bodyAsText()) shouldBe SingleRedirectResponse("/tools/some_slug")
        }
    }

    @Test
    fun verifyRedirectIgnoresWitoutHeaders() {
        transaction {
            SchemaUtils.drop(RedirectsTable)
            SchemaUtils.create(RedirectsTable)

            RedirectsTable.insert {
                it[from] = "tools/1120"
                it[to] = "tools/some_slug"
            }
        }

        testApplication {
            application {
                install(ContentNegotiation) {
                    json()
                }
                configureRedirectMiddleWare()
            }

            val response = client.config {
                this.followRedirects = false
            }.get("/v2/filter/tools=1120?page=0")

            response.status shouldBe HttpStatusCode.NotFound
        }
    }
}
