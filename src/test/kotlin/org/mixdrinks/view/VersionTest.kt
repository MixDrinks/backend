package org.mixdrinks.view

import org.junit.jupiter.api.Test

class VersionTest {

    @Test
    fun `version code is generated from version name`() {
        val version = Version("1.2.3")
        assert(version.code == 123)
    }

    @Test
    fun `version code is generated from version name with more than one digit`() {
        val version = Version("1.2.34")
        assert(version.code == 1234)
    }

    @Test
    fun `version code is generated from version name with more than one digit and more than one dot`() {
        val version = Version("0.0.1")
        assert(version.code == 1)
    }
}
