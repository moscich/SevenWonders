package com.moscichowski.wonders

import kotlin.math.max
import com.moscichowski.wonders.model.*


class WonderBuilder(wonders: Wonders) : ActionPerformer(wonders) {

    override fun hasPromo(): Boolean {
        return wonders.game.scienceTokens.find { it.first == wonders.game.currentPlayer && it.second == ScienceToken.ARCHITECTURE } != null
    }

    fun buildWonder(action: BuildWonder) {
        val game = wonders.game
        val player = if (game.currentPlayer == 0) game.player1 else game.player2
        val card = game.board.requestedCard(action.card.name) ?: throw Error()

        if (are7WondersBuilt(game)) { throw Error() }

        val opponent = if (game.currentPlayer == 1) game.player1 else game.player2

        val requiredGold = required2(game, player, action.wonder.cost)

        if (player.gold < requiredGold) {
            throw WonderBuildFailed()
        }

        val hasWonderAvailable = !player.wonders.any {
            it.second == action.wonder && !it.first
        }
        if (hasWonderAvailable) {
            throw WonderBuildFailed()
        }

        if (action.wonder.features.find { it is DestroyBrownCard } != null) {
            val cardToDestroy = action.param
            if (cardToDestroy is Card) {
                if (cardToDestroy.color != CardColor.BROWN) {
                    throw WonderBuildFailed()
                }
                opponent.cards.remove(cardToDestroy)
            }
        }

        if (action.wonder.features.find { it is DestroySilverCard } != null) {
            val cardToDestroy = action.param
            if (cardToDestroy is Card) {
                if (cardToDestroy.color != CardColor.SILVER) {
                    throw WonderBuildFailed()
                }
                opponent.cards.remove(cardToDestroy)
            }
        }

        removeCardFromBoard(card.name)
        player.gold -= requiredGold

        if (doesOpponentHaveEconomy(game)) {
            game.opponent.gold += (requiredGold - action.wonder.cost.gold)
        }

        if (action.wonder.features.find { it is RemoveGold } != null) {
            opponent.gold -= 3
            opponent.gold = max(0, opponent.gold)
        }

        resolveCommonFeatures(game, action.wonder.features, player)

        if (action.wonder.features.find { it is ExtraTurn } == null &&
                game.scienceTokens.find { it.first == game.currentPlayer && it.second == ScienceToken.THEOLOGY } == null ) {
            game.currentPlayer = (game.currentPlayer + 1) % 2
        }

        player.wonders.indexOfFirst { it.second == action.wonder }
        player.wonders = player.wonders.map {
            if (it.second == action.wonder) {
                return@map Pair(true, it.second)
            } else {
                return@map it
            }
        }
    }

    private fun are7WondersBuilt(game: Game) =
            game.player1.wonders.count { it.first } + game.player2.wonders.count { it.first } == 7
}