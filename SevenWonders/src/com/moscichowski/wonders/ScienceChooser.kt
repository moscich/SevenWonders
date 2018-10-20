package com.moscichowski.wonders

class ScienceChooser: ActionPerformer() {
    override fun hasPromo(): Boolean {
        return false
    }

    fun chooseScience(game: Game, action: ChooseScience) {
        val index = game.scienceTokens.indexOfFirst { it.second == action.token }
        if (index == -1) { throw Error() }
        when (action.token) {
            ScienceToken.AGRICULTURE -> game.player.gold += 6
            else -> {}
        }
        game.scienceTokens[index] = Pair(game.currentPlayer, action.token)
    }
}