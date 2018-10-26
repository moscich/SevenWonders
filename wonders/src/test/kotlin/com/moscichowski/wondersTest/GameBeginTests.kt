package com.moscichowski.wondersTest

import com.moscichowski.wonders.*
import org.junit.Test
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

class GameBeginTests {
    @Test
    fun `game should be initialized with 8 wonders to choose from`() {
        val wonders = (0 until 7).map { Wonder("Test") }
//        assertFails { val game = Game(Board(listOf()), wonders) }
        assertFails({ it is Requires8WondersError }) { val game = Game(Board(listOf()), wonders) }
    }

    private fun assertFails(error: (Error) -> Boolean, block: () -> Unit) {
        var errorEmited = false
        try {
            block()
        } catch (e: Error) {
            errorEmited = true
            assertTrue(error(e), "Different error expected")
        }
        if (!errorEmited) {
            fail("should throw")
        }
    }

    private fun assertFails(error: Error) {

    }
}