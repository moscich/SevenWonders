package com.moscichowski.wonders

import com.moscichowski.wonders.model.*
import kotlin.math.max
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

data class MilitaryThreshold(val player: Int,
                             val position: Int,
                             val gold: Int)

data class Game(private val _wonders: List<Wonder>,
                private val cards: List<List<Card>>,
                private var _board: Board = Board(listOf()),
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
    var board: Board
    get() {
        return if (state != GameState.WONDERS_SELECT) {
            _board
        } else {
            Board(listOf())
        }
    }
    set(value) {_board = value}

    init {
        if(cards.count() != 3) { throw WrongNumberOfCards(cards) }
        if(cards.find { it.count() != 20 } != null) { throw WrongNumberOfCards(cards) }
        if(_wonders.count() != 8) { throw Requires8WondersError() }

        val hiddenIndexes = listOf(2, 3, 4, 9, 10, 11, 12, 13)

        val nodes = cards.asSequence().first().mapIndexed { index, card ->
            val hidden = hiddenIndexes.contains(index)
            BoardNode(index, card, hidden = hidden)
        }

        val dependencies = listOf(
                Pair(2, 3),
                Pair(3, 4),
                Pair(5, 6),
                Pair(6, 7),
                Pair(7, 8),
                Pair(9, 10),
                Pair(10, 11),
                Pair(11, 12),
                Pair(12, 13),
                Pair(14, 15),
                Pair(15, 16),
                Pair(16, 17),
                Pair(17, 18),
                Pair(18, 19)

        )

        for (index in 0 until dependencies.count()) {
            val dependency = dependencies[index]
            nodes[index].descendants.add(nodes[dependency.first])
            nodes[index].descendants.add(nodes[dependency.second])
        }

        _board = Board(nodes)
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

data class BoardNode(val id: Int, private val innerCard: Card, val descendants: MutableList<BoardNode> = mutableListOf(), private val hidden: Boolean = false) {
    val card: Card?
        get() {
            return if (!hidden || descendants.isEmpty()) {
                innerCard
            } else {
                null
            }
        }

}

data class Wonder(var name: String, var cost: Resource = Resource(), var features: List<CardFeature> = mutableListOf()) {
    constructor() : this("") {

    }

}