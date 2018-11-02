package com.moscichowski.wondersTest

import com.moscichowski.wonders.*
import com.moscichowski.wonders.Wonder
import com.moscichowski.wonders.WrongNumberOfCards
import org.junit.Test

class BoardBuildingTests {
    @Test
    fun `Game fails if it dont receive 3x20 cards`() {
        val wonders = (0 until 8).map { Wonder("Test") }
        val cards1 = listOf((0 until 15).map { Card("some") }, (0 until 11).map { Card("some") })
        val cards2 = listOf((0 until 15).map { Card("some") }, (0 until 11).map { Card("some") }, (0 until 11).map { Card("some") })
        assertFails({ it is WrongNumberOfCards }) { Game(wonders, cards1) }
        assertFails({ it is WrongNumberOfCards }) { Game(wonders, cards2) }
    }

    @Test
    fun `Game inits with 3x20 cards`() {
        val wonders = (0 until 8).map { Wonder("Test") }
        val cards = listOf((0 until 20).map { Card("some") }, (0 until 20).map { Card("some") }, (0 until 20).map { Card("some") })
        val game = Game(wonders, cards)
    }
}