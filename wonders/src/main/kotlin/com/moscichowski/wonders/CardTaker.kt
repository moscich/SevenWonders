package com.moscichowski.wonders

import com.moscichowski.wonders.model.*

class CardTaker(wonders: Wonders) : ActionPerformer(wonders) {
    private lateinit var game: Game
    private lateinit var card: Card
    override fun hasPromo(): Boolean {
        return game.doesCurrentPlayerHaveScience(ScienceToken.CONSTRUCTION) && card.color == CardColor.BLUE
    }

    override fun additionalMilitaryPoints(): Int {
        return if (game.doesCurrentPlayerHaveScience(ScienceToken.STRATEGY)) { 1 } else { 0 }
    }

    fun takeCard(action: TakeCard) {
        this.game = wonders.game
        this.card = game.board?.requestedCard(action.cardName) ?: throw CardUnavailable()

        val player = if (game.currentPlayer == 0) game.player1 else game.player2

        var requiredGold = required2(game, player, card.cost)

        if (player.hasFreeSymbol(card.freeSymbol)) {
            if (game.doesCurrentPlayerHaveScience(ScienceToken.CITY_PLANNING)) {
                player.gold += 4
            }
            requiredGold = 0
        }

        if (player.gold < requiredGold) {
            throw Error()
        }

        removeCardFromBoard(action.cardName)

        player.gold -= requiredGold

        if (doesOpponentHaveEconomy(game)) {
            game.opponent.gold += (requiredGold - card.cost.gold)
        }

        resolveCommonFeatures(game, card.features, player)

        if (player.alreadyHasThisScienceSymbol(card.features)) {
            game.state = GameState.CHOOSE_SCIENCE
        }

        player.cards.add(card)
        game.currentPlayer = (game.currentPlayer + 1) % 2
    }

}