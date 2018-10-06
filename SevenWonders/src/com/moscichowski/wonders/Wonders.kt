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

                val player = if (gameState.currentPlayer == 0) gameState.player1 else gameState.player2
                val wantedNode = gameState.board.cards.find { node ->
                    node.card == action.card
                } ?: throw Error()

                val requiredGold = checkHowMuch(action.card, action.card.cost)

                gameState.board.cards.remove(wantedNode)
                gameState.board.cards.forEach { node: BoardNode ->
                    node.descendants.remove(wantedNode.card)
                }
                player.gold -= requiredGold

                resolveCommonFeatures(action.card.features, player)

                gameState.currentPlayer = (gameState.currentPlayer + 1) % 2
                player.cards.add(action.card)
            }
            is BuildWonder -> {
                val player = if (gameState.currentPlayer == 0) gameState.player1 else gameState.player2
                val wantedNode = gameState.board.cards.find { node ->
                    node.card == action.card
                } ?: throw WonderBuildFailed()
                if (!wantedNode.descendants.isEmpty()) {
                    throw WonderBuildFailed()
                }

                val requiredGold = checkHowMuch(action.card, action.wonder.cost)

                val opponent = if (gameState.currentPlayer == 1) gameState.player1 else gameState.player2

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

                if (action.wonder.features.find { it is RemoveGold } != null) {
                    opponent.gold -= 3
                    opponent.gold = max(0, opponent.gold)
                }

                resolveCommonFeatures(action.wonder.features, player)

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

    private fun resolveCommonFeatures(features: List<CardFeature>, player: Player) {
        val addGoldFeature = features.find { it is AddGold }
        if (addGoldFeature != null && addGoldFeature is AddGold) {
            player.gold += addGoldFeature.gold
        }
    }

    private fun checkHowMuch(card: Card, cost: Resource): Int {
        val player = if (gameState.currentPlayer == 0) gameState.player1 else gameState.player2

        val wantedNode = gameState.board.cards.find { node ->
            node.card == card
        } ?: throw Error()

        if (!wantedNode.descendants.isEmpty()) {
            throw Error()
        }

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
            max(0, cost.clay - providedResources.clay) * clayCost +
                    max(0, cost.wood - providedResources.wood) * woodCost +
                    max(0, cost.stone - providedResources.stone) * stoneCost +
                    max(0, cost.papyrus - providedResources.papyrus) * (opponentResource.papyrus + 2) +
                    max(0, cost.glass - providedResources.glass) * (opponentResource.glass + 2) +
                    cost.gold
        }.min() ?: throw Error()

        if (player.gold < requiredGold) {
            throw Error()
        }

        return requiredGold
    }

    private fun Player.providedResources(): List<Resource> {
        val providedFromCards = listOf(cards.flatMap { it.features }.fold(Resource(), operation = { sum, element ->
            return@fold if (element is ProvideResource) {
                element.resource + sum
            } else {
                sum
            }
        }))

        val toCombine = mutableListOf<Resource>()
        val cardFeatures = cards.flatMap { it.features }
        if (cardFeatures.contains(ProvideSilverResource)) {
            toCombine.add(Resource(papyrus = 1, glass = 1))

        }
        if (cardFeatures.contains(ProvideSilverResource)) {
            toCombine.add(Resource(wood = 1, clay = 1, stone = 1))
        }

        val wonderFeatures = wonders.filter { it.first }.flatMap { it.second.features }
        if (wonderFeatures.contains(ProvideSilverResource)) {
            toCombine.add(Resource(papyrus = 1, glass = 1))
        }
        if (wonderFeatures.contains(ProvideBrownResource)) {
            toCombine.add(Resource(wood = 1, clay = 1, stone = 1))
        }

        var result = providedFromCards
        toCombine.forEach {
            val newResult = mutableListOf<Resource>()
            newResult.addAll(result)
            result.forEach { resultResource ->
                newResult.addAll(resultResource.combine(it))
            }
            result = newResult//result.first().combine(it)
        }
//        if (toCombine.count() > 0) {
//            result = result.first().combine(toCombine.first())
//        }

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

class TakeCardFailed : Error() {
    val something: String = "Test"
    override val message: String?
        get() = "Wrong neighbourhood"
}

fun main(args: Array<String>) {
}