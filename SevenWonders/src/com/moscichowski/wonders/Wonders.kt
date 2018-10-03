package com.moscichowski.wonders

import kotlin.math.max

class Wonders {
    val gameState: Game

    constructor(state: Game) {
        this.gameState = state
    }

    fun takeAction(action: Action) {
        when (action) {
            is TakeCard -> {
                val wantedNode = gameState.board.cards.find { node ->
                    node.card == action.card
                } ?: throw Error()
                if (!wantedNode.descendants.isEmpty()) {
                    throw Error()
                }

                val player = if (gameState.currentPlayer == 0) gameState.player1 else gameState.player2
                val opponent = if (gameState.currentPlayer == 1) gameState.player1 else gameState.player2

                val opponentResource = opponent.resources()

                val providedResourcesPossibilities = player.providedResources()

                val playerFeatures = player.cards.flatMap { it.features }
                val woodCost = if (playerFeatures.find { it is WoodWarehouse } != null) {
                    1
                } else {
                    opponentResource.wood + 2
                }
                val clayCost = if (playerFeatures.find { it is ClayWarehouse } != null) {
                    1
                } else {
                    opponentResource.clay + 2
                }
                val stoneCost = if (playerFeatures.find { it is StoneWarehouse } != null) {
                    1
                } else {
                    opponentResource.stone + 2
                }

                val requiredGold = providedResourcesPossibilities.asSequence().map { providedResources ->
                    max(0, action.card.cost.clay - providedResources.clay) * clayCost +
                            max(0, action.card.cost.wood - providedResources.wood) * woodCost +
                            max(0, action.card.cost.stone - providedResources.stone) * stoneCost +
                            max(0, action.card.cost.papyrus - providedResources.papyrus) * (opponentResource.papyrus + 2) +
                            max(0, action.card.cost.glass - providedResources.glass) * (opponentResource.glass + 2) +
                            action.card.cost.gold
                }.min() ?: throw Error()

                if (player.gold < requiredGold) {
                    throw Error()
                }
                gameState.board.cards.remove(wantedNode)
                gameState.board.cards.forEach { node: BoardNode ->
                    node.descendants.remove(wantedNode.card)
                }
                player.gold -= requiredGold
                gameState.currentPlayer = (gameState.currentPlayer + 1) % 2
                player.cards.add(action.card)
            }
            is BuildWonder -> {
                val wantedNode = gameState.board.cards.find { node ->
                    node.card == action.card
                } ?: throw WonderBuildFailed()
                if (!wantedNode.descendants.isEmpty()) {
                    throw WonderBuildFailed()
                }

                val player = if (gameState.currentPlayer == 0) gameState.player1 else gameState.player2
                val opponent = if (gameState.currentPlayer == 1) gameState.player1 else gameState.player2

                val opponentResource = opponent.resources()

                val providedResources = player.providedResources().first()

                val playerFeatures = player.cards.flatMap { it.features }
                val woodCost = if (playerFeatures.find { it is WoodWarehouse } != null) {
                    1
                } else {
                    opponentResource.wood + 2
                }
                val clayCost = if (playerFeatures.find { it is ClayWarehouse } != null) {
                    1
                } else {
                    opponentResource.clay + 2
                }
                val stoneCost = if (playerFeatures.find { it is StoneWarehouse } != null) {
                    1
                } else {
                    opponentResource.stone + 2
                }

                val requiredGold = max(0, action.wonder.cost.clay - providedResources.clay) * clayCost +
                        max(0, action.wonder.cost.wood - providedResources.wood) * woodCost +
                        max(0, action.wonder.cost.stone - providedResources.stone) * stoneCost +
                        max(0, action.wonder.cost.papyrus - providedResources.papyrus) * (opponentResource.papyrus + 2) +
                        max(0, action.wonder.cost.glass - providedResources.glass) * (opponentResource.glass + 2) +
                        action.wonder.cost.gold

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

                gameState.board.cards.remove(wantedNode)
                gameState.board.cards.forEach { node: BoardNode ->
                    node.descendants.remove(wantedNode.card)
                }
                player.gold -= requiredGold

                if (action.wonder.features.find { it is ExtraTurn } == null) {
                    gameState.currentPlayer = (gameState.currentPlayer + 1) % 2
                }

                player.wonders.indexOfFirst { it.second == action.wonder }
                player.wonders = player.wonders.map {
                    if (it.second == action.wonder) {
                        return@map Pair(true, it.second)
                    } else {
                        return@map it
                    }}
            }
        }

    }

    private fun Player.providedResources(): List<Resource> {
        val providedFromCards = listOf(cards.flatMap { it.features }.fold(Resource(), operation = { sum, element ->
            return@fold if (element is ProvideResource) {
                element.resource + sum
            } else {
                sum
            }
        }))

        val wonderFeatures = wonders.filter { it.first }.flatMap { it.second.features }
        val possibleResources = wonderFeatures.filter { it is ProvideSilverResource }.map { listOf(Resource(papyrus = 1), Resource(glass = 1)) }.flatMap { it }

        val result = mutableListOf<Resource>()
        result.addAll(providedFromCards)
        for (i in 0 until providedFromCards.count()) {
            val fromCards = providedFromCards[i]
            for (j in 0 until possibleResources.count()) {
                val possible = possibleResources[j]
                result.add(fromCards + possible)
            }
        }

        return result
    }
}

sealed class Action
data class TakeCard(val card: Card) : Action()
data class BuildWonder(val card: Card, val wonder: Wonder, var param: Any? = null) : Action()

class WonderBuildFailed : Error() {
    val something: String = "Test"
    override val message: String?
        get() = "Wrong neighbourhood"
}

fun main(args: Array<String>) {
}