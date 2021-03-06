package com.moscichowski.wonders

import com.moscichowski.wonders.model.*
import kotlin.math.max

abstract class ActionPerformer(val wonders: Wonders) {
    fun resolveCommonFeatures(game: Game, features: List<CardFeature>, player: Player) {
        val addGoldFeature = features.find { it is AddGold }
        if (addGoldFeature != null && addGoldFeature is AddGold) {
            player.gold += addGoldFeature.gold
        }
        val militaryFeature = features.find { it is Military }
        if (militaryFeature != null && militaryFeature is Military) {
            val points = militaryFeature.points + additionalMilitaryPoints()
            game.military += if (game.currentPlayer == 0) {
                points
            } else {
                -points
            }
            val activatedThresholds = game.militaryThresholds
                    .filter {
                        (it.player == 0 && game.military >= it.position)
                                || (it.player == 1 && -game.military >= it.position)
                    }
            activatedThresholds.forEach { game.opponent.gold -= it.gold }
            game.militaryThresholds.removeAll(activatedThresholds)
        }

        val goldForColorFeature = features.find { it is GoldForColor }
        if (goldForColorFeature is GoldForColor) {
            player.gold += goldForColorFeature.colorValue * player.cards.count { it.color == goldForColorFeature.color }
        }

        val goldForWondersFeature = features.find { it is GoldForWonder }
        if (goldForWondersFeature is GoldForWonder) {
            player.gold += 2 * player.wonders.count { it.built }
        }

        val guild = features.find { it is Guild }
        if (guild is Guild) {
            player.gold += GuildFeatureResolver().pointsForGuild(guild, game)
        }
    }

    open fun additionalMilitaryPoints(): Int {
        return 0
    }

    fun Player.providedResources(): List<Resource> {
        val providedFromCards = listOf(cards.flatMap { it.features }.fold(Resource(), operation = { sum: Resource, element: CardFeature ->
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

        val wonderFeatures = wonders.filter { it.built }.flatMap { it.wonder.features }
        if (wonderFeatures.contains(ProvideSilverResource)) {
            toCombine.add(Resource(papyrus = 1, glass = 1))
        }
        if (wonderFeatures.contains(ProvideBrownResource)) {
            toCombine.add(Resource(wood = 1, clay = 1, stone = 1))
        }

        return combinePromos(providedFromCards, toCombine)
    }

    fun combinePromos(providedFromCards: List<Resource>, toCombine: MutableList<Resource>): List<Resource> {
        var result = providedFromCards
        toCombine.forEach {
            val newResult = mutableListOf<Resource>()
            newResult.addAll(result)
            result.forEach { resultResource ->
                newResult.addAll(resultResource.combine(it))
            }
            result = newResult
        }
        return result
    }

    fun List<CardFeature>.cost(type: WarehouseType, opponentResource: Resource): Int {
        return if (find { it is Warehouse && it.type == type } != null) {
            1
        } else {
            type.cost(opponentResource) + 2
        }
    }

    fun List<CardFeature>.glassCost(opponentResource: Resource): Int {
        return if (find { it is Customs } != null) {
            1
        } else {
            opponentResource.glass + 2
        }
    }

    fun List<CardFeature>.papyrusCost(opponentResource: Resource): Int {
        return if (find { it is Customs } != null) {
            1
        } else {
            opponentResource.papyrus + 2
        }
    }

    fun discountBy2Combine(): MutableList<Resource> {
        val toCombine = mutableListOf<Resource>()
        toCombine.add(Resource(1, 1, 1, 1, 1))
        toCombine.add(Resource(1, 1, 1, 1, 1))
        return toCombine
    }

//    fun boardCheck(game: Game, card: Card): Pair<Player, BoardNode> {
//        val player = if (game.currentPlayer == 0) game.player1 else game.player2
//        val wantedNode = game.board.cards.find { node ->
//            node.card == card
//        } ?: throw Error()
//        if (!wantedNode.descendants.isEmpty()) {
//            throw Error()
//        }
//        return Pair(player, wantedNode)
//    }

    fun required2(game: Game, player: Player, cost: Resource): Int {
        val opponent = opponent(game)
        val opponentResource = opponent.resources()
        var providedResourcesPossibilities = player.providedResources()

        if (hasPromo()) {
            providedResourcesPossibilities = appendConstructionIfExist(providedResourcesPossibilities)
        }

        return resourceCost(player, opponentResource, providedResourcesPossibilities, cost)
    }

    fun opponent(game: Game): Player {
        return if (game.currentPlayer == 1) game.player1 else game.player2
    }

    open fun hasPromo(): Boolean = false

    private fun appendConstructionIfExist(providedResourcesPossibilities: List<Resource>): List<Resource> {
        var providedResourcesPossibilities1 = providedResourcesPossibilities
        val toCombine = discountBy2Combine()
        providedResourcesPossibilities1 = combinePromos(providedResourcesPossibilities1, toCombine)
        return providedResourcesPossibilities1
    }

    fun resourceCost(player: Player, opponentResource: Resource, providedResourcesPossibilities: List<Resource>, cost: Resource): Int {
        val playerFeatures = player.cards.flatMap { it.features }
        val woodCost = playerFeatures.cost(WarehouseType.WOOD, opponentResource)
        val clayCost = playerFeatures.cost(WarehouseType.CLAY, opponentResource)
        val stoneCost = playerFeatures.cost(WarehouseType.STONE, opponentResource)
        val papyrusCost = playerFeatures.papyrusCost(opponentResource)
        val glassCost = playerFeatures.glassCost(opponentResource)
        val requiredGold = providedResourcesPossibilities.asSequence().map { providedResources ->
            max(0, cost.clay - providedResources.clay) * clayCost +
                    max(0, cost.wood - providedResources.wood) * woodCost +
                    max(0, cost.stone - providedResources.stone) * stoneCost +
                    max(0, cost.papyrus - providedResources.papyrus) * papyrusCost +
                    max(0, cost.glass - providedResources.glass) * glassCost +
                    cost.gold
        }.min() ?: throw Error()
        return requiredGold
    }

    fun doesOpponentHaveEconomy(game: Game): Boolean {
        val opponentIndex = if (game.currentPlayer == 0) { 1 } else { 0 }
        return game.scienceTokens.find { it.first == opponentIndex && it.second == ScienceToken.ECONOMY } != null
    }

    internal val Game.opponent: Player
        get() = if (currentPlayer == 1) {
            player1
        } else {
            player2
        }

    internal val Game.player: Player
        get() = if (currentPlayer == 0) {
            player1
        } else {
            player2
        }

    internal fun removeCardFromBoard(card: String) {
        val board = wonders.game.board ?: throw Error()
        val elements = board.elements
        val removingNode = elements.find { it.card?.name == card }
        elements.remove(removingNode)
        elements.forEach { node: BoardNode ->
            node.descendants.remove(removingNode)
            if (node.descendants.count() == 0 && node.card == null) {
                node.card = wonders.getCard()
            }
        }
    }

    private val GoldForColor.colorValue: Int get() {
        return when(color) {
            CardColor.RED -> 1
            CardColor.BROWN -> 2
            CardColor.SILVER -> 3
            CardColor.GREEN -> 1
            CardColor.BLUE -> 1
            CardColor.YELLOW -> 1
        }
    }
}