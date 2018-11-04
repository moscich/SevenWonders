package com.moscichowski.wondersTest

import com.moscichowski.wonders.*
import com.moscichowski.wonders.WrongNumberOfCards
import com.moscichowski.wonders.model.Wonder
import org.junit.Test
import kotlin.test.assertEquals

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
        val cards = listOf((0 until 20).map { Card("card $it") }, (0 until 20).map { Card("some") }, (0 until 20).map { Card("some") })
        val game = Game(wonders, cards)
        game.state = GameState.REGULAR

        val cases = listOf(
                Triple("card 0", 2, 3),
                Triple("card 1", 3, 4),
                Triple(null, 5, 6),
                Triple(null, 6, 7),
                Triple(null, 7, 8),
                Triple("card 5", 9, 10),
                Triple("card 6", 10, 11),
                Triple("card 7", 11, 12),
                Triple("card 8", 12, 13),
                Triple(null, 14, 15),
                Triple(null, 15, 16),
                Triple(null, 16, 17),
                Triple(null, 17, 18),
                Triple(null, 18, 19)
                )

        for (index in 0 until cases.count()) {
            val case = cases[index]
            val boardNode = game.board.cards[index]
            assertEquals(case.first, boardNode.card?.name)
            assertEquals(game.board.cards[case.second], boardNode.descendants[0])
            assertEquals(game.board.cards[case.third], boardNode.descendants[1])
        }

        for (index in cases.count()..19) {
            val boardNode = game.board.cards[index]
            assertEquals("card $index", boardNode.card?.name)
            assertEquals(0, boardNode.descendants.count())
        }
    }
}