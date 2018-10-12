package com.moscichowski.wonders

import kotlin.math.max


class WonderBuilder: ActionPerformer() {
    fun buildWonder(action: BuildWonder, game: Game) {
        val (player, wantedNode) = boardCheck(game, action.card)

        val opponent = if (game.currentPlayer == 1) game.player1 else game.player2
        val opponentResource = opponent.resources()
        var providedResourcesPossibilities = player.providedResources()

        providedResourcesPossibilities = appendConstructionIfExist(game, providedResourcesPossibilities)

        val playerFeatures = player.cards.flatMap { it.features }
        val woodCost = playerFeatures.cost(WarehouseType.WOOD, opponentResource)
        val clayCost = playerFeatures.cost(WarehouseType.CLAY, opponentResource)
        val stoneCost = playerFeatures.cost(WarehouseType.STONE, opponentResource)
        val requiredGold = providedResourcesPossibilities.asSequence().map { providedResources ->
            max(0, action.wonder.cost.clay - providedResources.clay) * clayCost +
                    max(0, action.wonder.cost.wood - providedResources.wood) * woodCost +
                    max(0, action.wonder.cost.stone - providedResources.stone) * stoneCost +
                    max(0, action.wonder.cost.papyrus - providedResources.papyrus) * (opponentResource.papyrus + 2) +
                    max(0, action.wonder.cost.glass - providedResources.glass) * (opponentResource.glass + 2) +
                    action.wonder.cost.gold
        }.min() ?: throw Error()

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

        if (action.wonder.features.find { it is RemoveGold } != null) {
            opponent.gold -= 3
            opponent.gold = max(0, opponent.gold)
        }

        resolveCommonFeatures(game, action.wonder.features, player)

        if (action.wonder.features.find { it is ExtraTurn } == null) {
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