package org.mixdrinks.view.v2

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

internal class UtilsKtTest : FunSpec({
    test("Verify round") {
        roundScore(1.222F) shouldBe 1.2F
        roundScore(4.992F) shouldBe 4.9F
    }
}
)
