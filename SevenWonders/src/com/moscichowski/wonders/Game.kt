package com.moscichowski.wonders

import kotlin.math.max
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

data class MilitaryThreashold(val player: Int,
                              val position: Int,
                              val gold: Int)

data class Game(val player1: Player,
                val player2: Player,
                val board: Board,
                var currentPlayer: Int = 0,
                var military: Int = 0,
                var state: GameState = GameState.REGULAR,
                val scienceTokens: MutableList<Pair<Int?, ScienceToken>> = mutableListOf(),
                val militaryThresholds: MutableList<MilitaryThreashold> = mutableListOf(
                        MilitaryThreashold(0, 3, 2),
                        MilitaryThreashold(0, 6, 5),
                        MilitaryThreashold(1, 3, 2),
                        MilitaryThreashold(1, 6, 5)
                )
) {
    val opponent: Player
        get() = if (currentPlayer == 1) {
            player1
        } else {
            player2
        }

    val player: Player
        get() = if (currentPlayer == 0) {
            player1
        } else {
            player2
        }

    fun doesCurrentPlayerHaveScience(science: ScienceToken): Boolean {
        return scienceTokens.find { it.first == currentPlayer && it.second == science } != null
    }

    fun victoryPointsForPlayer(playerNo: Int): Int {
        val player = if (playerNo == 0) {
            player1
        } else {
            player2
        }

        val pointsForGold = player.gold / 3

        val sciencePoints = scienceTokens.filter { it.first == playerNo }.fold(0) { res, token ->
            val points = when (token.second) {
                ScienceToken.AGRICULTURE -> 4
                else -> {
                    0
                }
            }
            res + points
        }

        val pointsForFeatures = player.features.fold(0) { res, feature ->
            res + victoryPointsForFeature(feature)
        }
        return pointsForFeatures + pointsForGold + sciencePoints
    }

    private fun victoryPointsForFeature(feature: CardFeature): Int {
        when (feature) {
            is VictoryPoints -> return feature.points
            is Guild -> {
                val playersResourceCards = player.cards.count { it.color == CardColor.SILVER || it.color == CardColor.BROWN }
                val opponentResourceCards = opponent.cards.count { it.color == CardColor.SILVER || it.color == CardColor.BROWN }
                return max(playersResourceCards, opponentResourceCards)
            }
        }
        return 0
    }
}

enum class GameState {
    REGULAR {
        override fun canPerform(action: Action): Boolean {
            return action !is ChooseScience
        }
    },
    CHOOSE_SCIENCE {
        override fun canPerform(action: Action): Boolean {
            return action is ChooseScience
        }
    };

    abstract fun canPerform(action: Action): Boolean
}

data class Player internal constructor(var gold_: Int) {

    var gold: Int by object : ObservableProperty<Int>(gold_) {
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            val positive = max(0, value)
            super.setValue(thisRef, property, positive)
        }
    }

    val cards: MutableList<Card> = mutableListOf()
    var wonders: List<Pair<Boolean, Wonder>> = listOf()

    val features: List<CardFeature>
        get() {
            val features = mutableListOf<CardFeature>()
            val wonderFeatures = wonders.filter { it.first }.flatMap { it.second.features }.toMutableList()
            val cardFeatures = cards.flatMap { it.features }
            features.addAll(cardFeatures)
            features.addAll(wonderFeatures)
            return features
    }

    fun resources(): Resource {
        return cards.flatMap { card -> card.features }.fold(Resource()) { sum, feature ->
            return if (feature is ProvideResource) {
                feature.resource
            } else {
                sum
            }
        }
    }

    fun goldCardsCount(): Int {
        return cards.filter { it.color == CardColor.GOLD }.count()
    }

    fun hasFreeSymbol(symbol: CardFreeSymbol?): Boolean {
        return cards.flatMap { it.features }.find { it is FreeSymbol && it.symbol == symbol } != null
    }

    fun alreadyHasThisScienceSymbol(features: List<CardFeature>): Boolean {
        val symbol = (features.find { it is Science } as? Science)?.science
        return cards.flatMap { it.features }.find { it is Science && it.science == symbol } != null
    }
}

data class Board(val cards_: List<BoardNode>) {
    val cards = cards_.toMutableList()
}

data class BoardNode(private val innerCard: Card, val descendants: MutableList<Card> = mutableListOf(), private val hidden: Boolean = false) {
    val card: Card?
        get() {
            return if (!hidden || descendants.isEmpty()) {
                innerCard
            } else {
                null
            }
        }

}

data class Resource(val wood: Int = 0,
                    val clay: Int = 0,
                    val stone: Int = 0,
                    val glass: Int = 0,
                    val papyrus: Int = 0,
                    val gold: Int = 0) {
    operator fun plus(sum: Resource): Resource {
        return Resource(wood = sum.wood + wood,
                clay = sum.clay + clay,
                stone = sum.stone + stone,
                glass = sum.glass + glass,
                papyrus = sum.papyrus + papyrus)
    }

    fun combine(resource: Resource): List<Resource> {
        val result = mutableListOf<Resource>()
        resource.decompose().filter { it != Resource() }.forEach {
            result.add(this + it)
        }
        return result
    }

    private fun decompose(): List<Resource> {
        return listOf(
                Resource(wood = wood),
                Resource(clay = clay),
                Resource(stone = stone),
                Resource(papyrus = papyrus),
                Resource(glass = glass),
                Resource(gold = gold))
    }
}

enum class CardFreeSymbol {
    SWORD
}

enum class ScienceSymbol {
    WHEEL
}

enum class ScienceToken {
    ENGINEERING, ARCHITECTURE, CONSTRUCTION, ECONOMY, STRATEGY, THEOLOGY, CITY_PLANNING, AGRICULTURE
}

data class Card(val name: String,
                val color: CardColor,
                val cost: Resource = Resource(),
                val features: List<CardFeature> = listOf(),
                val freeSymbol: CardFreeSymbol? = null)

enum class CardColor {
    BROWN, SILVER, GOLD, BLUE,

    RED,

    GREEN
}

enum class WarehouseType {
    WOOD {
        override fun cost(resource: Resource): Int {
            return resource.wood
        }
    },
    CLAY {
        override fun cost(resource: Resource): Int {
            return resource.clay
        }
    },
    STONE {
        override fun cost(resource: Resource): Int {
            return resource.stone
        }
    };

    abstract fun cost(resource: Resource): Int
}

sealed class CardFeature
data class ProvideResource(val resource: Resource) : CardFeature()
data class AddGold(val gold: Int) : CardFeature()
data class Military(val points: Int) : CardFeature()
data class FreeSymbol(val symbol: CardFreeSymbol) : CardFeature()
data class Science(val science: ScienceSymbol) : CardFeature()
data class Warehouse(val type: WarehouseType) : CardFeature()
data class GoldForColor(val color: CardColor) : CardFeature()
data class Guild(val temp: Int) : CardFeature()
data class VictoryPoints(val points: Int) : CardFeature()
object GoldForWonder : CardFeature()
object Customs : CardFeature()
object DestroyBrownCard : CardFeature()
object DestroySilverCard : CardFeature()
object ExtraTurn : CardFeature()
object RemoveGold : CardFeature()
object ProvideSilverResource : CardFeature()
object ProvideBrownResource : CardFeature()

data class Wonder(val name: String, val cost: Resource = Resource(), val features: List<CardFeature> = mutableListOf())