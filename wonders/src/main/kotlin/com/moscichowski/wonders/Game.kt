package com.moscichowski.wonders

import java.lang.Error
import kotlin.math.max
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

data class MilitaryThreshold(val player: Int,
                             val position: Int,
                             val gold: Int)

data class Game(val board: Board,
                private val _wonders: List<Wonder>,
                val player1: Player = Player(6),
                val player2: Player = Player(6),
                var currentPlayer: Int = 0,
                var military: Int = 0,
                var state: GameState = GameState.WONDERS_SELECT,
                val scienceTokens: MutableList<Pair<Int?, ScienceToken>> = mutableListOf(),
                val militaryThresholds: MutableList<MilitaryThreshold> = mutableListOf(
                        MilitaryThreshold(0, 3, 2),
                        MilitaryThreshold(0, 6, 5),
                        MilitaryThreshold(1, 3, 2),
                        MilitaryThreshold(1, 6, 5)
                )
) {
    init {
        if(_wonders.count() != 8) { throw Requires8WondersError() }
    }
    private val mutableWonders = _wonders.toMutableList()

    val wonders: List<Wonder>
    get() {
        return if (mutableWonders.count() > 4) {
            mutableWonders.subList(0, mutableWonders.count() - 4)
        } else {
            mutableWonders
        }
    }

    internal fun selectWonder(wonder: Wonder) {
        mutableWonders.remove(wonder)
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
                ScienceToken.PHILOSOPHY -> 7
                ScienceToken.MATHEMATICS ->
                    3 * scienceTokens.count { it.first == playerNo }
                else -> {
                    0
                }
            }
            res + points
        }

        val pointsForFeatures = player.features.fold(0) { res, feature ->
            res + victoryPointsForFeature(feature)
        }

        return pointsForFeatures + pointsForGold + sciencePoints + pointsForMilitary(playerNo)
    }

    private fun pointsForMilitary(player: Int): Int {
        val multiplier = if (player == 0) {
            1
        } else {
            -1
        }
        return when (military * multiplier) {
            in 1..2 -> 2
            in 3..5 -> 5
            in 5..10 -> 10
            else -> {
                0
            }
        }
    }

    private fun victoryPointsForFeature(feature: CardFeature): Int {
        return when (feature) {
            is VictoryPoints -> feature.points
            is Guild -> GuildFeatureResolver().pointsForGuild(feature, this)
            else -> 0
        }
    }
}

enum class GameState {
    WONDERS_SELECT {
        override fun canPerform(action: Action): Boolean {
            return action is ChooseWonder
        }
    },
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

data class Player (private var gold_: Int) {

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

    fun yellowCardsCount(): Int {
        return cards.filter { it.color == CardColor.YELLOW }.count()
    }

    fun hasFreeSymbol(symbol: CardFreeSymbol?): Boolean {
        return cards.flatMap { it.features }.find { it is FreeSymbol && it.symbol == symbol } != null
    }

    fun alreadyHasThisScienceSymbol(features: List<CardFeature>): Boolean {
        val symbol = (features.find { it is Science } as? Science)?.science
        return cards.flatMap { it.features }.find { it is Science && it.science == symbol } != null
    }
}

data class Board(private val cards_: List<BoardNode>) {
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
    ENGINEERING, ARCHITECTURE, CONSTRUCTION, ECONOMY, STRATEGY, THEOLOGY, CITY_PLANNING, AGRICULTURE, MATHEMATICS, PHILOSOPHY
}

data class Card(val name: String,
                val color: CardColor,
                val cost: Resource = Resource(),
                val features: List<CardFeature> = listOf(),
                val freeSymbol: CardFreeSymbol? = null)

enum class CardColor {
    BROWN, SILVER, YELLOW, BLUE,

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
data class Guild(val type: GuildType) : CardFeature()
data class VictoryPoints(val points: Int) : CardFeature()
object GoldForWonder : CardFeature()
object Customs : CardFeature()
object DestroyBrownCard : CardFeature()
object DestroySilverCard : CardFeature()
object ExtraTurn : CardFeature()
object RemoveGold : CardFeature()
object ProvideSilverResource : CardFeature()
object ProvideBrownResource : CardFeature()

enum class GuildType {
    YELLOW, BROWN_SILVER, WONDERS, BLUE, GREEN, GOLD, RED
}

data class Wonder(var name: String, var cost: Resource = Resource(), var features: List<CardFeature> = mutableListOf()) {
    constructor() : this("") {

    }

}