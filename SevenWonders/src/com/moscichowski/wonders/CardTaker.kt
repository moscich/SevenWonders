package com.moscichowski.wonders

import kotlin.math.max

class CardTaker : ActionPerformer() {
    fun takeCard(game: Game, action: TakeCard) {
        val (player, wantedNode) = boardCheck(game, action.card)

        val opponent = if (game.currentPlayer == 1) game.player1 else game.player2
        val opponentResource = opponent.resources()
        var providedResourcesPossibilities = player.providedResources()

        providedResourcesPossibilities = appendConstructionIfExist(action, game, providedResourcesPossibilities)

        val playerFeatures = player.cards.flatMap { it.features }
        val woodCost = playerFeatures.cost(WarehouseType.WOOD, opponentResource)
        val clayCost = playerFeatures.cost(WarehouseType.CLAY, opponentResource)
        val stoneCost = playerFeatures.cost(WarehouseType.STONE, opponentResource)
        var requiredGold = providedResourcesPossibilities.asSequence().map { providedResources ->
            max(0, action.card.cost.clay - providedResources.clay) * clayCost +
                    max(0, action.card.cost.wood - providedResources.wood) * woodCost +
                    max(0, action.card.cost.stone - providedResources.stone) * stoneCost +
                    max(0, action.card.cost.papyrus - providedResources.papyrus) * (opponentResource.papyrus + 2) +
                    max(0, action.card.cost.glass - providedResources.glass) * (opponentResource.glass + 2) +
                    action.card.cost.gold
        }.min() ?: throw Error()

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