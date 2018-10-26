package com.moscichowski.wonders

class Wonders(val game: Game) {

    fun takeAction(action: Action) {
        if (!game.state.canPerform(action)) { throw Error() }
        action.performOn(game)
    }
}

sealed class Action {
    abstract fun performOn(game: Game)
}

data class ChooseWonder(val wonderName: String) : Action() {
    override fun performOn(game: Game) {
        InitialWondersSelecter().selectWonder(game, this)
    }
}

data class BuildWonder(val card: Card, val wonder: Wonder, var param: Any? = null) : Action() {
    override fun performOn(game: Game) {
        WonderBuilder().buildWonder(this, game)
    }
}

data class TakeCard(val cardName: String) : Action() {
    override fun performOn(game: Game) {
        CardTaker().takeCard(game, this)
    }
}

data class ChooseScience(val token: ScienceToken) : Action() {
    override fun performOn(game: Game) {
        ScienceChooser().chooseScience(game, this)
    }
}

data class SellCard(val card: Card) : Action() {
    override fun performOn(game: Game) {
        CardSeller().sellCard(game, this)
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