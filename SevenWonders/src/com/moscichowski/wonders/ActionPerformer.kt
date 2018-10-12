package com.moscichowski.wonders

open class ActionPerformer {
    fun resolveCommonFeatures(game: Game, features: List<CardFeature>, player: Player) {
        val addGoldFeature = features.find { it is AddGold }
        if (addGoldFeature != null && addGoldFeature is AddGold) {
            player.gold += addGoldFeature.gold
        }
        val militaryFeature = features.find { it is Military }
        if (militaryFeature != null && militaryFeature is Military) {
            game.military += if (game.currentPlayer == 0) {
                militaryFeature.points
            } else {
                -militaryFeature.points
            }
            val activatedThresholds = game.militaryThresholds
                    .filter {
                        (it.player == 0 && game.military >= it.position)
                                || (it.player == 1 && -game.military >= it.position)
                    }
            activatedThresholds.forEach { game.opponent.gold -= it.gold }
            game.militaryThresholds.removeAll(activatedThresholds)
        }
    }

    fun Player.providedResources(): List<Resource> {
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
}