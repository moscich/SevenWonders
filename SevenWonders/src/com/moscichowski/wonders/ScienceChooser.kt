package com.moscichowski.wonders

class ScienceChooser: ActionPerformer() {
    fun chooseScience(game: Game, action: ChooseScience) {
        val index = game.scienceTokens.indexOfFirst { it.second == action.token }
        if (index == -1) { throw Error() }
        game.scienceTokens[index] = Pair(game.currentPlayer, action.token)
    }
}