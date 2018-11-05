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
        assertFails({ it is WrongNumberOfCards }) { Wonders(wonders, cards1) }
        assertFails({ it is WrongNumberOfCards }) { Wonders(wonders, cards2) }
    }

    @Test
    fun `Game inits with 3x20 cards`() {
        val wonderList = (0 until 8).map { Wonder("Test") }
        val cards = listOf((0 until 20).map { Card("card $it") }, (0 until 20).map { Card("some") }, (0 until 20).map { Card("some") })
        val wonders = Wonders(wonderList, cards)
        val game = wonders.game
        game.state = GameState.REGULAR

        val cases = listOf(
                Triple("card 0", 2, 3),
                Triple("card 1", 3, 4),
                Triple(null, 5, 6),
                Triple(null, 6, 7),
                Triple(null, 7, 8),
                Triple("card 2", 9, 10),
                Triple("card 3", 10, 11),
                Triple("card 4", 11, 12),
                Triple("card 5", 12, 13),
                Triple(null, 14, 15),
                Triple(null, 15, 16),
                Triple(null, 16, 17),
                Triple(null, 17, 18),
                Triple(null, 18, 19)
                )

        // test cases with descendants
        for (index in 0 until cases.count()) {
            val case = cases[index]
            val boardNode = game.board.elements[index]
            assertEquals(case.first, boardNode.card?.name)
            assertEquals(game.board.elements[case.second], boardNode.descendants[0])
            assertEquals(game.board.elements[case.third], boardNode.descendants[1])
        }

        // test cases without descendants
        for (index in cases.count()..19) {
            val expectedCardName = "card ${index - 8}"
            val boardNode = game.board.elements[index]
            assertEquals(expectedCardName, boardNode.card?.name)
            assertEquals(0, boardNode.descendants.count())
        }
    }
}