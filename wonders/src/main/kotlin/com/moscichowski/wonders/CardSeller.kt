package com.moscichowski.wonders

class CardSeller(wonders: Wonders): ActionPerformer(wonders) {

    fun sellCard(action: SellCard) {
        val game = wonders.game
        val player = if (game.currentPlayer == 0) game.player1 else game.player2
        val card = game.board?.requestedCard(action.card) ?: throw Error()

        removeCardFromBoard(card.name)

        player.gold += 2 + player.yellowCardsCount()
        game.currentPlayer = if (game.currentPlayer == 0) { 1 } else { 0 }
    }
}