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
        val card = game.board?.requestedCard(action.card) ?: throw Error()

        val wonder = player.wonders.find {
            it.wonder.name == action.wonder && !it.built
        }?.wonder ?: throw WonderBuildFailed()

        if (are7WondersBuilt(game)) { throw Error() }

        val opponent = if (game.currentPlayer == 1) game.player1 else game.player2

        val requiredGold = required2(game, player, wonder.cost)

        if (player.gold < requiredGold) {
            throw WonderBuildFailed()
        }

        if (wonder.features.find { it is DestroyBrownCard } != null) {
            val cardToDestroy = action.param
            if (cardToDestroy is Card) {
                if (cardToDestroy.color != CardColor.BROWN) {
                    throw WonderBuildFailed()
                }
                opponent.cards.remove(cardToDestroy)
            }
        }

        if (wonder.features.find { it is DestroySilverCard } != null) {
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
            game.opponent.gold += (requiredGold - wonder.cost.gold)
        }

        if (wonder.features.find { it is RemoveGold } != null) {
            opponent.gold -= 3
            opponent.gold = max(0, opponent.gold)
        }

        resolveCommonFeatures(game, wonder.features, player)

        if (wonder.features.find { it is ExtraTurn } == null &&
                game.scienceTokens.find { it.first == game.currentPlayer && it.second == ScienceToken.THEOLOGY } == null ) {
            game.currentPlayer = (game.currentPlayer + 1) % 2
        }

        player.wonders = player.wonders.map {
            if (it.wonder.name == action.wonder) {
                return@map WonderPair(true, it.wonder)
            } else {
                return@map it
            }
        }
    }

    private fun are7WondersBuilt(game: Game) =
            game.player1.wonders.count { it.built } + game.player2.wonders.count { it.built } == 7
}