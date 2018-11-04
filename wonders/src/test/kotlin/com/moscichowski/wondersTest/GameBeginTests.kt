package com.moscichowski.wondersTest

import com.moscichowski.wonders.*
import com.moscichowski.wonders.model.Wonder
import org.junit.Test
import kotlin.test.*

class GameBeginTests {
    @Test
    fun `game should be initialized with 8 wonders to choose from`() {
        val wonders = (0 until 7).map { Wonder("Test") }
        assertFails({ it is Requires8WondersError }) { Wonders(wonders, cards) }
    }

    @Test
    fun `game should be set to choose wonders phase`() {
        val wonders = Wonders(testWonders, cards)

        assertEquals(GameState.WONDERS_SELECT, wonders.game.state)
    }

    @Test
    fun `game should allow to select 4 wonders at the beginning`() {
        val wonders = Wonders(testWonders, cards)

        assertEquals(4, wonders.game.wonders.count())
    }

    @Test
    fun `wonders should enable possibility to choose wonders`() {
        val wonders = Wonders(testWonders, cards)
        wonders.takeAction(ChooseWonder("Test 0"))
        assertEquals(1, wonders.game.currentPlayer)
        assertEquals(3, wonders.game.wonders.count())

        wonders.takeAction(ChooseWonder("Test 1"))
        assertEquals(1, wonders.game.currentPlayer)
        assertEquals(2, wonders.game.wonders.count())

        wonders.takeAction(ChooseWonder("Test 2"))
        assertEquals(0, wonders.game.currentPlayer)
        assertEquals(1, wonders.game.wonders.count())

        wonders.takeAction(ChooseWonder("Test 3"))
        assertEquals(1, wonders.game.currentPlayer)
        assertEquals(4, wonders.game.wonders.count())

        wonders.takeAction(ChooseWonder("Test 4"))
        assertEquals(0, wonders.game.currentPlayer)
        assertEquals(3, wonders.game.wonders.count())

        wonders.takeAction(ChooseWonder("Test 5"))
        assertEquals(0, wonders.game.currentPlayer)
        assertEquals(2, wonders.game.wonders.count())

        wonders.takeAction(ChooseWonder("Test 6"))
        assertEquals(1, wonders.game.currentPlayer)
        assertEquals(1, wonders.game.wonders.count())

        wonders.takeAction(ChooseWonder("Test 7"))
        assertEquals(0, wonders.game.currentPlayer)
        assertEquals(0, wonders.game.wonders.count())

        assertEquals(GameState.REGULAR, wonders.game.state)
        assertEquals(listOf("Test 0", "Test 3", "Test 5", "Test 6"), wonders.game.player1.wonders.map { it.second.name })
        assertEquals(listOf("Test 1", "Test 2", "Test 4", "Test 7"), wonders.game.player2.wonders.map { it.second.name })

        assertNull(wonders.game.player1.wonders.find { it.first })
        assertNull(wonders.game.player2.wonders.find { it.first })
    }

    @Test
    fun `game should be initialized with 3 boards 20 cards each`() {

    }

}