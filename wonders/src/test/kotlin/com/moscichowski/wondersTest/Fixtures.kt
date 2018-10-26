package com.moscichowski.wondersTest

import com.moscichowski.wonders.*

fun Game(board: Board): Game {
    val testWonders = (0 until 8).map { Wonder("Test $it") }
    return Game(board, testWonders)
}

fun Game(player1: Player, player2: Player, board: Board, currentPlayer: Int = 0): Game {
    val testWonders = (0 until 8).map { Wonder("Test") }
    return Game(board, testWonders, player1 = player1, player2 = player2, currentPlayer = currentPlayer, state = GameState.REGULAR)
}