package com.moscichowski.wonders
import com.moscichowski.wonders.model.*

class ScienceChooser: ActionPerformer() {

    fun chooseScience(game: Game, action: ChooseScience) {
        val index = game.scienceTokens.filter { it.first == null }.indexOfFirst { it.second == action.token }
        if (index == -1) { throw Error() }
        when (action.token) {
            ScienceToken.AGRICULTURE -> game.player.gold += 6
            ScienceToken.CITY_PLANNING -> game.player.gold += 6
            else -> {}
        }
        game.scienceTokens[index] = Pair(game.currentPlayer, action.token)
    }
}