package com.moscichowski.wondersTest

import com.moscichowski.wonders.*
import com.moscichowski.wonders.model.ExtraTurn
import com.moscichowski.wonders.model.ScienceToken
import com.moscichowski.wonders.model.Wonder
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class MovingToNewAgeTests {

    @Test
    fun `When military 0 next player is current active player 1`() {
        val wonders = lastCardAge1()
        wonders.takeAction(TakeCard("last card"))
        assertEquals(0, wonders.game.currentPlayer)
    }

    @Test
    fun `When military 0 next player is current active player 2`() {
        val wonders = lastCardAge1()
        wonders.game.currentPlayer = 1
        wonders.takeAction(TakeCard("last card"))
        assertEquals(1, wonders.game.currentPlayer)
    }

    @Test
    fun `When military negative next player 0 choose`() {
        val wonders = lastCardAge1()
        wonders.game.military = -1
        wonders.takeAction(TakeCard("last card"))

        assertEquals(0, wonders.game.currentPlayer)
        assertEquals(GameState.CHOOSE_PLAYER, wonders.game.state)
    }

    @Test
    fun `When military positive next player 1 choose`() {
        val wonders = lastCardAge1()
        wonders.game.military = 1
        wonders.takeAction(TakeCard("last card"))

        assertEquals(1, wonders.game.currentPlayer)
        assertEquals(GameState.CHOOSE_PLAYER, wonders.game.state)
    }

    @Test
    fun `Extra turn effect is lost`() {
        val wonders = lastCardAge1()
        wonders.game.military = 1
        wonders.game.player1.wonders = listOf(WonderPair(false, Wonder("Wonder", features = listOf(ExtraTurn))))
        wonders.takeAction(BuildWonder("last card", "Wonder"))
        assertEquals(1, wonders.game.currentPlayer)
        assertEquals(GameState.CHOOSE_PLAYER, wonders.game.state)
    }

    @Test
    fun `should build second board after first cleared with take_card`() {
        val wonders = lastCardAge1()
        wonders.takeAction(TakeCard("last card"))
        assertEquals(20, wonders.game.board?.elements?.count())
        assertEquals(12, wonders.game.board?.elements?.count { it.card?.name == "epoh 2" } )
    }

    @Test
    fun `should build second board after first cleared with wonder build`() {
        val wonders = lastCardAge1()
        wonders.game.player1.wonders = listOf(WonderPair(false, Wonder("Wonder")))
        wonders.takeAction(BuildWonder("last card", "Wonder"))
        assertEquals(20, wonders.game.board?.elements?.count())
        assertEquals(12, wonders.game.board?.elements?.count { it.card?.name == "epoh 2" } )
    }

    @Test
    fun `in choose player state action are forbidden`() {
        val wonders = Wonders(testWonders, cards)
        wonders.game.state = GameState.CHOOSE_PLAYER
        wonders.game.board = Board(Card("card"))
        wonders.game.player1.wonders = listOf(WonderPair(false, Wonder("Wonder")))
        assertFails { wonders.takeAction(BuildWonder("card", "Wonder")) }
        assertFails { wonders.takeAction(TakeCard("card")) }
        assertFails { wonders.takeAction(ChooseScience(ScienceToken.THEOLOGY)) }
    }

    @Test
    fun `in choose player state only selecting player action allowed`() {
        val wonders = Wonders(testWonders, cards)
        wonders.game.state = GameState.CHOOSE_PLAYER
        wonders.takeAction(ChoosePlayer(1))
        assertEquals(1, wonders.game.currentPlayer)
    }

    @Test
    fun `after player choose game state return to regular`() {
        val wonders = Wonders(testWonders, cards)
        wonders.game.state = GameState.CHOOSE_PLAYER
        wonders.takeAction(ChoosePlayer(1))
        assertEquals(GameState.REGULAR, wonders.game.state)
    }

    private fun lastCardAge1(): Wonders {
        val wonders = Wonders(testWonders, cards)
        wonders.game.state = GameState.REGULAR
        wonders.game.board = Board(listOf(BoardNode(0, Card("last card"), position = BoardPosition(0, 0))))
        return wonders
    }
}