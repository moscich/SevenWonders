package com.moscichowski.wonders

import java.awt.Color

data class Game(val player1: Player,
                val player2: Player,
                val board: Board,
                var currentPlayer: Int = 0
)

data class Player internal constructor(var gold: Int) {
    val cards: MutableList<Card> = mutableListOf()
    var wonders: List<Pair<Boolean, Wonder>> = listOf()

    fun resources(): Resource {
        return cards.flatMap { card -> card.features }.fold(Resource()) { sum, feature ->
            return if (feature is ProvideResource) {
                feature.resource
            } else {
                sum
            }
        }
    }
}

data class Board(val cards: MutableList<BoardNode>)

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
}

data class Card(val name: String, val color: CardColor, val cost: Resource = Resource(), val features: List<CardFeature> = listOf())

enum class CardColor {
    BROWN, SILVER
}

sealed class CardFeature
data class ProvideResource(val resource: Resource) : CardFeature()
object WoodWarehouse : CardFeature()
object ClayWarehouse : CardFeature()
object StoneWarehouse : CardFeature()
object DestroyBrownCard : CardFeature()
object DestroySilverCard : CardFeature()
object ExtraTurn : CardFeature()
object ProvideSilverResource: CardFeature()