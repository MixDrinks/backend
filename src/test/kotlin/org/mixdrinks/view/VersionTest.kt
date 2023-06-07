package org.mixdrinks.view

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VersionTest {

    @Test
    fun `version code is generated from version name 1 1 1`() {
        val version = Version("1.1.1")
        assertEquals(100_000 + 1_000 + 1, version.code)
    }

    @Test
    fun `version code is generated from version name`() {
        val version = Version("1.2.3")
        assertEquals(100_000 + 2_000 + 3, version.code)
    }

    @Test
    fun `version code is generated from version name with more than one digit`() {
        val version = Version("1.2.34")
        assertEquals(100_000 + 2_000 + 34, version.code)
    }

    @Test
    fun `version code is generated from version name with more than one digit and more than one dot`() {
        val version = Version("0.0.1")
        assertEquals(1, version.code)
    }

    @Test
    fun `Verify code is generated from version name from pull request template`() {
        val version = Version("1.2.88-10")
        assertEquals(100_000 + 2_000 + 88, version.code)
    }

    @Test
    fun `Verify code is generated from version name from pull request template 2`() {
        val version = Version("1.10.70-171")
        assertEquals(100_000 + 10_000 + 70, version.code)
    }
}
