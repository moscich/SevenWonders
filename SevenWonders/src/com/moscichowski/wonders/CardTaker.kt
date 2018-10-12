package com.moscichowski.wonders

import kotlin.math.max

class CardTaker : ActionPerformer() {
    fun takeCard(game: Game, action: TakeCard) {
        val (player, wantedNode) = boardCheck(game, action.card)

        val opponent = if (game.currentPlayer == 1) game.player1 else game.player2
        val opponentResource = opponent.resources()
        var providedResourcesPossibilities = player.providedResources()

        providedResourcesPossibilities = appendConstructionIfExist(action, game, providedResourcesPossibilities)

        var requiredGold = resourceCost(player, opponentResource, providedResourcesPossibilities, action.card.cost)

        if (player.hasFreeSymbol(action.card.freeSymbol)) {
            requiredGold = 0
        }

        if (player.gold < requiredGold) {
            throw Error()
        }

        game.board.cards.remove(wantedNode)
        game.board.cards.forEach { node: BoardNode ->
            node.descendants.remove(wantedNode.card)
        }
        player.gold -= requiredGold

        resolveCommonFeatures(game, action.card.features, player)

        if (player.hasScienceSymbolFromFeatures(action.card.features)) {
            game.state = GameState.CHOOSE_SCIENCE
        }

        player.cards.add(action.card)

        game.currentPlayer = (game.currentPlayer + 1) % 2
    }

    private fun appendConstructionIfExist(action: TakeCard, game: Game, providedResourcesPossibilities: List<Resource>): List<Resource> {
        var providedResourcesPossibilities1 = providedResourcesPossibilities
        if (action.card.color == CardColor.BLUE && hasConstructionFeature(game)) {
            val toCombine = discountBy2Combine()
            providedResourcesPossibilities1 = combinePromos(providedResourcesPossibilities1, toCombine)
        }
        return providedResourcesPossibilities1
    }

    private fun hasConstructionFeature(game: Game) =
            game.scienceTokens.find { it.first == game.currentPlayer && it.second == ScienceToken.CONSTRUCTION } != null
}