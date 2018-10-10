package com.moscichowski.wonders

import kotlin.math.max


class WonderBuilder: ActionPerformer() {
    fun buildWonder(action: BuildWonder, game: Game) {
        val wantedNode = game.board.cards.find { node ->
            node.card == action.card
        } ?: throw WonderBuildFailed()
        if (!wantedNode.descendants.isEmpty()) {
            throw WonderBuildFailed()
        }

        val player1 = if (game.currentPlayer == 0) game.player1 else game.player2
        if (!wantedNode.descendants.isEmpty()) {
            throw Error()
        }
        val opponent = if (game.currentPlayer == 1) game.player1 else game.player2
        val opponentResource = opponent.resources()
        val providedResourcesPossibilities = player1.providedResources()
        val playerFeatures = player1.cards.flatMap { it.features }
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

        if (player1.gold < requiredGold) {
            throw WonderBuildFailed()
        }

        val hasWonderAvailable = !player1.wonders.any {
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
        player1.gold -= requiredGold

        if (action.wonder.features.find { it is RemoveGold } != null) {
            opponent.gold -= 3
            opponent.gold = max(0, opponent.gold)
        }

        resolveCommonFeatures(game, action.wonder.features, player1)

        if (action.wonder.features.find { it is ExtraTurn } == null) {
            game.currentPlayer = (game.currentPlayer + 1) % 2
        }

        player1.wonders.indexOfFirst { it.second == action.wonder }
        player1.wonders = player1.wonders.map {
            if (it.second == action.wonder) {
                return@map Pair(true, it.second)
            } else {
                return@map it
            }
        }
    }
}