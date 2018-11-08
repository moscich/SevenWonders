package com.moscichowski.wonders

import com.moscichowski.wonders.model.*
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

data class MilitaryThreshold(val player: Int,
                             val position: Int,
                             val gold: Int)

data class Game(private val _wonders: List<Wonder>,
                var board: Board? = null,
                val player1: Player = Player(6),
                val player2: Player = Player(6),
                var currentPlayer: Int = 0,
                var military: Int = 0,
                var state: GameState = GameState.WONDERS_SELECT,
                val scienceTokens: MutableList<Pair<Int?, ScienceToken>> = mutableListOf(),
                private val _militaryThresholds: List<MilitaryThreshold> = mutableListOf(
                        MilitaryThreshold(0, 3, 2),
                        MilitaryThreshold(0, 6, 5),
                        MilitaryThreshold(1, 3, 2),
                        MilitaryThreshold(1, 6, 5)
                )
) {

    val militaryThresholds = _militaryThresholds.toMutableList()

    private var mutableWonders = _wonders.toMutableList()

    var wonders: List<Wonder>
    get() {
        return mutableWonders
    }
    internal set(value) {
        mutableWonders = value.toMutableList()
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

data class WonderPair(val built: Boolean,
                      val wonder: Wonder
                      )

data class Player (private var gold_: Int,
                   private val _cards: List<Card> = listOf(),
                   var wonders: List<WonderPair> = listOf()
) {

    val cards = _cards.toMutableList()

    var gold: Int by object : ObservableProperty<Int>(gold_) {
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            val positive = max(0, value)
            super.setValue(thisRef, property, positive)
        }
    }

    val features: List<CardFeature>
        get() {
            val features = mutableListOf<CardFeature>()
            val wonderFeatures = wonders.filter { it.built }.flatMap { it.wonder.features }.toMutableList()
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

fun<T> MutableList<T>.pop(): T {
    val elem = first()
    remove(elem)
    return elem
}

data class Board(private val cards_: List<BoardNode>) {
    val elements = cards_.toMutableList()
    fun requestedCard(name: String): Card? {
        return elements.find { it.card?.name == name && it.descendants.count() == 0 }?.card
    }
}

data class BoardNode(val id: Int, var card: Card?, val descendants: MutableList<BoardNode> = mutableListOf(), val position: BoardPosition, private val hidden: Boolean = false)