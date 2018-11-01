package com.moscichowski.wondersTest

import com.moscichowski.wonders.*

fun Game(): Game {
    return Game(testWonders, listOf())
}

fun Game(player1: Player, player2: Player, board: Board, currentPlayer: Int = 0): Game {
    return Game(testWonders, listOf(), board, player1 = player1, player2 = player2, currentPlayer = currentPlayer, state = GameState.REGULAR)
}

val testWonders = (0 until 8).map { Wonder("Test $it") }