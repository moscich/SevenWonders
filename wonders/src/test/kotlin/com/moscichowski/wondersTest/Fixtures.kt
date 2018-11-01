package com.moscichowski.wondersTest

import com.moscichowski.wonders.*

class EmptyBoardBuilder(val board: Board): BoardBuilder {
    override fun build1Board(): Board { return board }
    override fun build2Board(): Board { return board }
    override fun build3Board(): Board { return board }
}

fun Game(): Game {
    val testWonders = (0 until 8).map { Wonder("Test $it") }
    return Game(testWonders)
}

fun Game(player1: Player, player2: Player, board: Board, currentPlayer: Int = 0): Game {
    val testWonders = (0 until 8).map { Wonder("Test") }
    return Game(testWonders, EmptyBoardBuilder(board), player1 = player1, player2 = player2, currentPlayer = currentPlayer, state = GameState.REGULAR)
}