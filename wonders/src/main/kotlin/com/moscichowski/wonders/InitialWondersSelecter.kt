package com.moscichowski.wonders

class InitialWondersSelecter(wonders: Wonders) : ActionPerformer(wonders) {
    fun selectWonder(chooseWonder: ChooseWonder) {
        val game = wonders.game
        val wonder = game.wonders.find { it.name == chooseWonder.wonderName } ?: throw Error()
        game.selectWonder(wonder)
        val toMutableList = game.player.wonders.toMutableList()
        toMutableList.add(Pair(false, wonder))
        game.player.wonders = toMutableList
        if (game.wonders.count() != 2) {
            game.currentPlayer = (game.currentPlayer + 1) % 2
        }
        if (game.wonders.count() == 0) {
            game.state = GameState.REGULAR
            game.board = wonders.buildBoard()
        }
    }
}