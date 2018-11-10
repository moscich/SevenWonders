package com.moscichowski.wonders
import com.moscichowski.wonders.model.*

class WondersBuilder {
    fun setupWonders(wonders: List<Wonder>, cards: List<List<Card>>): Wonders {
        if(cards.count() != 3) { throw WrongNumberOfCards(cards) }
        if(cards.find { it.count() != 20 } != null) { throw WrongNumberOfCards(cards) }
        if(wonders.count() != 8) { throw Requires8WondersError() }

        return Wonders(Game(wonders.subList(0,4)), cards, wonders.subList(4, 8))
    }
}

class Wonders(var game: Game,
              private var _cards: List<List<Card>>,
              _wonders: List<Wonder>
              ) {

    var wonders = _wonders.toMutableList()
    var cards: List<MutableList<Card>> = _cards.map { it.toMutableList() }

    internal fun getCard(): Card {
        return cards[0].pop()
    }

    internal fun getWonders(): List<Wonder> {
        val wonders = this.wonders.map { it }
        this.wonders.clear()
        return wonders
    }

    internal fun buildBoard(): Board {

        val positions = listOf(
                BoardPosition(1,3),
                BoardPosition(1,4),
                BoardPosition(2,2),
                BoardPosition(2,3),
                BoardPosition(2,4),
                BoardPosition(3,2),
                BoardPosition(3,3),
                BoardPosition(3,4),
                BoardPosition(3,5),
                BoardPosition(4,1),
                BoardPosition(4,2),
                BoardPosition(4,3),
                BoardPosition(4,4),
                BoardPosition(4,5),
                BoardPosition(5,1),
                BoardPosition(5,2),
                BoardPosition(5,3),
                BoardPosition(5,4),
                BoardPosition(5,5),
                BoardPosition(5,6)
        )

        val hiddenIndexes = listOf(2, 3, 4, 9, 10, 11, 12, 13)

        val nodes = (0 until 20).map {
            val card = if (!hiddenIndexes.contains(it)) {
                getCard()
            } else {
                null
            }
            BoardNode(it, card, position = positions[it])
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

        return Board(nodes)
    }

    fun takeAction(action: Action) {
        if (!game.state.canPerform(action)) { throw Error() }
        action.performOn(this)
    }
}

data class BoardPosition(val row: Int, val column: Int) {

}

sealed class Action {
    abstract fun performOn(wonders: Wonders)
}

data class ChooseWonder(val wonderName: String) : Action() {
    override fun performOn(wonders: Wonders) {
        InitialWondersSelecter(wonders).selectWonder( this)
    }
}

data class BuildWonder(val wonder: String, val card: String, var param: Any? = null) : Action() {
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