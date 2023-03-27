package org.mixdrinks.view.controllers.search

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.ParametersImpl
import io.ktor.server.application.ApplicationCall
import io.mockk.every
import io.mockk.mockk
import org.mixdrinks.view.controllers.search.paggination.Page
import org.mixdrinks.view.controllers.search.paggination.getPage

internal class SearchViewKtTest : FunSpec({

    test("Verify page build") {
        val call = mockCall(0)
        call.getPage(10) shouldBe Page(0, 10)
    }

    test("Verify page build 1") {
        val call = mockCall(1)
        call.getPage(10) shouldBe Page(10, 10)
    }
})

private fun mockCall(pageIndex: Int): ApplicationCall {
    return mockk {
        every { request } answers {
            mockk {
                every { queryParameters } answers {
                    ParametersImpl(
                        values = mapOf(
                            Pair("page", listOf(pageIndex.toString()))
                        )
                    )
                }
            }
        }
    }
}
