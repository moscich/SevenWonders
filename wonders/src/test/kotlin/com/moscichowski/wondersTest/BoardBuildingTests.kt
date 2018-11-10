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
        assertFails({ it is WrongNumberOfCards }) { WondersBuilder().setupWonders(wonders, cards1) }
        assertFails({ it is WrongNumberOfCards }) { WondersBuilder().setupWonders(wonders, cards2) }
    }

    @Test
    fun `Game inits with 3x20 cards`() {
        val cards = listOf((0 until 20).map { Card("card $it") }, (0 until 20).map { Card("card2 $it") }, (0 until 20).map { Card("some") })
        val wonders = Wonders(testWonders, cards)
        val board = wonders.buildBoard(0)

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
            val boardNode = board.elements[index]
            assertEquals(case.first, boardNode.card?.name)
            assertEquals(board.elements[case.second], boardNode.descendants[0])
            assertEquals(board.elements[case.third], boardNode.descendants[1])
        }

        // test cases without descendants
        for (index in cases.count()..19) {
            val expectedCardName = "card ${index - 8}"
            val boardNode = board.elements[index]
            assertEquals(expectedCardName, boardNode.card?.name)
            assertEquals(0, boardNode.descendants.count())
        }
    }

    @Test
    fun `Second Epoh`() {
        val cards = listOf((0 until 20).map { Card("card $it") }, (0 until 20).map { Card("card2 $it") }, (0 until 20).map { Card("some") })
        val wonders = Wonders(testWonders, cards)
        val board = wonders.buildBoard(1)

        val cases = listOf(
                Triple("card2 0", 6, null),
                Triple("card2 1", 6, 7),
                Triple("card2 2", 7, 8),
                Triple("card2 3", 8, 9),
                Triple("card2 4", 9, 10),
                Triple("card2 5", 10, null),
                Triple(null, 11, null),
                Triple(null, 11, 12),
                Triple(null, 12, 13),
                Triple(null, 13, 14),
                Triple(null, 14, null),
                Triple("card2 6", 15, null),
                Triple("card2 7", 15, 16),
                Triple("card2 8", 16, 17),
                Triple("card2 9", 17, null),
                Triple(null, 18, null),
                Triple(null, 18, 19),
                Triple(null, 19, null)
        )

        // test cases with descendants
        for (index in 10 until cases.count()) {
            val case = cases[index]
            val boardNode = board.elements[index]
            assertEquals(case.first, boardNode.card?.name)
            val descendant1 = case.second
            val descendant2 = case.third
            assertEquals(board.elements[descendant1], boardNode.descendants[0])
            if (descendant2 != null) {
                assertEquals(board.elements[descendant2], boardNode.descendants[1])
            }
        }

        // test cases without descendants
        for (index in cases.count()..19) {
            val expectedCardName = "card2 ${index - 8}"
            val boardNode = board.elements[index]
            assertEquals(expectedCardName, boardNode.card?.name)
            assertEquals(0, boardNode.descendants.count())
        }
    }
}