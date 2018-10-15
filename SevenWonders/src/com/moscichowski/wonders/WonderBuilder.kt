package com.moscichowski.wonders

import kotlin.math.max


class WonderBuilder : ActionPerformer() {
    private lateinit var game: Game

    override fun hasPromo(): Boolean {
        return game.scienceTokens.find { it.first == game.currentPlayer && it.second == ScienceToken.ARCHITECTURE } != null
    }

    fun buildWonder(action: BuildWonder, game: Game) {
        this.game = game
        val (player, wantedNode) = boardCheck(game, action.card)

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

        game.board.cards.remove(wantedNode)
        game.board.cards.forEach { node: BoardNode ->
            node.descendants.remove(wantedNode.card)
        }
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

    private fun appendConstructionIfExist(game: Game, providedResourcesPossibilities: List<Resource>): List<Resource> {
        var providedResourcesPossibilities1 = providedResourcesPossibilities
        if (hasConstructionFeature(game)) {
            val toCombine = discountBy2Combine()
            providedResourcesPossibilities1 = combinePromos(providedResourcesPossibilities1, toCombine)
        }
        return providedResourcesPossibilities1
    }

    private fun hasConstructionFeature(game: Game) =
            game.scienceTokens.find { it.first == game.currentPlayer && it.second == ScienceToken.ARCHITECTURE } != null
}