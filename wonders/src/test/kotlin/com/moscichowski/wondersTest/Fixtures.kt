package com.moscichowski.wondersTest

import com.moscichowski.wonders.*
import com.moscichowski.wonders.BoardNode
import com.moscichowski.wonders.model.Card
import kotlin.test.assertTrue
import kotlin.test.fail

fun Game(): Game {
    return Game(testWonders, cards)
}

fun Game(player1: Player, player2: Player, board: Board, currentPlayer: Int = 0): Game {
    val game = Game(testWonders, cards, board, player1 = player1, player2 = player2, currentPlayer = currentPlayer, state = GameState.REGULAR)
    game.board = board
    return game
}

fun BoardNode(innerCard: Card, descendants: MutableList<BoardNode> = mutableListOf(), hidden: Boolean = false) = BoardNode(0, innerCard, descendants, hidden)

val testWonders = (0 until 8).map { Wonder("Test $it") }
val cards = listOf((0 until 20).map { Card("some") }, (0 until 20).map { Card("some") }, (0 until 20).map { Card("some") })

fun assertFails(error: (Error) -> Boolean, block: () -> Unit) {
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