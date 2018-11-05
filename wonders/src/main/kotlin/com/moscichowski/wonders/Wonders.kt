package com.moscichowski.wonders
import com.moscichowski.wonders.model.*

interface CardProvider {
    fun getCard(): Card
}

class Wonders: CardProvider {
    override fun getCard(): Card {
        return firstEpoh.pop()
    }

    var game: Game
    private var cards: List<MutableList<Card>>

    var firstEpoh: MutableList<Card>

    constructor(wonders: List<Wonder>, cards: List<List<Card>>) {

        this.cards = cards.map { it.toMutableList() }
        this.firstEpoh = cards[0].toMutableList()
        if(cards.count() != 3) { throw WrongNumberOfCards(cards) }
        if(cards.find { it.count() != 20 } != null) { throw WrongNumberOfCards(cards) }
        if(wonders.count() != 8) { throw Requires8WondersError() }

        val hiddenIndexes = listOf(2, 3, 4, 9, 10, 11, 12, 13)

        val nodes = (0 until 20).map {
            val card = if (!hiddenIndexes.contains(it)) {
                firstEpoh.pop()
            } else {
                null
            }
            BoardNode(it, card)
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

        val board = Board(nodes)
        this.game = Game(wonders, cards, board)
    }

    fun takeAction(action: Action) {
        if (!game.state.canPerform(action)) { throw Error() }
        action.performOn(this)
    }
}

sealed class Action {
    abstract fun performOn(wonders: Wonders)
}

data class ChooseWonder(val wonderName: String) : Action() {
    override fun performOn(wonders: Wonders) {
        InitialWondersSelecter(wonders).selectWonder( this)
    }
}

data class BuildWonder(val card: Card, val wonder: Wonder, var param: Any? = null) : Action() {
    override fun performOn(wonders: Wonders) {
        WonderBuilder(wonders).buildWonder(this)
    }
}

data class TakeCard(val cardName: String) : Action() {
    override fun performOn(wonders: Wonders) {
        CardTaker(wonders).takeCard(this)
    }
}

data class ChooseScience(val token: ScienceToken) : Action() {
    override fun performOn(wonders: Wonders) {
        ScienceChooser(wonders).chooseScience(this)
    }
}

data class SellCard(val card: String) : Action() {
    override fun performOn(wonders: Wonders) {
        CardSeller(wonders).sellCard(this)
    }
}

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