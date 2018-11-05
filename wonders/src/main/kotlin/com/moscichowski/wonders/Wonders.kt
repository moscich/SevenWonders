package com.moscichowski.wonders
import com.moscichowski.wonders.model.*

class WondersBuilder {
    fun setupWonders(wonders: List<Wonder>, cards: List<List<Card>>): Wonders {
        if(cards.count() != 3) { throw WrongNumberOfCards(cards) }
        if(cards.find { it.count() != 20 } != null) { throw WrongNumberOfCards(cards) }
        if(wonders.count() != 8) { throw Requires8WondersError() }

        return Wonders(Game(wonders), cards)
    }
}

class Wonders(var game: Game,
              private var cards: List<List<Card>>
              ) {


    lateinit var firstEpoh: MutableList<Card>

    internal fun getCard(): Card {
        return firstEpoh.pop()
    }

    internal fun buildBoard(): Board {
        this.firstEpoh = cards[0].toMutableList()

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

        return Board(nodes)
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